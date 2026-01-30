# Checklist CRUD Feature Implementation

## Actions for the Developer

No external configuration required. MongoDB is handled via Docker Compose and Testcontainers.

---

## Actions for the AI Editor

### Feature Summary

Implement a Checklist CRUD system with admin and user views. Admin manages checklist items (batch create/update) with
categories, ordering, status, and pre-check state. User sees a daily snapshot of active items and can toggle completion.
Admin changes only affect user checklist after a manual reset (next-day logic).

### Architecture

Hexagonal architecture with base package `spring.checklisit`:

```
src/main/java/spring/checklisit/
├── domain/
│   └── checklist/
│       ├── Category.java
│       ├── Status.java
│       ├── ChecklistItem.java
│       ├── UserChecklistItem.java
│       ├── UserChecklist.java
│       ├── ChecklistPort.java
│       ├── ChecklistUseCase.java
│       └── ResourceNotFoundException.java
└── infra/
    ├── config/
    │   └── OpenApiConfig.java
    ├── spi/
    │   └── db/
    │       └── checklist/
    │           └── MongoChecklistAdapter.java
    └── api/
        └── rest/
            ├── GlobalExceptionHandler.java
            └── checklist/
                ├── ChecklistResource.java
                ├── ChecklistItemDto.java
                └── UserChecklistDto.java

src/test/java/spring/checklisit/
├── domain/
│   └── checklist/
│       └── ChecklistUseCaseIntegrationTest.java
└── infra/
    └── api/
        └── rest/
            └── checklist/
                └── ChecklistResourceIntegrationTest.java
```

### API Endpoints

| Method | Endpoint                       | Description                    |
|--------|--------------------------------|--------------------------------|
| GET    | /checklist/items               | List all checklist items       |
| POST   | /checklist/items               | Create items (batch)           |
| PUT    | /checklist/items               | Update items (batch)           |
| DELETE | /checklist/items/{id}          | Delete item                    |
| GET    | /checklist                     | Get today's user checklist     |
| PATCH  | /checklist/{itemId}/complete   | Mark item complete             |
| PATCH  | /checklist/{itemId}/uncomplete | Mark item uncomplete           |
| POST   | /checklist/reset               | Reset/generate daily checklist |

### Technical Setup Instructions

1. Add `springdoc-openapi-starter-webmvc-ui:2.8.3` dependency to build.gradle

2. Create all folders as defined in architecture

3. Create domain enums and entities (non-tested):
    - `Category` enum: MORNING, AFTERNOON, NIGHT
    - `Status` enum: ACTIVE, INACTIVE
    - `ChecklistItem` entity: id, label, category, order, status, complete
    - `UserChecklistItem` embedded: itemId, label, category, order, complete
    - `UserChecklist` entity: id, date, items (list of UserChecklistItem)
    - `ResourceNotFoundException` exception class

4. Domain layer TDD - write failing tests first in `ChecklistUseCaseIntegrationTest`:
    - Test reset creates snapshot with only ACTIVE items
    - Test reset inherits complete value from ChecklistItem
    - Test user can toggle complete on UserChecklistItem

5. Implement to make domain tests pass:
    - `ChecklistPort` interface: findAll, findById, saveAll, deleteById for ChecklistItem; findByDate, save for
      UserChecklist
    - `ChecklistUseCase` service: admin batch operations, user operations, reset logic

6. REST API TDD - write failing tests first in `ChecklistResourceIntegrationTest`:
    - Test create items batch
    - Test list items
    - Test update items batch
    - Test delete item
    - Test validation rejects missing required fields (400)
    - Test get today's checklist
    - Test complete/uncomplete item
    - Test reset generates checklist with only ACTIVE items and inherited complete value

7. Implement to make REST tests pass:
    - `ChecklistItemDto`: id, label, category, order, status, complete with validation (all required except id)
    - `UserChecklistDto`: id, date, items list
    - `ChecklistResource` controller with all endpoints
    - `GlobalExceptionHandler` for ResourceNotFoundException (404) and validation errors (400)

8. Implement persistence:
    - `MongoChecklistAdapter` implementing ChecklistPort using Spring Data MongoDB

9. Create OpenAPI configuration:
    - `OpenApiConfig` with API info (title, description, version)

### Custom Instructions

- Never install additional libraries
- Strictly follow the provided instructions
- Follow plan in order, no skipping steps
- Always adapt to current project rules and structure
- Do all steps without asking
- Always start with package installation if necessary
- Use proper versions from package manager
- Respect conventions, like naming and existing code patterns
