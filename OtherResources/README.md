# Microservices Notes — Service Discovery with Eureka + Feign Client

> Evolution: Phase 1 (independent services) → Phase 2 (RestTemplate) → **Phase 2.5 (Eureka + Feign)**

---

## 1. Why We Moved Away from RestTemplate

**RestTemplate** worked, but it had one weakness: Student Service needed to know Address Service's *exact* address, hardcoded in `application.properties`:

```properties
address-service.url=http://localhost:8082
```

This is fine on one machine, but breaks down in real systems where:
- Multiple copies (instances) of a service might run, on different ports/machines
- Services get restarted, scaled up/down, or moved
- You don't want to manually update a URL every time something changes

**The fix:** stop hardcoding *where* a service lives. Instead, ask a central directory "where is Address Service right now?" That directory is **Eureka**.

---

## 2. Core Concepts & Terminology

| Term | Meaning |
|---|---|
| **Service Registry** | A central directory that tracks which services are running and where. Eureka Server plays this role. |
| **Service Registration** | A service announcing itself to Eureka on startup: "I'm ADDRESS-SERVICE, running at this host:port." |
| **Service Discovery** | A service *asking* Eureka: "Where is ADDRESS-SERVICE right now?" instead of hardcoding a URL. |
| **Eureka Server** | The registry application itself. A separate, third Spring Boot app (ours runs on port `8761`). |
| **Eureka Client** | A small dependency added to Student Service and Address Service so they can register with Eureka and stay "alive" via heartbeats. |
| **Heartbeat / Lease** | Periodic "I'm still alive" signal each service sends to Eureka. If it stops, Eureka removes that instance after a timeout. |
| **Feign Client** | A *declarative* REST client — you write a Java **interface**, not manual HTTP-calling code. Feign generates the actual implementation at startup. |
| **Spring Cloud LoadBalancer** | Resolves a service *name* (e.g. `ADDRESS-SERVICE`) into a real, current URL using Eureka's registry — used internally by Feign. |
| **Spring Cloud BOM** (`dependencyManagement`) | A version-locking mechanism ensuring all `spring-cloud-starter-*` dependencies use versions compatible with your Spring Boot version. |

---

## 3. Architecture — Three Applications Now

```
┌─────────────────────┐
│   Eureka Server      │  ← port 8761 — the registry (new, 3rd app)
│  (registry/directory) │
└──────────┬───────────┘
           │  registers with ▲        ▲  registers with
           │                 │        │
┌──────────▼──────┐   ┌──────┴────────▼─────┐
│  Student Service  │   │   Address Service    │
│    port 8081       │   │    port 8082          │
│ (Eureka Client +   │──►│  (Eureka Client only) │
│  Feign Client)     │Feign  calls by name    │
└─────────────────────┘   └───────────────────────┘
```

- Eureka Server doesn't call anyone — it just holds the directory.
- Both services **register themselves** with Eureka on startup.
- Student Service also uses **Feign** to *look up and call* Address Service — by name, not hardcoded URL.

---

## 4. Dependencies Added (by project)

### Eureka Server (new project)
| Dependency | Purpose |
|---|---|
| `spring-cloud-starter-netflix-eureka-server` | Turns this app into the registry itself |

Plus the Spring Cloud BOM in `pom.xml`:
```xml
<properties>
    <spring-cloud.version>2025.1.1</spring-cloud.version> <!-- or 2025.1.2 for Boot 4.1.x -->
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Address Service (existing project) — added
| Dependency | Purpose |
|---|---|
| `spring-cloud-starter-netflix-eureka-client` | Registers this service with Eureka on startup |

*(No Feign needed here — Address Service never calls anyone else.)*

### Student Service (existing project) — added
| Dependency | Purpose |
|---|---|
| `spring-cloud-starter-netflix-eureka-client` | Registers this service with Eureka on startup |
| `spring-cloud-starter-openfeign` | Enables writing Feign Client interfaces |
| `spring-cloud-starter-loadbalancer` | Resolves service names → real URLs via Eureka, used by Feign |

Both existing services also need the same Spring Cloud BOM block added to their `pom.xml`.

---

## 5. Eureka Server Setup

**Main class** — one annotation turns a plain Spring Boot app into the registry:

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceApplication.class, args);
    }
}
```

**application.properties:**
```properties
spring.application.name=eureka-service
server.port=8761

# This app IS the registry — it doesn't need to register with
# itself or look itself up.
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

Dashboard available at: **http://localhost:8761**

---

## 6. Registering a Service (Address Service & Student Service)

No new annotation needed on the main class — just having the Eureka Client
dependency on the classpath is enough. Add to `application.properties`:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

- `service-url.defaultZone` → tells this service *where* Eureka lives, so it knows who to register with.
- `prefer-ip-address=true` → stores the real IP instead of just `localhost` (matters once services run in separate Docker containers later).

**Important naming detail:** whatever you set as `spring.application.name`
(e.g. `Address-Service`), **Eureka always stores/displays it in UPPERCASE**
internally (`ADDRESS-SERVICE`). Feign lookups match this uppercase name.

---

## 7. Feign Client — Replacing RestTemplate

### Old way (RestTemplate) — a class, manual HTTP logic
```java
public class AddressClient {
    private final RestTemplate restTemplate;

    @Value("${address-service.url}")
    private String addressServiceUrl;

    public Map<String, Object> createAddress(AddressRequestDTO dto) {
        String url = addressServiceUrl + "/api/addresses";
        return restTemplate.postForObject(url, dto, Map.class);
    }
}
```

### New way (Feign) — just an interface, no manual logic
```java
@FeignClient(name = "ADDRESS-SERVICE")
public interface AddressClient {

    @PostMapping("/api/addresses")
    Map<String, Object> createAddress(@RequestBody AddressRequestDTO addressRequestDTO);

    @GetMapping("/api/addresses/{addressId}")
    Map<String, Object> getAddressById(@PathVariable("addressId") Long addressId);

    @PutMapping("/api/addresses/{addressId}")
    void updateAddress(@PathVariable("addressId") Long addressId, @RequestBody AddressRequestDTO addressRequestDTO);
}
```

**What changed and why it matters:**
- No `RestTemplate` field, no manually built URL, no `.postForObject()`/`.getForObject()` calls.
- `@FeignClient(name = "ADDRESS-SERVICE")` replaces the hardcoded `address-service.url` — Feign asks Eureka "where is ADDRESS-SERVICE?" at call time.
- Method signatures + annotations (`@PostMapping`, `@GetMapping`, `@PathVariable`, `@RequestBody`) *are* the implementation — Spring generates the actual REST-calling code automatically at startup.
- `@EnableFeignClients` must be added to the main application class so Spring scans for `@FeignClient` interfaces.

```java
@EnableFeignClients
@SpringBootApplication
public class StudentServiceApplication { ... }
```

### Nothing else changes downstream
Because the Feign interface has the **same method names and signatures**
as the old RestTemplate-based class, `StudentServiceImpl` calls it exactly
the same way (`addressClient.createAddress(...)`) — no changes needed
there. Only the *implementation* of `AddressClient` changed, not how it's used.

---

## 8. Full Request Flow (Create Student) — With Eureka + Feign

```
Client (Postman)
   │  POST /api/students
   │  { studentName, address: {houseName, streetName, city, pincode} }
   ▼
Student Service — Controller
   ▼
Student Service — StudentServiceImpl.createStudent()
   │
   │  calls addressClient.createAddress(requestDTO.getAddress())
   ▼
Feign (AddressClient interface)
   │  1. Asks Eureka: "Where is ADDRESS-SERVICE right now?"
   │  2. Eureka returns current host:port
   │  3. Feign builds and sends: POST http://<resolved-host>:8082/api/addresses
   ▼
Address Service — Controller → Service → Repository → DB
   │  saves address, generates addressId
   ▼
Address Service returns JSON:
   { "addressId": 1, "houseName": ..., "streetName": ..., "city": ..., "pincode": ... }
   ▼
Student Service receives this as Map<String, Object>
   │  extracts addressId = 1
   │  saves Student { studentName, addressId }
   ▼
Student Service builds combined response (student + full address)
   ▼
Client receives final JSON response
```

**Key idea to remember:**
> Address Service saves the Address and generates `addressId`.
> Student Service saves the Student and stores that `addressId`.
> Student Service never stores address details directly — it asks
> Address Service, and now it finds Address Service *dynamically*
> through Eureka instead of a fixed URL.

---

## 9. Why `Map<String, Object>` Instead of a Typed DTO?

Both in the old RestTemplate version and the new Feign version, the
response from Address Service is captured as `Map<String, Object>`
rather than a strongly-typed `AddressResponseDTO`. This is a deliberate
simplification for this teaching project — it keeps you looking directly
at the raw JSON keys (`addressId`, `houseName`, etc.) instead of hiding
them behind another DTO class shared between two services. A short
helper method (`mapToAddressResponseDTO`) then converts that Map into
our own `AddressResponseDTO` for the final client-facing response.

---

## 10. Quick Reference — What to Check on the Eureka Dashboard

Visit **http://localhost:8761** after starting all three apps in order:
1. Eureka Server
2. Address Service
3. Student Service

You should see, under "Instances currently registered with Eureka":
```
ADDRESS-SERVICE   UP (1) - <host>:Address-Service:8082
STUDENT-SERVICE   UP (1) - <host>:Student-Service:8081
```

If a service is missing here, Feign will fail to find it — always check
this dashboard first when debugging inter-service call issues.