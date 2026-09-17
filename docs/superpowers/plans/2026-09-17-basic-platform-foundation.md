# Basic Platform Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a runnable, locally deployable foundation for the Memo application with a Vue frontend, Spring Boot API, MySQL and Milvus dependencies, and a verified health-check contract.

**Architecture:** The frontend is a Vue 3 TypeScript single-page application that calls a Spring Boot REST API through `/api`. The backend has a small vertical foundation slice: application bootstrap, the `/api/health` endpoint, the `Result<T>` envelope and global validation/error handling. Docker Compose starts MySQL and Milvus for future note, knowledge-base and RAG modules without adding domain behavior prematurely.

**Tech Stack:** Java 17, Spring Boot 3, Maven, JUnit 5, Vue 3, TypeScript, Vite, Pinia, Vue Router, Vitest, Docker Compose, MySQL 8, Milvus standalone.

---

## File Structure

| Path | Responsibility |
| --- | --- |
| `backend/pom.xml` | Spring Boot dependencies and build plugins. |
| `backend/src/main/java/com/aireview/AiReviewApplication.java` | Backend process entry point. |
| `backend/src/main/java/com/aireview/common/Result.java` | Stable API success and error envelope. |
| `backend/src/main/java/com/aireview/common/GlobalExceptionHandler.java` | REST validation and unexpected-error mapping. |
| `backend/src/main/java/com/aireview/health/HealthController.java` | Public liveness endpoint. |
| `backend/src/main/resources/application.yml` | Non-secret local configuration and environment-variable placeholders. |
| `backend/src/test/java/com/aireview/health/HealthControllerTest.java` | Health API contract test. |
| `frontend/package.json` | Frontend scripts and dependencies. |
| `frontend/vite.config.ts` | Vite build and `/api` development proxy. |
| `frontend/src/main.ts` | Vue application bootstrap. |
| `frontend/src/App.vue` | Application shell. |
| `frontend/src/router/index.ts` | Initial public route. |
| `frontend/src/views/HomeView.vue` | Minimal running-state view. |
| `frontend/src/api/request.ts` | Typed fetch wrapper for the `Result<T>` API contract. |
| `frontend/src/api/request.spec.ts` | API wrapper unit test. |
| `deploy/docker-compose.yml` | Local MySQL and Milvus standalone dependencies. |
| `deploy/.env.example` | Documented non-secret environment variable template. |

## Task 1: Create the Spring Boot health vertical slice

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/aireview/AiReviewApplication.java`
- Create: `backend/src/main/java/com/aireview/common/Result.java`
- Create: `backend/src/main/java/com/aireview/health/HealthController.java`
- Create: `backend/src/test/java/com/aireview/health/HealthControllerTest.java`

- [ ] **Step 1: Write the failing HTTP contract test**

```java
@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void healthReturnsTheApiEnvelope() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.status").value("UP"));
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn -f backend/pom.xml test -Dtest=HealthControllerTest`

Expected: Maven fails because the backend project and health endpoint do not exist.

- [ ] **Step 3: Implement the minimum backend slice**

```java
@SpringBootApplication
public class AiReviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiReviewApplication.class, args);
    }
}

public record Result<T>(int code, String message, T data) {
    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "OK", data);
    }
}

@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of("status", "UP"));
    }
}
```

Use Spring Boot Web and Test dependencies in `backend/pom.xml`, with Java release 17.

- [ ] **Step 4: Run the test to verify it passes**

Run: `mvn -f backend/pom.xml test -Dtest=HealthControllerTest`

Expected: `BUILD SUCCESS` and one passing test.

- [ ] **Step 5: Commit the vertical slice**

```bash
git add backend/pom.xml backend/src
git commit -m "feat: 新增后端健康检查框架"
```

## Task 2: Add backend configuration and unified error responses

**Files:**
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/java/com/aireview/common/GlobalExceptionHandler.java`
- Create: `backend/src/test/java/com/aireview/common/GlobalExceptionHandlerTest.java`
- Modify: `backend/src/main/java/com/aireview/common/Result.java`

- [ ] **Step 1: Write the failing validation error test**

```java
@WebMvcTest(ValidationProbeController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void invalidInputUsesTheApiEnvelope() throws Exception {
        mockMvc.perform(post("/test/validation").contentType(APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn -f backend/pom.xml test -Dtest=GlobalExceptionHandlerTest`

Expected: compilation or assertion failure because validation exceptions have no shared response mapper.

- [ ] **Step 3: Implement configuration and error mapping**

```yaml
spring:
  application:
    name: ai-review
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/memo}
    username: ${DB_USERNAME:memo}
    password: ${DB_PASSWORD:memo}
server:
  port: ${SERVER_PORT:8080}
```

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException exception) {
        return Result.fail(400, exception.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleUnexpected(Exception exception) {
        return Result.fail(500, "服务器内部错误");
    }
}
```

Add `Result.fail(int code, String message)` and use `@Valid` in the test-only validation probe.

- [ ] **Step 4: Run all backend tests**

Run: `mvn -f backend/pom.xml test`

Expected: `BUILD SUCCESS` with the health and error-envelope tests passing.

- [ ] **Step 5: Commit the shared API foundation**

```bash
git add backend
git commit -m "feat: 新增后端统一响应与异常处理"
```

## Task 3: Create the Vue application shell and typed API client

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/index.html`
- Create: `frontend/src/main.ts`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/router/index.ts`
- Create: `frontend/src/views/HomeView.vue`
- Create: `frontend/src/api/request.ts`
- Create: `frontend/src/api/request.spec.ts`

- [ ] **Step 1: Write the failing API client test**

```ts
it('unwraps a successful Result response', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
    new Response(JSON.stringify({ code: 0, message: 'OK', data: { status: 'UP' } }))
  ))

  await expect(request<{ status: string }>('/api/health')).resolves.toEqual({ status: 'UP' })
})
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `npm --prefix frontend run test -- --run`

Expected: command fails because the frontend project and `request` module do not exist.

- [ ] **Step 3: Implement the Vue shell and client**

```ts
export type Result<T> = { code: number; message: string; data: T }

export async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...init?.headers },
    ...init,
  })
  const body = (await response.json()) as Result<T>
  if (!response.ok || body.code !== 0) throw new Error(body.message)
  return body.data
}
```

Create a Vue Router route `/` rendering `HomeView.vue`; that view calls `request('/api/health')` on mount and displays API availability. Configure Vite's dev server proxy so `/api` targets `http://localhost:8080`.

- [ ] **Step 4: Run unit tests and production build**

Run: `npm --prefix frontend run test -- --run`

Expected: one passing request-client test.

Run: `npm --prefix frontend run build`

Expected: Vite produces `frontend/dist` without TypeScript errors.

- [ ] **Step 5: Commit the frontend foundation**

```bash
git add frontend
git commit -m "feat: 新增前端应用基础框架"
```

## Task 4: Add local MySQL and Milvus service dependencies

**Files:**
- Create: `deploy/docker-compose.yml`
- Create: `deploy/.env.example`
- Modify: `README.md`

- [ ] **Step 1: Write a compose configuration validation command**

The Compose file must define `mysql`, `etcd`, `minio`, and `milvus` services, persist their data in named volumes, and expose Milvus on `19530` and `9091`.

- [ ] **Step 2: Verify the missing configuration fails**

Run: `docker compose -f deploy/docker-compose.yml config`

Expected: command fails because the Compose file does not exist.

- [ ] **Step 3: Implement the local dependency stack**

```yaml
services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_DATABASE: ${MYSQL_DATABASE:-memo}
      MYSQL_USER: ${MYSQL_USER:-memo}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:-memo}
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-root}
    ports: ["3306:3306"]
  etcd:
    image: quay.io/coreos/etcd:v3.5.18
  minio:
    image: minio/minio:RELEASE.2024-10-13T13-34-11Z
  milvus:
    image: milvusdb/milvus:v2.5.5
    ports: ["19530:19530", "9091:9091"]
```

Give each service its documented data volume, health check and required Milvus environment variables. Add `deploy/.env.example` with only development defaults, and add README commands for copying it and starting/stopping dependencies.

- [ ] **Step 4: Validate the Compose model**

Run: `docker compose -f deploy/docker-compose.yml --env-file deploy/.env.example config`

Expected: exit code 0 and all four services appear in rendered output.

- [ ] **Step 5: Commit the dependency stack**

```bash
git add deploy README.md
git commit -m "chore: 新增本地 MySQL 与 Milvus 依赖"
```

## Task 5: Verify the baseline and document the first feature boundary

**Files:**
- Modify: `README.md`
- Modify: `docs/需求分析.md`

- [ ] **Step 1: Add the executable baseline instructions**

Document the exact order: start Docker dependencies, start the Spring Boot API, install frontend dependencies, start Vite, and visit the root route. State that this milestone intentionally contains no authentication, note CRUD, file upload, knowledge-base membership, embedding, Milvus collection, or model calls.

- [ ] **Step 2: Run the complete baseline verification**

Run: `mvn -f backend/pom.xml test`

Expected: `BUILD SUCCESS`.

Run: `npm --prefix frontend run test -- --run`

Expected: all Vitest tests pass.

Run: `npm --prefix frontend run build`

Expected: Vite build succeeds.

Run: `docker compose -f deploy/docker-compose.yml --env-file deploy/.env.example config`

Expected: Compose configuration is valid.

- [ ] **Step 3: Commit baseline documentation**

```bash
git add README.md docs/需求分析.md
git commit -m "docs: 明确基础框架运行边界"
```

## Plan Self-Review

- Spec coverage: this plan implements the foundation required by the architecture: frontend/API separation, unified response shape, deployable MySQL and Milvus dependencies, and a verified health contract. It intentionally defers all product behaviors to separately testable commits.
- Deliberate next plans: account authentication; note and Markdown-file management; knowledge-base membership; indexing and Milvus hybrid retrieval; DeepSeek chat; Qwen RAG sessions and citations.
- Consistency: `Result<T>` is the shared backend and frontend response contract. The health endpoint is the sole vertical API slice and is the frontend's baseline integration test target.
- Placeholder scan: no unfinished implementation placeholders are present; each task names exact files, verification commands and its own commit.
