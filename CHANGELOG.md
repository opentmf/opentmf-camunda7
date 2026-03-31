# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [24.0.5] - 2026-03-31

### Fixed
- Fix Docker image generation by adding the missing `repackage` goal in Maven configuration.

## [24.0.4] - 2026-03-31

### Changed
- Upgrade Spring Boot to 3.5.13
- Remove `issuer-uri` from Spring Security OAuth2 provider configuration to skip
  OIDC issuer validation on ID tokens, allowing the token's `iss` claim to differ
  from the server-to-server Keycloak address

### Added
- Introduce `keycloak.url.auth` property for the browser-facing Keycloak URL,
  enabling split-URL deployments where browsers reach Keycloak at a different
  address than the application (e.g. Citrix, split-DNS, external Ingress);
  defaults to `plugin.identity.keycloak.keycloak-issuer-url` so existing
  single-URL deployments are unaffected

## [24.0.3]

### Added
- AWS IAM authentication support via a dedicated `-aws` image variant

### Removed
- Bundled OpenTelemetry Java agent from Docker images

## [24.0.2]

### Fixed
- Manual expansion of `JAVA_TOOL_OPTIONS` in the Docker entrypoint

## [24.0.1]

### Changed
- Upgrade Spring Boot to 3.5.7
- Upgrade camunda-platform-7-keycloak to 7.24.0

### Added
- Bundle opentelemetry-javaagent.jar in the `/addons` folder of the Docker image
- Produce semver tags

## [24.0.0]

### Changed
- Upgrade Camunda7 embedded engine to Camunda 7.24 Community

## [23.0.2]

### Changed
- Switch to GitHub Docker registry

## [23.0.1]

### Added
- SSO support

## [23.0.0]

### Changed
- Update Camunda to 7.23.0 and Spring Boot 3.4.4
- First open source version, replacing the private PiA libraries with the open-sourced OpenTMF libraries

## [22.0.6]

### Changed
- Update pia-security to 1.0.7
- Minimize logging in default configuration
- Refine actuator endpoints related configuration
- Require security on GET /actuator/env endpoints
- Specify additional roles to unsanitize GET /actuator/env data

## [22.0.5]

### Changed
- Update pia-security to 1.0.6
- Change project tagging format and prepend just "v"

## [22.0.4]

### Changed
- Update camunda-incident-logger to 1.0.2
- Prepend "v7." to the project tagging format

## [22.0.3]

### Changed
- Update pia-security version from 1.0.3 to 1.0.5
- Update spring-boot version from 3.3.4 to 3.4.0
- Change the project tagging format to just version

### Fixed
- Default management server base path is now /

## [22.0.2]

### Changed
- Update pia-security version from 1.0.2 to 1.0.3

## [22.0.1]

### Changed
- Update camunda-incident-logger to 1.0.1
- Decrease default value of historyTimeToLive to 92 days

### Fixed
- Remove telemetry-reporter-activate property

## [22.0.0]

### Changed
- Update to Camunda 7.22.0

## [21.0.0]

### Added
- Initial version
