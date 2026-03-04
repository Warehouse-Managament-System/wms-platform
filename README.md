# WMS Platform

Warehouse Management System — B2B SaaS platform for warehouse space rental and logistics.

## Structure

This monorepo contains all WMS microservices as Gradle subprojects:

| Subproject | Description | Port |
|---|---|---|
| `wms-common` | Shared library (enums, entities, events, exceptions, security) | — |
| `identity-service` | User registration, authentication, owner approval | 8081 |
| `inventory-service` | Warehouse and goods management | 8082 |
| `reservation-service` | Booking, billing, payments (Stripe) | 8083 |
| `delivery-service` | Delivery scheduling and tracking | 8085 |
| `platform-service` | Notifications, reports, admin dashboards | 8087 |
| `api-gateway` | Spring Cloud Gateway, JWT validation | 8080 |
| `config-server` | Centralized configuration | 8888 |
| `eureka-server` | Service discovery | 8761 |

## Getting Started

```bash
# Start infrastructure (PostgreSQL, Kafka, Redis, Observability)
docker compose up -d

# Build all projects (also installs Git hooks)
./gradlew build
```

## Running Services

**All services at once:**

```bash
./gradlew bootRun --parallel
```

**Individually (start infrastructure services first):**

```bash
# 1. Infrastructure services
./gradlew :eureka-server:bootRun
./gradlew :config-server:bootRun

# 2. Gateway
./gradlew :api-gateway:bootRun

# 3. Business services
./gradlew :identity-service:bootRun
./gradlew :inventory-service:bootRun
./gradlew :reservation-service:bootRun
./gradlew :delivery-service:bootRun
./gradlew :platform-service:bootRun
```

## Development Workflow

### Code Formatting

All code is formatted with **Google Java Format** via **Spotless**. Formatting is enforced at every stage:

| Stage        | What Happens                                                        |
|--------------|---------------------------------------------------------------------|
| **On save**  | IntelliJ auto-formats using project code style                      |
| **On commit**| `pre-commit` hook runs `spotlessApply` and re-stages formatted files |
| **On push**  | `pre-push` hook runs `spotlessCheck` — blocks push if not formatted  |

```bash
./gradlew spotlessApply    # Format all files
./gradlew spotlessCheck    # Check formatting (no changes)
```

### Git Hooks

Git hooks are auto-installed on `./gradlew build`. They enforce:

| Hook          | Enforces                                                                                    |
|---------------|---------------------------------------------------------------------------------------------|
| `pre-commit`  | Branch naming, no secrets, no conflict markers, auto-formats Java                           |
| `commit-msg`  | Conventional Commits: `type(scope): description`                                            |
| `pre-push`    | Blocks direct/force pushes to `main`/`master`/`develop`, spotlessCheck, compileJava         |

### Branch Naming

```
<type>/<service>/<author>/<description>
```

Example: `feature/identity-service/ali/add-oauth2-login`

### Commit Messages

```
<type>(<scope>): <description>
```

Example: `feat(identity): add OAuth2 login flow`

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full contributor guide.

## Documentation

Full documentation is available in the [docs repo](https://github.com/Warehouse-Managament-System/WarehouseManagamentSystem).
