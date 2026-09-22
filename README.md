# Employee Management Service

A Spring Boot REST API for managing employee records, backed by MySQL (via JPA/Hibernate) and integrated with **Kafka** to publish employee lifecycle events (`EMPLOYEE_CREATED`, `EMPLOYEE_UPDATED`, `EMPLOYEE_DELETED`) that downstream services — like [NotificationService](../NotificationService) — can consume.

## Tech Stack

- **Java 17**, **Spring Boot 4.1.1**
- **Spring Web (MVC)** — REST API
- **Spring Data JPA** + **MySQL** — persistence
- **Spring for Apache Kafka** — event publishing
- **Lombok** — boilerplate reduction (`@Getter`/`@Setter`)
- **Maven** (with wrapper)

## Architecture

```
Client → EmployeeController → EmployeeService → EmployeeRepository → MySQL
                                     │
                                     └──► EmployeeKafkaProducer → Kafka topic: employee-events
```

- `GlobalExceptionHandler` (`@RestControllerAdvice`) converts domain exceptions into structured JSON error responses.
- Every create/update/delete operation also emits an `EmployeeEvent` record to Kafka, keyed by `employeeId` (so all events for one employee land on the same partition and stay ordered).

## API Endpoints

Base path: `/api/employees`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/employees` | Create a new employee. Fails with `409 Conflict` if the email already exists. Publishes `EMPLOYEE_CREATED`. |
| `GET` | `/api/employees/{id}` | Fetch a single employee by ID. `400 Bad Request` if not found. |
| `GET` | `/api/employees` | List all employees. |
| `PATCH` | `/api/employees/{id}` | Soft-delete: sets status to `REMOVED` (row is not physically deleted). Publishes `EMPLOYEE_DELETED`. |
| `PATCH` | `/api/employees/update/{id}` | Sets status to `ON_LEAVE`. Publishes `EMPLOYEE_UPDATED`. *(Note: this currently only toggles status — it doesn't accept a request body to update other fields.)* |

### Employee status lifecycle

`ACTIVE` → `ON_LEAVE` / `REMOVED` (enum also defines `INACTIVE`, `TERMINATED`, though no endpoint sets these yet).

### Sample request — create employee

```http
POST /api/employees
Content-Type: application/json

{
  "firstName": "Abhi",
  "lastName": "Kamarthi",
  "email": "abhi@example.com",
  "phoneNumber": 9876543210,
  "department": "Engineering",
  "designation": "Software Engineer",
  "salary": 850000.00,
  "joiningDate": "2024-01-15"
}
```

### Error response shape

```json
{
  "timestamp": "2026-09-22T10:15:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Employee Already Exists with Email : abhi@example.com",
  "path": "/api/employees"
}
```

## Kafka Event

Published to topic **`employee-events`**, keyed by `employeeId`:

```json
{
  "employeeId": 1,
  "name": "Abhi",
  "eventType": "EMPLOYEE_CREATED"
}
```

`eventType` is one of `EMPLOYEE_CREATED`, `EMPLOYEE_UPDATED`, `EMPLOYEE_DELETED`.

## Configuration

Set these environment variables before running (`application.properties` reads them):

```
USERNAME   # MySQL username
PASSWORD   # MySQL password
URL        # MySQL JDBC URL, e.g. jdbc:mysql://localhost:3306/employee_db
```

Kafka defaults to `localhost:9092` (`spring.kafka.bootstrap-servers`).

> **Note:** `application.properties` also defines `spring.kafka.consumer.*` properties, but this service has no `@KafkaListener` — those settings are unused here and can be removed, or were likely copy-pasted while wiring up the consumer side in NotificationService.

## Running locally

```bash
# 1. Start MySQL and Kafka (e.g. via Docker Compose)
# 2. Set env vars
export USERNAME=root
export PASSWORD=yourpassword
export URL=jdbc:mysql://localhost:3306/employee_db

# 3. Run
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api/employees`.

## Project Structure

```
src/main/java/com/abhi/employeemanagement/
├── controller/       EmployeeController — REST endpoints
├── service/          EmployeeService — business logic + event publishing
├── repository/       EmployeeRepository — Spring Data JPA
├── entity/           Employee entity, EmployeeStatus enum, request/response DTOs
├── kafka/            EmployeeEvent record, EmployeeKafkaProducer
├── exception/        EmployeeAlreadyExists, EmployeeDoesntExists
└── config/           GlobalExceptionHandler
```

## Possible Next Steps

- Allow `PATCH /update/{id}` to accept an `EmployeeRequest` body for full field updates, not just a status change.
- Add validation annotations (`@NotBlank`, `@Email`, etc.) on `EmployeeRequest`.
- Add pagination to `GET /api/employees`.
- Add integration tests for the controller/service layer.
