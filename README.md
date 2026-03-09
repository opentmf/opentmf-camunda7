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
