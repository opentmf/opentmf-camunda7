# the lightweight alpine does not support arm64
# hence another lightweight distro jammy for broader coverage
FROM eclipse-temurin:17-jre-jammy AS builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && \
    apt-get install -y curl iputils-ping procps rsync && \
    rm -rf /var/lib/apt/lists/* && \
    addgroup java && \
    adduser --ingroup java --disabled-password java
USER java
WORKDIR /application
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./
ENV SERVER_PORT=8080
EXPOSE $SERVER_PORT
ENV JVM_OPTS="-Duser.timezone=UTC"
ENV JVM_OPTS="${JVM_OPTS} -XX:InitialRAMPercentage=25.0"
ENV JVM_OPTS="${JVM_OPTS} -XX:MinRAMPercentage=25.0"
ENV JVM_OPTS="${JVM_OPTS} -XX:MaxRAMPercentage=50.0"
ENV JVM_OPTS="${JVM_OPTS} -Dserver.port=${SERVER_PORT}"
CMD java ${JVM_OPTS} org.springframework.boot.loader.launch.JarLauncher
