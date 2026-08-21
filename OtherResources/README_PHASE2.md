# Phase 2 — Service-to-Service Communication (Student ↔ Address)

This phase builds directly on Phase 1. If you haven't gone through the
Phase 1 README yet, do that first — this document assumes you already
understand Controller → Service → Repository → Database and what each
layer does.

## 1. Why Do Microservices Need to Communicate?

In Phase 1, Student Service and Address Service were completely blind to
each other. That's clean, but not very realistic — in a real system, a
"Student" logically **has** an address, and that address is somebody
else's responsibility (the Address Service).

Two ways to solve this:

1. **Duplicate address fields inside Student Service.** Simple, but now
   the same data lives in two places, owned by nobody in particular. If
   the address changes, who updates it? This breaks one of the core
   microservices principles: **each service owns its own data**.
2. **Student Service asks Address Service whenever it needs address data.**
   Address Service remains the single source of truth for addresses.
   Student Service just remembers a reference (`addressId`) and fetches
   details on demand.

We went with option 2 — this is the standard microservices pattern, and
it's why services need to **communicate** with each other over the network.

## 2. Which Service Calls Which?

```
Student Service  ──────calls──────►  Address Service
   (port 8081)      HTTP/REST           (port 8082)
```

The relationship is **one-directional** in this project:
- Student Service is the **caller** (the client).
- Address Service is the **callee** (just a normal REST API, unaware
  that Student Service even exists — it never calls back).

This is important: Address Service's code from Phase 1 is **completely
unchanged**. It doesn't know or care who is calling it — Postman,
Student Service, or anything else. That's the beauty of REST: any
service can be a client to any other service, just by knowing its URL.

## 3. How the Request Flows — Create Student

```
Postman
   │  POST /api/students
   │  { "studentName": "...", "address": { houseName, streetName, city, pincode } }
   ▼
Student Service — Controller
   ▼
Student Service — Service (StudentServiceImpl)
   │
   │  Step 1: calls AddressClient.createAddress(...)
   ▼
Student Service — AddressClient  ───HTTP POST───►  Address Service — Controller
                                                        ▼
                                                    Address Service — Service
                                                        ▼
                                                    Address Service — Repository
                                                        ▼
                                                    PostgreSQL (address table)
                                    ◄───returns addressId + full address───
   │
   │  Step 2: saves Student locally, storing only addressId
   ▼
Student Service — Repository ──► PostgreSQL (student table)
   │
   │  Step 3: builds combined response (student + full address)
   ▼
Postman
   receives: { studentId, studentName, address: { addressId, houseName, ... } }
```

Notice this is **two separate HTTP round trips** happening for one
Postman request: Postman → Student Service, and then internally,
Student Service → Address Service. If you watch both services' console
logs while testing, you'll see this happen in real time.

## 4. How the Address Is Created / Fetched

This is handled by a new class: `AddressClient` (in Student Service),
which wraps all calls to Address Service in one place:

| Method | Calls | Used by |
|---|---|---|
| `createAddress(...)` | `POST /api/addresses` | `createStudent()` |
| `getAddressById(...)` | `GET /api/addresses/{id}` | `getStudentById()`, `getAllStudents()` |
| `updateAddress(...)` | `PUT /api/addresses/{id}` | `updateStudent()` |

We use Spring's `RestTemplate` to make these calls — it works exactly
like Postman does: send a request, wait, get a response back. This is
the simplest way to make one service call another, with no extra
frameworks or infrastructure involved.

**What Student Service actually stores:** just `addressId` (a `Long`)
on the `Student` table — like a foreign key, except it points to a row
in a *completely different database/table* owned by a different service.
Whenever the client needs full address details, Student Service fetches
them fresh from Address Service rather than keeping a local copy.

## 5. Testing the Complete Flow with Postman

Import `postman/Microservices-Phase2.postman_collection.json`
(this replaces the Phase 1 collection — Create/Update Student now send
a **nested address object** instead of a plain string).

### Startup order matters here
Because `ddl-auto=create` rebuilds tables on every restart, and Student
Service needs to *call* Address Service, start them in this order:
1. `docker compose up -d` (PostgreSQL)
2. Start **Address Service** first (port 8082)
3. Start **Student Service** second (port 8081)

### Test sequence

**1. Create Student** → `POST http://localhost:8081/api/students`
```json
{
  "studentName": "Rahul Sharma",
  "address": {
    "houseName": "Sunrise Apartments",
    "streetName": "MG Road",
    "city": "Pune",
    "pincode": "411001"
  }
}
```
Response `201 Created`:
```json
{
  "studentId": 1,
  "studentName": "Rahul Sharma",
  "address": {
    "addressId": 1,
    "houseName": "Sunrise Apartments",
    "streetName": "MG Road",
    "city": "Pune",
    "pincode": "411001"
  }
}
```

**2. Get Student By Id** → `GET http://localhost:8081/api/students/1`
Same combined response — this time built by Student Service calling
`GET /api/addresses/1` on Address Service behind the scenes.

**3. Get All Students** → `GET http://localhost:8081/api/students`
Returns an array — Student Service calls Address Service **once per
student** in the list to build each entry.

**4. Update Student** → `PUT http://localhost:8081/api/students/1`
```json
{
  "studentName": "Rahul Sharma Updated",
  "address": {
    "houseName": "Sunrise Apartments",
    "streetName": "FC Road",
    "city": "Pune",
    "pincode": "411004"
  }
}
```
Internally calls `PUT /api/addresses/1` on Address Service to update
the existing address (not create a new one).

**5. Delete Student** → `DELETE http://localhost:8081/api/students/1`
Deletes the student row only. The linked address row in Address Service
is intentionally left behind — a known simplification for this
beginner project. (Cleaning this up properly is normally done with
async events or scheduled jobs, which is outside this project's scope.)

## 6. A Few Honest Trade-offs (Worth Understanding, Not Fixing Yet)

- **No error handling if Address Service is down.** If Address Service
  isn't running, `createStudent()` will throw an unhandled exception
  right now. In a real system you'd add try/catch and a fallback or
  retry — we're keeping Phase 2 focused purely on "how do services talk."
- **Orphaned addresses on delete**, as mentioned above.
- **RestTemplate is synchronous/blocking** — Student Service waits for
  Address Service to respond before continuing. This is the simplest
  mental model, but at scale, this waiting can add up. That's the kind
  of problem tools like message queues or async communication solve —
  intentionally out of scope for this project.

These aren't bugs — they're deliberate simplifications so you can focus
on the core concept: **one service calling another over REST, and each
service owning its own data.**

## 7. What's Next (Phase 3 preview)

Right now everything runs on your machine directly via `mvn spring-boot:run`,
with services finding each other at `localhost`. In Phase 3, we'll package
both services into Docker containers, and you'll see why `localhost`
stops working once services run in separate containers — and how to fix
that with Docker networking.