# opentmf-camunda7
Ann OpenTMF produced Spring Boot microservice that embeds the latest Camunda7 community edition with the public [Spin](https://docs.camunda.org/manual/latest/reference/spin/), and [OpenID auth for Keycloak](https://github.com/camunda-community-hub/camunda-platform-7-keycloak) plugins, as well as using OpenTMF's [Camunda7 Incident Logger](https://github.com/opentmf/camunda7-incident-logger), and [openid-rbac-security](https://github.com/opentmf/openid-rbac-security) framework to secure the API endpoints.

## Secure Endpoints
This camunda7-openid-microservice project uses OpenTMF's [openid-rbac-security](https://github.com/opentmf/openid-rbac-security) to secure its exposed endpoints.

The default openid-rbac-security configuration requires read or write access for GET, write access for POST, PUT, and DELETE endpoints. These defaults can be overridden. Please see [config-security.yml](src/main/resources/config-security.yml) for initial configuration.

## Workflow Variables Longer Than 4KB
With the help of the public [Spin](https://docs.camunda.org/manual/latest/reference/spin/) plugin, longer than 4KB workflow variables can be used. The Spin plugin is included in the camunda7-openid-microservice project by default.

## Incident Logging
OpenTMF's [Camunda7 Incident Logger](https://github.com/opentmf/camunda7-incident-logger) is used to write a log statement when a failed task has zero retry counts.

## JavaScript Script Tasks (GraalJS)
BPMN script tasks with `scriptFormat="javascript"` are evaluated by [GraalJS](https://github.com/oracle/graaljs), since Nashorn was removed from the JDK. The stock GraalJS JSR-223 bridge leaks one polyglot context per evaluation (nothing ever calls `Context.close()`, and the Truffle engine registry pins every context forever), which grows the old generation unboundedly under script-task load. This application therefore routes JavaScript evaluation through a context-closing engine facade that closes each polyglot context as soon as the script invocation (environment scripts plus user script) completes.

- Spin helpers such as `S(...)` keep working: the context stays open across the environment scripts and the user script of one invocation.
- Script results (`camunda:resultVariable`) that are plain JavaScript objects or arrays are copied into plain Java maps/lists before the context closes.
- Storing a raw JavaScript object **directly** via `execution.setVariable(...)` is discouraged: the value is serialized after the context has closed. Convert it first, e.g. `S(JSON.stringify(obj))` or a Java type.
- Two Micrometer counters, `opentmf.graaljs.contexts.created` and `opentmf.graaljs.contexts.closed`, expose the context lifecycle; in steady state their difference is 0.
- Rollback switch: set `OPENTMF_CAMUNDA_SCRIPT_CLOSING_GRAALJS=false` to restore the stock (leaking) engine behavior.

## Request - Response Logging
In order to enable request - response logging, set the following logging level to DEBUG. To cancel, set to INFO.

```xml
<logger name="org.glassfish.jersey.logging.LoggingFeature" level="DEBUG" />
```

## Use Camunda UIs Through OpenID Authentication
No need to setup users to access the Camunda7 user interfaces like Cockpit, Tasklist, and Admin. Just use Keycloak's OpenID authentication to access the UIs with the help of the [OpenID auth for Keycloak](https://github.com/camunda-community-hub/camunda-platform-7-keycloak) plugin.

## Deployment Configuration

The application is configured through environment variables. The tables below list the key variables; see [application.yml](src/main/resources/application.yml), [config-camunda.yml](src/main/resources/config-camunda.yml), and [config-security.yml](src/main/resources/config-security.yml) for the full set of defaults.

### General

| Environment Variable | Description | Default |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Comma-separated list of active Spring profiles. | — |
| `LOGGING_CONFIG` | Path to a custom Logback configuration file. | built-in default |
| `SERVER_FORWARD_HEADERS_STRATEGY` | Strategy for handling forwarded headers (`framework`, `native`, `none`). Set to `framework` when running behind a reverse proxy or Ingress. | `none` |
| `OPENTMF_CAMUNDA_SCRIPT_CLOSING_GRAALJS` | Close the GraalJS polyglot context after every JavaScript script invocation (leak fix). Set to `false` to restore the stock, leaking engine behavior. | `true` |

### Database

| Environment Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection URL. | `jdbc:postgresql://postgresql:5432/db?useUnicode=yes&characterEncoding=UTF-8&currentSchema=camunda7` |
| `SPRING_DATASOURCE_USERNAME` | Database user. | `camunda7` |
| `SPRING_DATASOURCE_PASSWORD` | Database password. | — |
| `SPRING_DATASOURCE_HIKARI_SCHEMA` | Schema used by the Camunda engine tables and the HikariCP connection pool. | `camunda7` |

### Keycloak / OpenID

| Environment Variable | Description | Default |
|---|---|---|
| `PLUGIN_IDENTITY_KEYCLOAK_KEYCLOAK_ISSUER_URL` | Server-to-server Keycloak realm URL. Used by the identity provider plugin to query users/groups and by Spring Security for token exchange, JWK Set retrieval, and user-info calls. | `http://keycloak.iam-dev.svc.cluster.local/realms/devtest` |
| `PLUGIN_IDENTITY_KEYCLOAK_KEYCLOAK_ADMIN_URL` | Keycloak Admin REST API URL for the realm. | `http://keycloak.iam-dev.svc.cluster.local/admin/realms/devtest` |
| `PLUGIN_IDENTITY_KEYCLOAK_CLIENT_ID` | OAuth2 client ID registered in Keycloak. | `xxx` |
| `PLUGIN_IDENTITY_KEYCLOAK_CLIENT_SECRET` | OAuth2 client secret. | `xxx` |
| `KEYCLOAK_URL_AUTH` | Browser-facing Keycloak realm URL. Used for the OAuth2 authorization redirect and the CSP `connect-src` header. Defaults to `PLUGIN_IDENTITY_KEYCLOAK_KEYCLOAK_ISSUER_URL`, so in standard deployments only the issuer URL needs to be set. Override this when browsers reach Keycloak at a different address than the application (e.g. Citrix, split-DNS, external Ingress). | same as `PLUGIN_IDENTITY_KEYCLOAK_KEYCLOAK_ISSUER_URL` |
| `OPENTMF_SECURITY_USER_CLAIM` | JWT claim used as the authenticated user's identity (e.g. `email`, `preferred_username`, `sub`). | `email` |
| `OPENTMF_SECURITY_AUTHORITIES_CLAIM` | JWT claim that carries the user's role/group list. | `groups` |

### Split-URL deployments

In environments where the browser-facing Keycloak address differs from the address reachable by the application (e.g. when accessing through Citrix or a corporate proxy), set both variables independently:

```yaml
# K8s service address — used for all server-to-server communication
PLUGIN_IDENTITY_KEYCLOAK_KEYCLOAK_ISSUER_URL: https://keycloak.iam.svc.cluster.local/realms/myRealm
# Browser-facing address — used for OAuth2 authorization redirects
KEYCLOAK_URL_AUTH: https://keycloak.internal.company.com/realms/myRealm
```

In standard deployments where a single URL is reachable from both the browser and the application, only `PLUGIN_IDENTITY_KEYCLOAK_KEYCLOAK_ISSUER_URL` needs to be set; `KEYCLOAK_URL_AUTH` inherits the same value automatically.

### Overriding Role Mappings
It is possible to override the default role mappings for specific endpoints. Given that we set:
- `SPRING_PROFILES_ACTIVE=test`
- `SPRING_CONFIG_ADDITIONAL_LOCATION=file:/application/`

the following configuration can be supplied in a file mounted on `/application/application-test.yml`. Of course different roles can be specified than the below example.

```yaml
---
opentmf:
  security:
    secure-endpoints:
      - method: GET
        path: /engine-rest/**
        roles:
          - reader
          - writer
          - admin
      - method: POST
        path: /engine-rest/**
        roles:
          - writer
          - admin
      - method: PUT
        path: /engine-rest/**
        roles:
          - writer
          - admin
      - method: DELETE
        path: /engine-rest/**
        roles:
          - writer
          - admin

      # Actuator Endpoints That Require Authorization
      - method: GET
        path: /actuator/env
        roles:
          - admin
      - method: GET
        path: /actuator/env/**
        roles:
          - admin
```

## AWS IAM Authentication Support

For deployments on AWS, a dedicated image variant is published with the `-aws` tag suffix (e.g. `opentmf-camunda7:24-aws`). This variant bundles the following runtime libraries:

| Library | Purpose |
|---|---|
| [AWS Advanced JDBC Wrapper](https://github.com/aws/aws-advanced-jdbc-wrapper) | IAM-based authentication for Amazon RDS / Aurora PostgreSQL — no database passwords needed |
| [AWS MSK IAM Auth](https://github.com/aws/aws-msk-iam-auth) | IAM-based authentication for Amazon MSK (Managed Kafka) |
| [AWS STS SDK](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/sts.html) | Required for IRSA (IAM Roles for Service Accounts) / WebIdentity credential resolution on EKS |

### Pulling the AWS variant

```bash
docker pull ghcr.io/opentmf/opentmf-camunda7:24-aws
```

All semver tags are available with the `-aws` suffix: `24.0.3-aws`, `24.0-aws`, `24-aws`.

### RDS IAM authentication example

Configure the AWS JDBC Wrapper as the datasource driver and let IAM handle credentials:

```yaml
spring:
  datasource:
    url: jdbc:aws-wrapper:postgresql://your-cluster.cluster-xxxx.eu-central-1.rds.amazonaws.com:5432/camunda7
    driver-class-name: software.amazon.jdbc.Driver
    hikari:
      data-source-properties:
        wrapperPlugins: iam
        targetDriverClassName: org.postgresql.Driver
    username: your_iam_db_user
    # no password — the wrapper obtains short-lived tokens via IAM
```

On EKS, attach an IAM role to the pod's service account (IRSA) and ensure the role has `rds-db:connect` permission on the database resource.

### Building a local AWS variant

```shell
mvn -Dmaven.test.skip -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -P docker,aws-iam clean package
```

Or directly with Docker:

```bash
docker build -f Dockerfile_release --build-arg MAVEN_PROFILES=repackage,aws-iam -t local/opentmf-camunda7:aws .
```

## Building a Local Docker Image
You can build a local docker image with the following command:
```shell
mvn -Dmaven.test.skip -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -P docker clean package
```

## Using a Public Docker Image
Please visit [GitHub Packages for opentmf-camunda7](https://github.com/orgs/opentmf/packages/container/package/opentmf-camunda7) for the released docker images.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for the full version history.
