# Changelog

All notable changes to this project are documented in this file.

## 24.0.3
- Adds AWS IAM authentication support via a dedicated `-aws` image variant
- Removes bundled OpenTelemetry Java agent from Docker images

## 24.0.2
- Fixes the manual expansion of `JAVA_TOOL_OPTIONS` in the Docker entrypoint

## 24.0.1
- Upgrades Spring Boot to 3.5.7
- Upgrades camunda-platform-7-keycloak to 7.24.0
- Bundles opentelemetry-javaagent.jar in the `/addons` folder of the Docker image
- Starts producing semver tags

## 24.0.0
- Upgrades Camunda7 embedded engine to Camunda 7.24 Community

## 23.0.2
- Started using GitHub Docker registry

## 23.0.1
- Enabled SSO

## 23.0.0
- Updates Camunda to 7.23.0 and Spring Boot 3.4.4
- The first open source version, replacing the private PiA libraries with the open-sourced OpenTMF libraries

## 22.0.6
- Updated pia-security to 1.0.7
- Minimized logging in default configuration
- Refined actuator endpoints related configuration
- Started requiring security on GET /actuator/env endpoints
- Specified additional roles to unsanitize GET /actuator/env data

## 22.0.5
- Updated pia-security to 1.0.6
- Changed project tagging format and prepended just "v"

## 22.0.4
- Updated camunda-incident-logger to 1.0.2
- Prepended "v7." to the project tagging format

## 22.0.3
- Updated pia-security version from 1.0.3 to 1.0.5
- Updated spring-boot version from 3.3.4 to 3.4.0
- Fix: default management server base path is now /
- Changed the project tagging format to just version

## 22.0.2
- Updated pia-security version from 1.0.2 to 1.0.3

## 22.0.1
- Updates to camunda-incident-logger 1.0.1
- Fix: Removed telemetry-reporter-activate property
- Decreased default value of historyTimeToLive to 92 days

## 22.0.0
- Updates to Camunda 7.22.0

## 21.0.0
- Initial version
