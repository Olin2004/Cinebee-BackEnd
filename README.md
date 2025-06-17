# Cinema Zino Backend - Deployment & System Architecture

## 1. System Overview

Cinema Zino Backend is a modern movie theater management system, providing APIs for:
- User registration and login (standard, Google, captcha)
- User management and authorization
- Movie, showtime, room, ticket, and payment management
- Email confirmation, JWT security
- Account status management (active, banned)
- Trending movies statistics by view count

## 2. Project Structure

```
cinema-zino-backend/
├── docker-compose.yml         # Run Redis (and can be extended for DB)
├── pom.xml                    # Maven dependencies
├── README.md                  # Deployment & system structure guide
├── src/
│   └── main/
│       ├── java/com/cinemazino/
│       │   ├── config/        # Security, JWT, CORS, Captcha configs
│       │   ├── controller/    # API endpoints (auth, profile, movie...)
│       │   ├── dto/           # Request/response objects
│       │   ├── entity/        # JPA entities (User, Movie, Ticket...)
│       │   ├── exception/     # Global exception handling
│       │   ├── repository/    # JPA repositories
│       │   ├── security/      # JWT filter, user details
│       │   ├── service/       # Business logic (Auth, Email, Movie...)
│       │   └── util/          # Utilities
│       └── resources/
│           ├── application.yml # Spring Boot, DB, Redis, JWT, Google configs
│           └── ...
└── ...
```

## 3. Technologies Used
- Java 21, Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA (MySQL)
- Redis (for captcha, token blacklist)
- Docker (for Redis)
- Google OAuth2, Email SMTP

## 4. Local Deployment Guide

### 4.1. Prerequisites
- Java 21
- Maven
- MySQL (create DB cinema_zino)
- Docker (for Redis)

### 4.2. Run Redis with Docker
```sh
docker-compose up -d
```

### 4.3. Environment Variables
- Create a `.env` file at the project root:
```
GOOGLE_CLIENT_SECRET=... # Get from Google Cloud
```

### 4.4. Configure DB, Redis, Google, JWT in `src/main/resources/application.yml`
- Sample config provided, just update DB user/pass and Google client-id if needed.

### 4.5. Build & Run Backend
```sh
mvn clean install
mvn spring-boot:run
```

### 4.6. Test API
- FE calls API via http://localhost:8080
- Test login, register, captcha, profile, movies, tickets, etc.

## 5. Deployment Notes
- Do not commit `.env` to git (keep client secret safe)
- Ensure ports 8080 (BE), 6379 (Redis), 3306 (MySQL) are open
- FE must send accessToken in Authorization header for protected APIs
- CORS is configured to allow FE from http://localhost:3000

## 6. Production & Scaling
- Deploy backend to server/cloud (Vercel, AWS, Azure, etc.)
- Use S3/Cloud for trailers, movie images, only store URLs in DB
- Extend docker-compose to run MySQL, backend, etc.

---

## 7. Contribution & Development
- Fork, create a branch, and submit pull requests to contribute
- Contact admin for admin privileges

---

# Cinema Zino Backend - Modern, secure, and scalable movie theater management system!

---

## Main Functionality Comments (in English)

- **Controllers**: Handle HTTP requests and return responses. Example: `ProfileController` returns user profile info, `AuthController` handles login/logout/refresh.
- **Services**: Contain business logic. Example: `AuthService` manages authentication, registration, Google login, token refresh, etc.
- **Repositories**: Interface with the database using Spring Data JPA.
- **Entities**: Represent database tables. Example: `User`, `Movie`, `Ticket`.
- **Security**: JWT authentication filter, user details service, and security config for endpoint protection.
- **Captcha**: Generates and validates captcha for login/registration security.
- **Trending Movies**: Movies are sorted by viewCount, and the trending API returns the most viewed movies.
- **Account Status**: User entity includes a status field (ACTIVE, BANNED) to manage account state.
- **Email Service**: Sends registration confirmation and other notifications.

// All methods and classes are commented in English to explain their purpose and usage.
