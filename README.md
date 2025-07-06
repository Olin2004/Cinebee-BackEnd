# Cinebee Backend

## Overview

Cinebee Backend is a robust and modern movie theater management system built with Spring Boot. It provides a comprehensive suite of RESTful APIs for:

-   **User Authentication:** Secure JWT-based authentication, Google OAuth2 integration, and CAPTCHA verification.
-   **User & Role Management:** Efficient management of user accounts and roles.
-   **Movie Management:** Full CRUD operations for movies, including trending, search, and listing functionalities.
-   **Secure File Uploads:** Integration with Cloudinary for secure and efficient image storage.
-   **Email Services:** Account confirmation and status updates via email.
-   **Analytics:** APIs for trending movies and various statistics.

## Tech Stack

-   **Java 21:** The core programming language.
-   **Spring Boot 3:** Framework for building robust, production-ready applications.
-   **Spring Security (JWT):** For authentication and authorization using JSON Web Tokens.
-   **Spring Data JPA (MySQL):** For data persistence and interaction with the MySQL database.
-   **Redis:** Used for caching (e.g., CAPTCHA, token blacklist) to improve performance.
-   **Cloudinary:** A cloud-based media management platform for image and video uploads.
-   **Docker:** For containerizing Redis, ensuring easy setup and consistent environments.
-   **Maven:** Dependency management and build automation tool.

## Project Structure

```
Cinebee-BackEnd/
├── docker-compose.yml      # Docker configuration for Redis
├── pom.xml                 # Maven project configuration
├── README.md               # Project documentation
├── src/
│   └── main/
│       ├── java/com/cinebee/
│       │   ├── config/         # Security, JWT, CORS, Cloudinary configurations
│       │   ├── controller/     # REST API endpoints (auth, movie, etc.)
│       │   ├── dto/            # Data Transfer Objects for requests and responses
│       │   ├── entity/         # JPA entities (User, Movie, Banner, etc.)
│       │   ├── exception/      # Custom exceptions and global exception handling
│       │   ├── mapper/         # Mappers for converting entities to DTOs and vice-versa
│       │   ├── repository/     # Spring Data JPA repositories for database operations
│       │   ├── security/       # JWT filter, custom user details service
│       │   └── service/        # Business logic and service implementations
│       └── resources/
│           └── application.yml # Spring Boot, DB, Redis, JWT, Cloudinary properties
└── ...
```

## Environment Variables (.env)

Create a `.env` file in the project root directory with the following variables. **Do NOT commit this file to your version control system.**

```
# Database Configuration
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password

# Redis Configuration
REDIS_PASSWORD=your_redis_password

# Email Configuration (for account confirmation, etc.)
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_app_password

# JWT Configuration
JWT_SECRET=aVeryLongAndSecureRandomSecretKeyForJWT

# reCAPTCHA Configuration
RECAPTCHA_SECRET=your_recaptcha_secret_key

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

## How to Run the Project

Follow these steps to set up and run the Cinebee Backend locally:

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 21 or higher
    *   Apache Maven
    *   MySQL Database Server
    *   Docker (for running Redis)

2.  **Database Setup:**
    *   Create a MySQL database named `cinebee`. You can adjust the database name in `src/main/resources/application.yml` if needed.

3.  **Environment Variables:**
    *   Create the `.env` file in the project root as described in the "Environment Variables" section above and fill in your credentials.

4.  **Start Redis (using Docker):**
    ```bash
    docker-compose up -d
    ```
    This command will start the Redis container in detached mode.

5.  **Build and Run the Application:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    The `mvn clean install` command compiles the project and packages it. `mvn spring-boot:run` starts the Spring Boot application.

6.  **Access the API:**
    *   The application will typically run on `http://localhost:8080`.
    

## API Usage

The API endpoints are designed to be intuitive and follow RESTful principles. All protected endpoints require a valid JWT in the `Authorization: Bearer <token>` header.

### Authentication Endpoints

-   **User Registration:**
    *   `POST /api/auth/register`
    *   Description: Register a new user account.
-   **User Login:**
    *   `POST /api/auth/login`
    *   Description: Authenticate a user and receive a JWT.
-   **Google OAuth2 Login:**
    *   `POST /api/auth/google`
    *   Description: Authenticate using a Google token.

### Movie Endpoints

-   **Get Trending Movies:**
    *   `GET /api/movies/trending`
    *   Description: Retrieves a list of trending movies.
    *   Authentication: None required.
-   **Search Movies:**
    *   `GET /api/movies/search?title={title}`
    *   Description: Searches for movies by title.
    *   Authentication: None required.
-   **Add New Movie (Admin Only):**
    *   `POST /api/movies/add-new-film`
    *   Description: Adds a new movie to the database. Supports optional poster image upload.
    *   Authentication: Requires `ADMIN` role.
    *   Request Body (form-data):
        *   `info` (Text): JSON string containing movie details.
            ```json
            {
              "title": "Movie Title",
              "othernames": "Alternative Title",
              "basePrice": 100000,
              "duration": 120,
              "genre": "Action",
              "description": "A brief description of the movie.",
              "posterUrl": "https://example.com/default-poster.jpg"
            }
            ```
        *   `posterImageFile` (File): (Optional) The movie poster image file.
-   **Update Movie (Admin Only):**
    *   `POST /api/movies/update-film?id={movieId}`
    *   Description: Updates an existing movie's information and/or poster image.
    *   Authentication: Requires `ADMIN` role.
    *   Request Body (form-data): Same as "Add New Movie".
-   **Delete Movie (Admin Only):**
    *   `POST /api/movies/delete?id={movieId}`
    *   Description: Deletes a movie by its ID.
    *   Authentication: Requires `ADMIN` role.

### Other Endpoints

-   **CAPTCHA Generation:**
    *   `GET /api/captcha/generate`
    *   Description: Generates a new CAPTCHA image and returns its ID.
-   **User Profile:**
    *   `GET /api/profile`
    *   Description: Retrieves the authenticated user's profile information.
    *   Authentication: Requires valid JWT.

## Testing Tips

-   Use tools like Postman, Insomnia, or your preferred REST client for testing API endpoints.
-   For endpoints requiring file uploads, always use `form-data` and ensure the `info` field is sent as a JSON string (Text type) and `posterImageFile` as a File type.
-   When testing admin-only APIs, ensure you include a valid JWT token for an `ADMIN` user in the `Authorization` header.
-   If you encounter a `403 Forbidden` error, verify your JWT token and the user's assigned role.

---

For more detailed information, refer to the source code within each package and controller.