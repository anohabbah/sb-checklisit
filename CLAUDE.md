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

# Package as executable JAR
./gradlew bootJar
```

## Local Development

Run the application with automatically starts MongoDB container using spring-boot-docker-compose:
```bash
./gradlew bootRun
```

The application can also be run via `TestChecklisitApplication` which automatically starts a MongoDB container using Testcontainers.

## Architecture

- **Framework**: Spring Boot 4.0.2 with Spring Cloud
- **Database**: MongoDB (via `spring-boot-starter-data-mongodb`)
- **Java Version**: 21 (enforced via toolchain)
- **Build System**: Gradle 9.3.0

### Package Structure

Base package: `spring.checklisit`.
Use Hexagonal Architecture structure:

```
src/main/java/spring/checklisit/
├── domain/
│   └── <domian_name>/
└── infra/
    ├── spi/
    │   └── db/
    │       └── <domian_name>/
    └── api/
        └── rest/
            └── <domian_name>/
```

- `<domian_name>` in this structure should be the feature domain object class name in lowercase.
- `infra/` sub package could be changed to reflect driving/driven port implementation technology

### Testing Strategy

- JUnit 5 with Spring Boot Test
- Testcontainers for MongoDB integration tests
- Test MongoDB container configured in `TestcontainersConfiguration.java` with `@ServiceConnection` for automatic connection wiring
