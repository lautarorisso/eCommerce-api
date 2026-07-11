# eCommerce API

REST API for an e-commerce system with shopping cart, order management, and JWT authentication. Built as a portfolio project demonstrating clean architecture, security best practices, and comprehensive testing.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL 16 (H2 for tests) |
| ORM | Hibernate / Spring Data JPA |
| Validation | Jakarta Bean Validation |
| Mapping | MapStruct 1.6.3 |
| API Docs | SpringDoc OpenAPI 3.0.2 (Swagger UI) |
| Build | Maven |
| Container | Docker + Docker Compose |

## Features

- **Authentication & Authorization** — JWT-based auth with role-based access control (USER / ADMIN)
- **CRUD Operations** — Full management of users, products, categories
- **Shopping Cart** — Add, update, remove products; each user has one active cart
- **Order Lifecycle** — State machine: PENDING → PAID → SHIPPED → DELIVERED (or CANCELLED from PENDING)
- **Pagination & Filters** — All list endpoints support pagination and query filters
- **Rate Limiting** — Login endpoint limited to 5 attempts per minute per IP
- **Stock Management** — Stock validation on order creation, restock endpoint for admins
- **Scheduled Tasks** — Automatic cart abandonment after inactivity
- **Swagger UI** — Interactive API documentation at `/swagger-ui.html`

## Getting Started

### Docker (recommended)

```bash
docker compose up --build
```

The app will be available at `http://localhost:8080`. Swagger UI at `http://localhost:8080/swagger-ui.html`.

### Local

**Prerequisites:** Java 21, PostgreSQL running on port 5432

1. Create the database:
```sql
CREATE DATABASE ecommerce;
```

2. Set environment variables (see [Environment Variables](#environment-variables))

3. Run:
```bash
./mvnw spring-boot:run
```

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_URL` | No | `jdbc:postgresql://localhost:5432/ecommerce` | Database connection URL |
| `DB_USER` | No | `user` | Database username |
| `DB_PASSWORD` | **Yes** | — | Database password |
| `DDL_AUTO` | No | `update` | Hibernate DDL strategy |
| `SHOW_SQL` | No | `false` | Log SQL statements |
| `JWT_SECRET` | **Yes** | — | Secret key for JWT signing (min 32 chars) |
| `JWT_EXPIRATION_HOURS` | No | `24` | Token lifetime in hours |
| `ADMIN_EMAIL` | No | `admin@email.com` | Seed admin email |
| `ADMIN_PASSWORD` | **Yes** | — | Seed admin password |

On first startup, an admin user and 8 default categories are automatically created via `DataSeeder`.

## Authentication Flow

1. **Register** or **Login** via `POST /api/auth/register` or `POST /api/auth/login`
2. Receive a JWT token in the response
3. Include the token in subsequent requests:
```
Authorization: Bearer <token>
```
4. Endpoints are protected by role:
   - **Public** — product listing, categories, auth endpoints
   - **Authenticated** — cart, orders, user profile
   - **ADMIN only** — user management, product/category management

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/register` | Register a new user | Public |
| `POST` | `/api/auth/login` | Login and get JWT token | Public |

### Users

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/users/me` | Get current user profile | Authenticated |
| `GET` | `/api/users` | List all users (paginated) | ADMIN |
| `GET` | `/api/users/{userId}` | Get user by ID | ADMIN |
| `POST` | `/api/users` | Create a user | ADMIN |
| `PATCH` | `/api/users/{userId}` | Update a user | ADMIN |
| `DELETE` | `/api/users/{userId}` | Delete a user | ADMIN |

### Products

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/products` | List products (paginated, filterable) | Public |
| `GET` | `/api/products/{productId}` | Get product by ID | Public |
| `POST` | `/api/products` | Create a product | ADMIN |
| `PATCH` | `/api/products/{productId}` | Update a product | ADMIN |
| `DELETE` | `/api/products/{productId}` | Delete a product | ADMIN |
| `PATCH` | `/api/products/{productId}/restock` | Add stock quantity | ADMIN |

**Product filters:** `search`, `minPrice`, `maxPrice`, `inStock`, `categoryId`

### Categories

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/categories` | List all categories | Public |
| `GET` | `/api/categories/{id}` | Get category by ID | Public |
| `POST` | `/api/categories` | Create a category | ADMIN |
| `PATCH` | `/api/categories/{id}` | Update a category | ADMIN |
| `DELETE` | `/api/categories/{id}` | Delete a category | ADMIN |

### Cart

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/carts/user/{userId}` | Get active cart for user | Authenticated |
| `POST` | `/api/carts/{cartId}/items` | Add product to cart | Authenticated |
| `PATCH` | `/api/carts/{cartId}/items/{productId}` | Update item quantity | Authenticated |
| `DELETE` | `/api/carts/{cartId}/items/{productId}` | Remove item from cart | Authenticated |
| `DELETE` | `/api/carts/{cartId}` | Clear cart | Authenticated |

### Orders

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/orders` | Create order from cart | Authenticated |
| `GET` | `/api/orders/user/{userId}` | List user orders (paginated) | Authenticated |
| `GET` | `/api/orders/{orderId}` | Get order by ID | Authenticated |
| `PATCH` | `/api/orders/{orderId}/cancel` | Cancel order (PENDING only) | Authenticated |
| `PATCH` | `/api/orders/{orderId}/pay` | Pay order (PENDING only) | Authenticated |
| `PATCH` | `/api/orders/{orderId}/ship` | Ship order (PAID only) | Authenticated |
| `PATCH` | `/api/orders/{orderId}/deliver` | Deliver order (SHIPPED only) | Authenticated |

**Order status flow:**

```
PENDING ──► PAID ──► SHIPPED ──► DELIVERED
  │
  └──► CANCELLED
```

**Order filters:** `status`, `minTotal`, `maxTotal`

## Testing

```bash
mvn test
```

62 tests across 4 tiers:

| Tier | Scope | Tests |
|------|-------|-------|
| Security | JwtService, SecurityUtils, JwtFilter, RateLimiter | 19 |
| Services | Error paths and business logic | 25 |
| Controllers | WebMvc layer with mock security | 12 |
| Integration | Full flow: register → login → cart → order | 1 |
| Controllers (additional) | Product, Cart, Auth, Order | 5 |

Tests use an H2 in-memory database with the `test` profile.

## Project Structure

```
src/main/java/com/lautarorisso/eCommerce_api/
├── config/           # DataSeeder, OpenApiConfig
├── controller/       # REST controllers (6)
├── dto/
│   ├── request/      # Incoming DTOs
│   └── response/     # Outgoing DTOs
├── enums/            # Role, CartStatus, OrderStatus
├── exceptions/       # Custom exceptions + GlobalExceptionHandler
├── mapper/           # MapStruct mappers
├── model/            # JPA entities
├── repository/       # Spring Data repositories
├── scheduler/        # Scheduled tasks (cart cleanup)
├── security/         # JWT filter, SecurityConfig, SecurityUtils
├── service/          # Service interfaces
│   └── impl/         # Service implementations
└── specification/    # JPA Specifications for dynamic queries
```

## Known Limitations

These are intentional design decisions that simplify the system at the expense of production-level robustness:

- **No stock reservation between cart and payment** — Stock is validated when adding items to the cart and when creating an order, but it is only actually deducted when the order is paid (`payOrder`). Between order creation and payment, other users could purchase the same stock. This approach simplifies inventory management: since stock is never deducted on order creation, cancelling a PENDING order doesn't require restoring stock. The trade-off is that stock availability at checkout time is best-effort, not guaranteed.

- **Cancel only available for PENDING orders** — Orders can only be cancelled while in PENDING status, before payment and stock deduction. Once paid, the order must follow the full lifecycle (shipped → delivered). This is a direct consequence of the stock deduction model above.

- **Hard deletes** — Users, products, and categories are physically deleted from the database rather than soft-deleted. This is appropriate for a portfolio project where historical data retention is not a requirement.

## License

This project is for educational purposes.
