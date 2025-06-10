# Spring Boot Redis Cart Service

This project demonstrates a shopping cart service using Spring Boot, MySQL, and Redis for caching. It provides efficient cart retrieval and caching strategies to improve performance and scalability.

## Features

- Cart management with MySQL as the primary data store.
- Redis caching for fast cart summary retrieval.

---

## Useful Redis Commands

```sh
# Connect to Redis CLI
redis-cli

# List all keys in Redis
KEYS *

# Get specific cart summary by user ID (replace userId)
GET cartSummary:1

# Check TTL (time to live) for a key
TTL cartSummary:1

# Delete specific key
DEL cartSummary:1

# Clear all data
FLUSHALL

# Monitor Redis commands in real-time
MONITOR

# Exit Redis CLI
exit
```

---

## Issues Working with Redis and MySQL in Spring Boot

### LazyInitializationException

#### Explanation

- **MySQL-only version:**  
  Operations happen within a single session managed by Spring's default transaction behavior. Lazy loading works as expected.

  ```java
  @Override
  public CartSummary getCartSummaryByUserId(Long userId) {
      // works because the entire operation happens within same session
      // Spring's default transaction behavior handles it
  }
  ```

- **Redis + MySQL version (with @Transactional):**  
  Using `@Transactional(readOnly = true)` ensures the Hibernate session stays open, allowing lazy loading.  
  `@Cacheable` adds a caching layer.

  ```java
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "cartSummary", key = "#userId")
  public CartSummary getCartSummaryByUserId(Long userId) {
      // works because @Transactional explicitly manages the session
      // @Cacheable adds caching layer
  }
  ```

#### Why LazyInitializationException Occurs

- Raised when accessing lazy-loaded fields (e.g., `cart.getCartEntry()`) outside an open Hibernate session.
- Common when mapping entities to DTOs after the session is closed.

#### Solutions

- Annotate fetch methods with `@Transactional(readOnly = true)` to keep the session open for lazy loading.
- Alternatively, set `hibernate.enable_lazy_load_no_trans=true` in `application.properties` (not recommended for production).

#### Best Practice

- **Use `@Transactional(readOnly = true)`** in service layer methods that read from the DB and deal with lazy associations.
- With Redis, cache DTOs or serialized objects so lazy loading is not needed after caching.

#### Risks of `hibernate.enable_lazy_load_no_trans=true`

- Unintended DB hits from unclosed sessions.
- Memory leaks.
- Performance issues (N+1 queries).

---

## Recommendation

- Prefer `@Transactional(readOnly = true)` for read operations involving lazy fields.
- Cache DTOs in Redis to avoid lazy loading issues after caching.

---
