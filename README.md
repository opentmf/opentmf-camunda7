# pia-camunda-7
Camunda 7 with Spin, Incident Logger and OpenID auth for Keycloak

## Secure Endpoints
This pia-camunda-7 project uses pia-security to secure its exposed endpoints. 

The default pia-security definitions whitelists all engine-rest endpoints so that the external task clients can communicate with the camunda server without authentication, whereas for all other endpoints requiring ADMIN or read or write accesses. Please see [config-security.yml](src/main/resources/config-security.yml)

## Incident Logging
When a failed task has zero retry counts, it is an indident and this incident is logged in WARN level. 

## Request - Response Logging
In order to enable request - response logging, set the ollowing logging level to DEBUG. To cancel, set to INFO.

```xml
<logger name="org.glassfish.jersey.logging.LoggingFeature" level="DEBUG" />
```

## Building the Docker Image
You can build a local docker image with the following command:
```shell
mvn -P docker clean package
```

## Version History
- 21.0.0
  - Initial Version
- 22.0.0
  - Updates to Camunda 7.22.0