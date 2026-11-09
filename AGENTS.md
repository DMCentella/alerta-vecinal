# AGENTS.md

## Build & Run

There is **no parent/aggregator POM**. Each service is an independent Maven project. Build or run from the service subdirectory:

```bash
# Build individual service
cd <service> && mvn compile          # or: mvn package -DskipTests

# Run individual service
cd <service> && mvn spring-boot:run
```

**Startup order matters** (inter-service dependencies):
1. `incident-service` (8082) — central incident CRUD + WebSocket, others call it
2. `auth-service` (8081) — JWT login + token validation endpoint
3. `serenazgo-service` (8080) — calls incident-service and auth-service
4. `admin-service` (8083) — calls incident-service and auth-service

## Databases

MySQL 8+ on `localhost:3306`, user `root` / password `123456`. The SQL script at repo root creates all 4 databases, but `ddl-auto=update` auto-creates tables regardless.

| Service | Actual DB name (in `.properties`) | README says |
|---|---|---|
| auth-service | `alerta_auth_db` | `alerta_auth_db` |
| incident-service | `bd_alerta_incidentes` | `bd_alerta_incidentes` |
| serenazgo-service | `bd_alerta_serenazgo` | `serenazgo_bd` |
| admin-service | `bd_alerta_admin` | `bd_alerta_admin` |

## Testing

**Only `incident-service` has substantive tests.** The other services have minimal or no tests.

```bash
cd incident-service && mvn test        # JUnit 5 + AssertJ, uses H2 in-memory
cd serenazgo-service && mvn test       # bare @SpringBootTest context-load only
```

No Checkstyle, SpotBugs, PMD, or other static analysis is configured.

## Key Architecture

**Auth pattern — delegated validation.** Non-auth services do NOT validate JWTs locally. Each has a `TokenInterceptor` that calls `GET /api/auth/validate?token=xxx` on `auth-service` (port 8081) on every request. Auth-service is a single point of failure for authentication.

**JWT propagation.** `serenazgo-service` and `admin-service` have an `AppConfig` that creates a `RestTemplate` bean with an interceptor that copies the `Authorization` header from the incoming request to outgoing calls to `incident-service`.

**No shared library.** DTOs like `GenericResponseDto`, `IncidenteDTO`, `ErrorMessage` are duplicated in each service. If you add a new shared DTO, replicate it in each service that needs it.

## Code conventions

- **auth-service** uses a different groupId/package: `pe.edu.cibertec.alertavecinal` / `pe.edu.cibertec.authservice`. The other three use `com.alertavecinal`. This is intentional, not a bug — keep it.
- All responses are wrapped in `GenericResponseDto` → `{ "response": ... }`.
- `auth-service` entities use `@Getter` + `@Setter`; other services use `@Data` on entities.
- Dependency injection via `@RequiredArgsConstructor` with `final` fields (no `@Autowired`).

## Ports

| Service | Port |
|---|---|
| serenazgo-service | 8080 |
| auth-service | 8081 |
| incident-service | 8082 |
| admin-service | 8083 |

## Lombok

Lombok is used everywhere. The `maven-compiler-plugin` in each `pom.xml` has Lombok annotation processor paths. If builds fail with missing getters/setters, Lombok annotation processing may not be active.
