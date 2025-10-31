# opentmf-camunda7
Ann OpenTMF produced Spring Boot microservice that embeds the latest Camunda7 community edition with the public [Spin](https://docs.camunda.org/manual/latest/reference/spin/), and [OpenID auth for Keycloak](https://github.com/camunda-community-hub/camunda-platform-7-keycloak) plugins, as well as using OpenTMF's [Camunda7 Incident Logger](https://github.com/opentmf/camunda7-incident-logger), and [openid-rbac-security](https://github.com/opentmf/openid-rbac-security) framework to secure the API endpoints.

## Secure Endpoints
This camunda7-openid-microservice project uses OpenTMF's [openid-rbac-security](https://github.com/opentmf/openid-rbac-security) to secure its exposed endpoints.

The default openid-rbac-security configuration requires read or write access for GET, write access for POST, PUST, and DELETE endpoints. These defaults can be overridden. Please see [config-security.yml](src/main/resources/config-security.yml) for initial configuration.

## Workflow Variables Longer Than 4KB
With the help of the public [Spin](https://docs.camunda.org/manual/latest/reference/spin/) plugin, longer than 4KB workflow variables can be used. The Spin plugin is included in the camunda7-openid-microservice project by default.

## Incident Logging
OpenTMF's [Camunda7 Incident Logger](https://github.com/opentmf/camunda7-incident-logger) is used to write a log statement when a failed task has zero retry counts.

## Request - Response Logging
In order to enable request - response logging, set the following logging level to DEBUG. To cancel, set to INFO.

```xml
<logger name="org.glassfish.jersey.logging.LoggingFeature" level="DEBUG" />
```

## Use Camunda UIs Through OpenID Authentication
No need to setup users to access the Camunda7 user interfaces like Cockpit, Tasklist, and Admin. Just use Keycloak's OpenID authentication to access the UIs with the help of the [OpenID auth for Keycloak](https://github.com/camunda-community-hub/camunda-platform-7-keycloak) plugin.


## OpenTelemetry (opt-in)

This image bundles the [OpenTelemetry Java agent] at build time. It is **disabled by default** and can be turned on at runtime—no rebuild needed.

**Agent path:** `/addons/opentelemetry-javaagent.jar` (also available via `$OTEL_AGENT_PATH`)

### Quick start (Docker / Compose)

Enable the agent by setting `JAVA_TOOL_OPTIONS` (the JVM reads this automatically):

```yaml
services:
  camunda7:
    image: ghcr.io/opentmf/opentmf-camunda7:24
    environment:
      # Enable the agent (no rebuild required):
      JAVA_TOOL_OPTIONS: "-javaagent:/addons/opentelemetry-javaagent.jar"

      # Minimal OTel config (adjust to your setup):
      OTEL_SERVICE_NAME: "opentmf-camunda7"
      OTEL_ENABLED: true
      OTEL_EXPORTER_OTLP_ENDPOINT: http://otel-collector:4318
      OTEL_EXPORTER_OTLP_PROTOCOL: http/protobuf
      OTEL_TRACES_EXPORTER: otlp
      OTEL_LOGS_EXPORTER: otlp
      OTEL_METRICS_EXPORTER: otlp
      OTEL_RESOURCE_ATTRIBUTES: "deployment.environment=dev,service.namespace=opentmf"
```

**Plain docker run:**
```bash
docker run --rm \
       -e JAVA_TOOL_OPTIONS="-javaagent:/addons/opentelemetry-javaagent.jar" \
       -e OTEL_SERVICE_NAME=opentmf-camunda7 \
       -e OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317 \
       -e OTEL_EXPORTER_OTLP_PROTOCOL=grpc \
       ghcr.io/opentmf/opentmf-camunda7:24
```

### Kubernetes example (Deployment)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: camunda7
spec:
  replicas: 1
  selector:
    matchLabels: { app: camunda7 }
  template:
    metadata:
      labels: { app: camunda7 }
    spec:
      containers:
        - name: camunda7
          image: ghcr.io/opentmf/opentmf-camunda7:YOUR_TAG
          env:
            - name: JAVA_TOOL_OPTIONS
              value: "-javaagent:/addons/opentelemetry-javaagent.jar"
            - name: OTEL_SERVICE_NAME
              value: "opentmf-camunda7"
            - name: OTEL_EXPORTER_OTLP_ENDPOINT
              value: "http://otel-collector:4317"
            - name: OTEL_EXPORTER_OTLP_PROTOCOL
              value: "grpc"
            - name: OTEL_RESOURCE_ATTRIBUTES
              value: "deployment.environment=prod,service.namespace=opentmf"
```

### Common configuration

- **Service name:** `OTEL_SERVICE_NAME=opentmf-camunda7`
- **Collector endpoint:**
    - gRPC: `OTEL_EXPORTER_OTLP_ENDPOINT=http://<collector>:4317` + `OTEL_EXPORTER_OTLP_PROTOCOL=grpc`
    - HTTP/Protobuf: `...:4318` + `OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf`
- **Resources/labels:** `OTEL_RESOURCE_ATTRIBUTES=deployment.environment=dev,service.namespace=opentmf`
- **Sampling (optional):**
  ```bash
  OTEL_TRACES_SAMPLER=parentbased_traceidratio
  OTEL_TRACES_SAMPLER_ARG=0.1   # 10% sampling
  ```
- **Propagators (optional):** `OTEL_PROPAGATORS=tracecontext,baggage` (default) or `b3` if you need Zipkin/B3.

### Disable or limit signals

- Turn everything off quickly: `OTEL_SDK_DISABLED=true`
- Traces only:
  ```bash
  OTEL_METRICS_EXPORTER=none
  OTEL_LOGS_EXPORTER=none
  ```

### Advanced: override agent version at build time

The Dockerfiles download the agent using an overridable build arg:

```bash
# for example:
docker build -f Dockerfile_release  \
       --build-arg OTEL_AGENT_VERSION=2.21.0 \
       -t ghcr.io/opentmf/opentmf-camunda7:otel-2.21.0 .
```

[OpenTelemetry Java agent]: https://github.com/open-telemetry/opentelemetry-java-instrumentation

## Building a Local Docker Image
You can build a local docker image with the following command:
```shell
mvn -Dmaven.test.skip -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -P docker clean package
```

## Using a Public Docker Image
Please visit [GitHub Packages for opentmf-camunda7](https://github.com/orgs/opentmf/packages/container/package/opentmf-camunda7) for the released docker images.

## Version History
### 21.0.0
  - Initial Version
### 22.0.0
  - Updates to Camunda 7.22.0
### 22.0.1
  - Updates to camunda-incident-logger 1.0.1
  - Fix: Removed telemetry-reporter-activate property.
  - Decreased default value of historyTimeToLive to 92 days
### 22.0.2
  - Updated pia-security version from 1.0.2 to 1.0.3
### 22.0.3
  - Updated pia-security version from 1.0.3 to 1.0.5
  - Updated spring-boot version from 3.3.4 to 3.4.0
  - fix: default management server base path is now /
  - changed the project tagging format to just version
### 22.0.4
  - Updated camunda-incident-logger to 1.0.2
  - Prepended "v7." to the project tagging format
### 22.0.5
  - Updated pia-security to 1.0.6
  - Changed project tagging format and prepended just "v"
### 22.0.6
  - Updated pia-security to 1.0.7
  - Minimized logging in default configuration
  - Refined actuator endpoints related configuration
  - Started requiring security on GET /actuator/env endpoints.
  - Specified additional roles to unsanitize GET /actuator/env data
### 23.0.0
  - Updates Camunda to 7.23.0 and Spring Boot 3.4.4
  - The first open source version, replacing the private PiA libraries with the open-sourced OpenTMF libraries.
### 23.0.1
  - Enabled SSO
### 23.0.2
  - Started using github docker registry
### 24.0.0
  - Upgrades Camunda7 embedded engine to Camunda 7.24 Community
### 24.0.1
  - Upgrades Spring Boot to 3.5.7
  - Upgrades camunda-platform-7-keycloak.version to 7.24.0
  - Adds opentelemetry-javaagent.jar to /addons folder of the docker image.
  - Starts producing semver tags
### 24.0.2
  - Fixes the manual expansion of `JAVA_TOOL_OPTIONS` in the Docker entrypoint
