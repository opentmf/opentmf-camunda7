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
