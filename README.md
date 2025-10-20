# Spring Boot JWT Auth API 🔐

A clean, stateless **REST API** built with **Spring Boot 3.x** that demonstrates secure **JWT-based authentication & authorization**, user profile caching with **Caffeine**, and safe **PDF attachment uploads**.

---

## 🧰 Tech Stack

* Java 17
* Spring Boot 3 · Spring Security
* JPA / Hibernate · Jakarta Bean Validation
* JJWT (HS256)
* Caffeine Cache
* Lombok

---

## ✨ Features

* User registration and login with JWT
* Stateless security (no HTTP sessions)
* BCrypt password hashing
* Custom 401/403 handlers
* Secure PDF-only uploads with filename/MIME/path checks
* Structured error model and validation
* Profile caching with Caffeine for better performance
* Clear layering: Controller · Service · Repository · Mapper

---

## 🔒 Security Overview

* JWT is issued on successful login at `/auth/login`.
* All protected requests must include `Authorization: Bearer <token>`.
* Token claims are minimal and non-sensitive: `sub` = **publicId**, `username`, `roles`.
* Each request passes through `JwtAuthenticationFilter`, which validates signature and expiration, converts claims into a lightweight `CustomUserPrincipal`, and sets the authentication in Spring's `SecurityContext`.
* Controllers can access the authenticated user via `@AuthenticationPrincipal(expression = "publicId")`.

---

## 🆔 Why `publicId` instead of DB `id`

* **Privacy & stability:** The API exposes a **publicId** (UUID) rather than the internal database `id`. This avoids leaking sequential identifiers and decouples public references from persistence internals.
* **No forced logout on credential change:** Because JWT `sub` holds the stable `publicId`, changing credentials (e.g., password update) does **not** require immediate token invalidation or forced re-login, unless your policy demands revocation. You can still add explicit revocation or short TTL if required.

---

## 🔁 Authentication Flow

1. **Login** → `POST /auth/login` with `username` and `password`.
2. **Issue JWT** → HS256-signed token with claims: `sub=publicId`, `username`, `roles` and `exp` from `jwt.expirationMillis`.
3. **Client stores token** and sends it on each request via `Authorization: Bearer <token>`.
4. **Filter validates** the token and populates the security context for downstream handlers.

---

## 🧠 Token Handling

* **Generation:** `AuthService` uses `JwtUtil.generateToken(publicId, username, roles)`.
* **Signature:** HS256 with a strong Base64-encoded secret.
* **Claims:** Only non-sensitive identifiers (no PII, no permissions beyond roles).
* **Expiration:** Configurable via `jwt.expirationMillis`.
* **Validation:** Every request re-validates signature and expiration using the same secret.
* **Stateless:** No server-side sessions. Scaling is simple and horizontal.
* **Refresh:** Not included by default. Can be added with a refresh endpoint and rotation.

---

## 🧱 `JwtAuthenticationFilter` explained

The filter extends `OncePerRequestFilter` and runs **once per request**:

1. **Read header:** If `Authorization` is missing or not `Bearer <token>`, the filter **does nothing** and passes the request through.
2. **Parse & validate:** Uses `JwtUtil.tryParse(token)` to verify signature and `exp`. Invalid or expired → chain continues without authentication (will hit 401 later if required).
3. **Build principal:** Extracts `publicId` (subject), `username`, and `roles` claims and creates a minimal `CustomUserPrincipal` **without** hitting the database.
4. **Set context:** Creates an authenticated `UsernamePasswordAuthenticationToken` and stores it in `SecurityContextHolder` so downstream code can use `@AuthenticationPrincipal(expression = "publicId")`.
5. **Continue chain:** Delegates to the next filter/controller.

**Result:** Zero DB lookups per request - only cryptographic validation and claim parsing occur.

---

## 🚀 Caffeine Caching for performance

* **What:** `Cache<String, UserReadOnlyDTO>` keyed by `publicId`.
* **Where:** Used in `UserService.getMyProfileByPublicId` to avoid repeated DTO construction and serialization for frequently requested profiles.
* **Config:** TTL **15 minutes**, `maximumSize(1000)`.
* **Invalidation:** On profile update or delete, the service updates/invalidates cache entries to keep data consistent.

**Benefit:** Lower latency and reduced CPU/DB pressure for hot endpoints like `/api/users/me`.

---

## 📡 Endpoints

### Auth

* `POST /auth/login` → issues JWT

### Users

* `POST /api/users/create` → create user
* `GET /api/users/me` → returns current profile (Caffeine-cached)
* `PATCH /api/users/me` → update username/password
* `DELETE /api/users/me` → delete account

### Attachments (PDF only)

* `POST /api/attachment/upload`
* `GET /api/attachment/my`
* `DELETE /api/attachment/{attachmentId}`

**Controller tip:** Access the caller like this:

```java
@GetMapping("/me")
public ResponseEntity<UserReadOnlyDTO> getMyProfile(
    @AuthenticationPrincipal(expression = "publicId") String publicId
) {
    return ResponseEntity.ok(userService.getMyProfileByPublicId(publicId));
}
```

---

## 🗃️ Entities

**User**

* `username` (unique ≤16), `password` (BCrypt, write-only)
* `role` (`ADMIN|USER`), `isActive`
* `publicId` (UUID, unique, immutable)
* Auditing: `createdAt`, `updatedAt`

**Attachment**

* Linked to `User`
* Fields: `fileName`, `savedName`, `extension=pdf`, `contentType=application/pdf`, `filePath`
* Secured with path traversal and MIME checks

---

## 📦 DTOs

* `UserInsertDTO`, `UserUpdateDTO`, `UserReadOnlyDTO`
* `LoginRequestDTO`, `LoginResponseDTO`
* `AttachmentReadOnlyDTO`

---

## ⚙️ Configuration

```properties
# application.properties
jwt.secret=<BASE64_HS256_KEY>   # strong, high-entropy
jwt.expirationMillis=900000     # e.g., 15 minutes
```

---

## 🚨 Error Model

* Central `@ControllerAdvice` returns `ResponseMessageDTO { timestamp, code, message }`.
* Validation errors follow a consistent shape with a list of `{field, message}`.
* Domain exceptions: `ALREADY_EXISTS`, `NOT_FOUND`, `INVALID_INPUT`, `VALIDATION`.

---

## 🧭 Architecture

* `SecurityConfig` → filter chain, access rules, handlers, provider
* `AuthService` → credential auth and JWT issuance
* `JwtAuthenticationFilter` → token validation per request
* `CustomUserDetailsService` → user lookup for the **login** path
* `CustomUserPrincipal` → minimal identity and authorities
* `UserService` → profile CRUD, cache management
* `AttachmentService` → secure PDF handling
* Repositories → `UserRepository`, `AttachmentRepository`

---

## ✅ Quality & next steps

**Strengths**

* Stateless JWT with minimal claims
* Public-facing `publicId` for privacy and stable references
* No DB hits in the request path after login
* Centralized error handling and validation
* Safe file handling and cache-aware profile endpoint

**Recommended add-ons**

* Refresh tokens + rotation; optional denylist for emergency revocation
* Rate limiting on `/auth/login`
* Explicit CORS configuration for frontend
* Method-level security with `@PreAuthorize`

---

## 📌 Summary

A modern and secure **JWT-based authentication API** with a privacy-first identifier strategy using **publicId**, efficient **Caffeine** caching for hot reads, and a lightweight `JwtAuthenticationFilter` that keeps request handling **DB-free** and fast. Ready to extend with refresh tokens and fine-grained authorization.
