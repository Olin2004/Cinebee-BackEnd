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

Create a `.env` file in the project root with the following structure (do NOT add sample values):

```
# Database
DB_USERNAME=
DB_PASSWORD=

# Redis
REDIS_PASSWORD=

# Mail
MAIL_USERNAME=
MAIL_PASSWORD=

# JWT
JWT_SECRET=

# Recaptcha
RECAPTCHA_SECRET=

# Google OAuth
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

# Cloudinary
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
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

## API Endpoints & How to Test

### 1. Get Trending Movies
- **Endpoint:** `GET /api/movies/trending`
- **Description:** Get top 10 trending movies (sorted by rating, likes, views).
- **Test:**
  - Method: GET
  - No auth required
  - Example: `http://localhost:8080/api/movies/trending`

### 2. Get All Movies by Likes (Paging)
- **Endpoint:** `GET /api/movies/all-by-likes?page=1&size=20`
- **Description:** Get paginated movies sorted by likes and views.
- **Test:**
  - Method: GET
  - Params: `page` (default 1), `size` (default 20)
  - No auth required

### 3. Search Movies (Suggest)
- **Endpoint:** `GET /api/movies/search?title=...`
- **Description:** Search movies by title (autocomplete/suggest).
- **Test:**
  - Method: GET
  - Param: `title` (string)
  - No auth required

### 4. Add New Movie (Admin Only)
- **Endpoint:** `POST /api/movies/add-new-film`
- **Description:** Add a new movie (with or without image upload).
- **Test:**
  - Method: POST
  - Headers: `Authorization: Bearer <admin_token>`
  - Body: `form-data`
    - `info` (Text): JSON string, e.g.
      ```json
      {"title":"Komang","othernames":"Moa","basePrice":100000,"duration":120,"genre":"Action","description":"Phim hành động","posterUrl":"https://..."}
      ```
    - `posterImageFile` (File): (optional) image file

### 5. Update Movie (Admin Only)
- **Endpoint:** `POST /api/movies/update-film?id=24`
- **Description:** Update movie info and/or poster image.
- **Test:**
  - Method: POST
  - Headers: `Authorization: Bearer <admin_token>`
  - Params: `id` (movie id)
  - Body: `form-data` (same as add)

### 6. Delete Movie (Admin Only)
- **Endpoint:** `POST /api/movies/delete?id=24`
- **Description:** Delete a movie by id.
- **Test:**
  - Method: POST
  - Headers: `Authorization: Bearer <admin_token>`
  - Params: `id` (movie id)

---
**Testing Tips:**
- Use Postman or any REST client.
- For file upload, always use `form-data` and set `info` as JSON string (Text type), `posterImageFile` as File.
- For admin APIs, always include a valid JWT token in the `Authorization` header.
- If you get 403, check your token and user role.

---

**For more details, see code in each package and controller.**
