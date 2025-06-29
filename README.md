# Cinebee Backend

## Overview

Cinebee Backend is a modern movie theater management system providing RESTful APIs for:

-    User authentication (JWT, Google, captcha)
-    User & role management
-    Movie management (add, update, trending, search)
-    Secure file upload (Cloudinary)
-    Email confirmation, account status
-    Trending/statistics APIs

## Tech Stack

-    Java 21, Spring Boot 3
-    Spring Security (JWT)
-    Spring Data JPA (MySQL)
-    Redis (captcha, token blacklist)
-    Cloudinary (image upload)
-    Docker (for Redis)

## Project Structure

```
Cinebee-BackEnd/
├── docker-compose.yml
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/com/cinebee/
│       │   ├── config/         # Security, JWT, CORS, Cloudinary
│       │   ├── controller/     # API endpoints (auth, movie, ...)
│       │   ├── dto/            # Request/response objects
│       │   ├── entity/         # JPA entities (User, Movie, ...)
│       │   ├── exception/      # Global exception handling
│       │   ├── mapper/         # Entity <-> DTO mapping
│       │   ├── repository/     # JPA repositories
│       │   ├── security/       # JWT filter, user details
│       │   └── service/        # Business logic
│       └── resources/
│           ├── application.yml # Spring Boot, DB, Redis, JWT, Cloudinary
│           └── ...
└── ...
```

## Environment Variables (.env)

Create a `.env` file in the project root with the following structure (do NOT add sample values here):

```
DB_USERNAME=
DB_PASSWORD=
REDIS_PASSWORD=
MAIL_USERNAME=
MAIL_PASSWORD=
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
JWT_SECRET=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
```

> **Note:** Do not commit your `.env` file to git for security reasons.

## How to Run the Project

1.  **Install:** Java 21, Maven, MySQL, Docker (for Redis)
2.  **Create DB:** Create a MySQL database named `cinebee` (or the name you set in `application.yml`)
3.  **Create `.env`** and fill in the environment variables as above
4.  **Start Redis:**
    ```sh
    docker-compose up -d
    ```
5.  **Build & run:**
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```
6.  **Access API:**
    -    Default: `http://localhost:8080`
    -    See API usage below

## API Usage

### Authentication

-    Login: `/api/auth/login` (returns JWT)
-    All protected APIs require `Authorization: Bearer <token>` header

### Movie APIs

-    **Add new movie (admin only):**
     -    `POST /api/movies/add-new-film`
     -    Body: `form-data`
          -    `info` (Text): JSON string, e.g.
               ```json
               {"title":"Komang","othernames":"Moa","basePrice":100000,"duration":120,"genre":"Action","description":"Phim hành động","posterUrl":"https://..."}
               ```
          -    `posterImageFile` (File): (optional) image file
-    **Update movie (admin only):**
     -    `POST /api/movies/update-film?id=24`
     -    Body: `form-data` (same as add)
-    **Trending/search/list:**
     -    `GET /api/movies/trending`, `/api/movies/search?title=...`, `/api/movies/all-by-likes`

### Security

-    Only users with role `ADMIN` can add/update/delete movies.
-    All APIs require JWT except those in the whitelist (see `SecurityConfig`).

## Notes

-    All image uploads are stored on Cloudinary, only the URL is saved in DB.
-    For any API requiring file upload, always use `form-data` and set `info` as JSON string.
-    If you get 403, check your token and user role.

---

**For more details, see code in each package and controller.**
