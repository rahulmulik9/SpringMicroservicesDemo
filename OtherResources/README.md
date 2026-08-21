# Phase 1 — Independent Microservices (Student Service & Address Service)

## 1. What is a Microservice?

Instead of building one giant application ("monolith") that does everything,
we split it into small, independent applications — each with its own job,
own codebase, own database tables, and own port.

In this project:
- **Student Service** (port `8081`) → manages student data.
- **Address Service** (port `8082`) → manages address data.

Right now, in Phase 1, these two services **do not know about each other**.
Each one is a complete, self-contained mini-application. That's the whole
point of this phase: prove that a microservice can stand entirely on its own.

## 2. The Layered Structure — What Each Folder Does

Both services follow the exact same folder structure. Here's what each layer is responsible for:

| Layer | Folder | Responsibility |
|---|---|---|
| **Controller** | `controller/` | The "front door". Receives HTTP requests (GET/POST/PUT/DELETE) and returns HTTP responses. Contains no business logic. |
| **DTO** | `dto/` | "Data Transfer Objects" — define the exact shape of data going in (`RequestDTO`) and out (`ResponseDTO`), plus a standard error shape (`ErrorResponseDTO`). We never expose the database Model directly to clients. |
| **Model** | `model/` | The JPA `@Entity` class — represents a row in the database table. |
| **Repository** | `repository/` | Talks directly to the database. Extends `JpaRepository`, so basic CRUD methods (`save`, `findById`, `findAll`, `delete`) come for free. |
| **Service** | `service/` | The "brain" — contains business logic, converts between DTOs and Models, and calls the Repository. |
| **Exception** | `exception/` | Custom exceptions (e.g. `StudentNotFoundException`) and a `GlobalExceptionHandler` that turns errors into a clean JSON response instead of a stack trace. |

## 3. How a Request Flows Through the Layers

Example: creating a student via `POST /api/students`

```
Postman
   │  sends JSON: { "studentName": "...", "address": "..." }
   ▼
Controller  (StudentController)
   │  receives the request, validates the body, calls the Service
   ▼
Service  (StudentServiceImpl)
   │  converts RequestDTO → Model (Student), applies business logic
   ▼
Repository  (StudentRepository)
   │  saves the Model into the PostgreSQL database
   ▼
Service
   │  converts the saved Model → ResponseDTO
   ▼
Controller
   │  wraps ResponseDTO in an HTTP response (status 201 Created)
   ▼
Postman
   receives the JSON response
```

Every request (GET, PUT, DELETE) follows this same one-directional flow:
**Controller → Service → Repository → Database**, and the response travels
back up the same chain.

## 4. Why Split Into DTOs Instead of Using the Model Directly?

- The **Model** (`Student.java`) mirrors the database table exactly.
- The **DTOs** define the public "contract" of the API.
- This separation means you can change your database structure later
  without necessarily breaking the API that clients depend on — and vice versa.

## 5. How Each Service Works Independently

- Each service has its **own** `application.properties`, its own port, and
  runs as a **separate Java process** (`mvn spring-boot:run` in each folder).
- Both currently point at the same PostgreSQL database (`microserviceDemo`)
  but manage **completely different tables** (`student` and `address`) and
  never call each other's code or APIs.
- You could stop the Address Service entirely, and the Student Service
  would keep working perfectly fine (and vice versa). That's the core
  promise of microservices: independent deployability.

## 6. Running Everything

### Step 1 — Start PostgreSQL
```bash
docker compose up -d
```
This starts a PostgreSQL container with database `microserviceDemo`, user `root`, password `root`, exposed on port `5432`.

### Step 2 — Run Student Service
```bash
cd student-service
mvn spring-boot:run
```
Runs on `http://localhost:8081`

### Step 3 — Run Address Service
```bash
cd address-service
mvn spring-boot:run
```
Runs on `http://localhost:8082`

## 7. Testing with Postman

1. Open Postman → **Import** → select `postman/Microservices-Phase1.postman_collection.json`.
2. You'll see two folders: **Student Service** and **Address Service**, each with 5 requests (Create, Get by Id, Get All, Update, Delete).

### Student Service — Example flow
1. **Create Student** → `POST http://localhost:8081/api/students`
   ```json
   { "studentName": "Rahul Sharma", "address": "Pune, Maharashtra" }
   ```
   Response: `201 Created`
   ```json
   { "studentId": 1, "studentName": "Rahul Sharma", "address": "Pune, Maharashtra" }
   ```
2. **Get Student By Id** → `GET http://localhost:8081/api/students/1` → returns the same object.
3. **Get All Students** → `GET http://localhost:8081/api/students` → returns a JSON array.
4. **Update Student** → `PUT http://localhost:8081/api/students/1` with a new body → returns the updated object.
5. **Delete Student** → `DELETE http://localhost:8081/api/students/1` → returns a plain confirmation message.
6. Try **Get Student By Id** again with an id that doesn't exist → you'll get a `404 Not Found` with a clean JSON error body, thanks to `GlobalExceptionHandler`.

### Address Service — Example flow
Same pattern, on port `8082`:
```json
{ "houseName": "Sunrise Apartments", "streetName": "MG Road", "city": "Pune", "pincode": "411001" }
```

## 8. What's Next (Phase 2 preview)

Right now, when you create a Student, the `address` field is just a plain
text string — it has nothing to do with the Address Service. In Phase 2,
we'll change this so the Student Service actually **calls** the Address
Service over REST to create/fetch structured address data, and we'll
explain exactly why and how that works.
