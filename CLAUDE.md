# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "spring.checklisit.ChecklisitApplicationTests"

# Run a single test method
./gradlew test --tests "spring.checklisit.ChecklisitApplicationTests.contextLoads"

# Clean build
./gradlew clean build
```

## Local Development

Run the application which automatically starts MongoDB container using spring-boot-docker-compose:
```bash
./gradlew bootRun
```

The application can also be run via `TestChecklisitApplication` which automatically starts a MongoDB container using Testcontainers.

## Architecture

- **Framework**: Spring Boot 4.0.2
- **Database**: MongoDB (via `spring-boot-starter-data-mongodb`)
- **Java Version**: 21 (enforced via toolchain)
- **Build System**: Gradle 9.3.0

### Hexagonal Architecture

Base package: `spring.checklisit`

```
src/main/java/spring/checklisit/
├── domain/
│   └── <domain_name>/
│       ├── <Entity>.java           # Domain entities
│       ├── <UseCase>.java          # Business logic (@Service)
│       ├── <Port>.java             # Port interface (driven)
│       └── <Exception>.java        # Domain exceptions
└── infra/
    ├── spi/
    │   └── db/
    │       └── <domain_name>/
    │           └── <Adapter>.java  # Database adapter implements Port
    └── api/
        └── rest/
            ├── config/             # Cross-cutting REST config
            └── <domain_name>/
                ├── <Resource>.java # REST controller (driving adapter)
                └── <Dto>.java      # Request/Response DTOs
```

- `<domain_name>` is the feature domain name in lowercase (e.g., `checklist`)
- `domain/` contains pure business logic with no infrastructure dependencies
- `infra/spi/` contains driven adapters (outbound: database, external services)
- `infra/api/` contains driving adapters (inbound: REST, messaging)
- DTOs use static `fromEntity()` and `toEntity()` methods for mapping

### API Conventions

- REST controllers use `@RestController` with `@Validated` for bean validation
- Error responses follow RFC 7807 Problem Details (`spring.mvc.problemdetails.enabled=true`)
- Custom exceptions extend domain-specific exceptions (e.g., `ResourceNotFoundException`)
- `GlobalExceptionHandler` extends `ResponseEntityExceptionHandler` for custom problem details

### Testing Strategy

- JUnit 5 with Spring Boot Test
- Testcontainers for MongoDB integration tests
- Test MongoDB container configured in `TestcontainersConfiguration.java` with `@ServiceConnection` for automatic connection wiring
