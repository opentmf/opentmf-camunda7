# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [24.0.6] - 2026-07-05

### Fixed
- Memory leak in JavaScript script tasks: the GraalJS JSR-223 bridge creates a new
  polyglot context per script evaluation (two per evaluation in practice) and never
  closes it, while the Truffle engine registry keeps a strong reference to every
  context ever created. The old generation grew monotonically (~0.36 MiB per process
  instance; a heap dump after ~4,200 instances showed 8,486 retained contexts
  totalling ≈1.2 GiB) and a full GC reclaimed nothing. JavaScript script evaluation
  now goes through a context-closing engine facade that closes the polyglot context
  as soon as the script invocation completes. Set
  `opentmf.camunda.script.closing-graaljs: false` to restore the previous behavior.

### Changed
- Upgrade Spring Boot to 3.5.16
- Upgrade GraalJS to 25.1.3
- Docker images now run on Eclipse Temurin JRE 25 (Camunda 7.24 and Spring Boot
  3.5.16 both support Java 25). This matches the GraalJS/Truffle 25.x runtime
  requirement and removes the version-mismatch warnings at startup. JavaScript
  script tasks still run interpreted — as of GraalVM 25, in-process JIT of guest
  code requires a GraalVM JDK — which is the same execution mode as before; the
  now-intentional interpreter notice is suppressed. Compiled bytecode target
  remains Java 17
- Script engines are always resolved through the process engine (resolution through
  the process application is disabled); equivalent in this single-classloader
  Spring Boot deployment, and required so that the context-closing facade covers
  process-application deployments too

### Added
- Micrometer counters `opentmf.graaljs.contexts.created` and
  `opentmf.graaljs.contexts.closed` for observing GraalJS context lifecycle; in
  steady state their difference is 0
- Local `sonar` Maven profile for analyzing the project on a developer-managed
  SonarQube at `http://localhost:9000` (`mvn -P sonar clean verify`)

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
