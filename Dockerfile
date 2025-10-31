# Download OpenTelemetry Java agent
ARG OTEL_AGENT_VERSION=2.21.0
FROM alpine:3.20 AS otel-agent
ARG OTEL_AGENT_VERSION
RUN apk add --no-cache curl \
    && curl -fsSL -o /opentelemetry-javaagent.jar \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar"

# the lightweight alpine does not support arm64
# hence another lightweight distro jammy for broader coverage
FROM eclipse-temurin:17-jre-jammy AS builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && \
    apt-get install -y curl jq iputils-ping procps rsync && \
    rm -rf /var/lib/apt/lists/* && \
    addgroup java && \
    adduser --ingroup java --disabled-password java
USER java
WORKDIR /application
COPY --chown=java:java --from=builder /application/dependencies/ ./
COPY --chown=java:java --from=builder /application/spring-boot-loader/ ./
COPY --chown=java:java --from=builder /application/snapshot-dependencies/ ./
COPY --chown=java:java --from=builder /application/application/ ./

## copy the agent into the final image without keeping curl
COPY --chown=java:java --from=otel-agent /opentelemetry-javaagent.jar /addons/opentelemetry-javaagent.jar
ENV OTEL_AGENT_PATH=/addons/opentelemetry-javaagent.jar

ENV SERVER_PORT=8080
ENV DEBUG_PORT=5005
EXPOSE $SERVER_PORT
EXPOSE ${DEBUG_PORT}

ENV JVM_OPTS="-Duser.timezone=UTC -Dserver.port=${SERVER_PORT}"
ENV DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"

ENTRYPOINT ["sh","-lc","exec java ${DEBUG_OPTS} ${JVM_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
CMD []
