# Cinebee Backend

## Overview

Cinebee Backend is a robust and modern movie theater management system built with Spring Boot. It provides a comprehensive suite of RESTful APIs for:

-   **User Authentication:** Secure JWT-based authentication, Google OAuth2 integration, and CAPTCHA verification.
-   **User & Role Management:** Efficient management of user accounts and roles.
-   **Movie Management:** Full CRUD operations for movies, including trending, search, and listing functionalities.
-   **Secure File Uploads:** Integration with Cloudinary for secure and efficient image storage.
-   **Email Services:** Account confirmation and status updates via email (now asynchronous).
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
│       │   └── service/        # Business logic and service implementations (now with interfaces and implementations)
│       └── resources/
│           └── application.yml # Spring Boot, DB, Redis, JWT, Cloudinary properties
└── ...
```

## Database Schema and Relationships

Below is a detailed breakdown of the database tables and their relationships, derived from the JPA entities.

### **1. Table: `Users`**
*   **Description:** Stores user information.
*   **Key Columns:** `id` (PK), `username` (Unique), `email` (Unique), `phoneNumber` (Unique), `password`, `fullName`, `dateOfBirth`, `avatarUrl`, `oauthId`, `provider`, `role`, `userStatus`, `createdAt`, `updatedAt`.
*   **Indexes:** `idx_username` on `username`, `idx_email` on `email`.
*   **Relationships:**
    *   `One-to-Many` with `Comments`: One user can write many comments.
    *   `One-to-Many` with `Payments`: One user can make many payments.
    *   `One-to-Many` with `Tickets`: One user can book many tickets.

### **2. Table: `Movies`**
*   **Description:** Stores detailed information about movies.
*   **Key Columns:** `id` (PK), `title` (Non-null), `description`, `duration` (Non-null), `posterUrl`, `posterPublicId`, `releaseDate`, `genre`, `basePrice` (Non-null), `discountPercentage`, `createdAt`, `othernames`, `rating`, `votes`, `views`, `actors`, `director`, `country`.
*   **Relationships:**
    *   `One-to-Many` with `Banners`: One movie can appear on many banners.
    *   `One-to-Many` with `Comments`: One movie can have many comments.
    *   `One-to-Many` with `Showtimes`: One movie can have many showtimes.
    *   `One-to-Many` with `Trailers`: One movie can have many trailers.

### **3. Table: `banners`**
*   **Description:** Stores information about promotional banners.
*   **Key Columns:** `id` (PK), `title`, `description`, `bannerUrl`, `startDate`, `endDate`, `isActive`.
*   **Relationships:**
    *   `Many-to-One` with `Movies`: Many banners can link to one movie (via `movie_id` foreign key).

### **4. Table: `Comments`**
*   **Description:** Stores user comments on movies.
*   **Key Columns:** `id` (PK), `content` (Non-null), `createdAt`.
*   **Relationships:**
    *   `Many-to-One` with `Movies`: Each comment belongs to one movie (via `movie_id` foreign key, non-null).
    *   `Many-to-One` with `Users`: Each comment is made by one user (via `user_id` foreign key, non-null).

### **5. Table: `Payments`**
*   **Description:** Stores payment transaction details for tickets.
*   **Key Columns:** `id` (PK), `amount` (Non-null), `paymentMethod` (Enum, Non-null), `paymentStatus` (Enum, Non-null), `createdAt`.
*   **Relationships:**
    *   `Many-to-One` with `Tickets`: Each payment is for one ticket (via `ticket_id` foreign key, non-null).
    *   `Many-to-One` with `Users`: Each payment is made by one user (via `user_id` foreign key).

### **6. Table: `Rooms`**
*   **Description:** Stores information about cinema rooms within a theater.
*   **Key Columns:** `id` (PK), `name` (Non-null), `capacity`, `createdAt`.
*   **Unique Constraints:** `(theater_id, name)`.
*   **Relationships:**
    *   `Many-to-One` with `Theaters`: Each room belongs to one theater (via `theater_id` foreign key, non-null).
    *   `One-to-Many` with `Room_Seats`: One room has many defined seats.
    *   `One-to-Many` with `Showtimes`: One room can host many showtimes.

### **7. Table: `Room_Seats`**
*   **Description:** Defines the seats within a specific cinema room.
*   **Key Columns:** `id` (PK), `seatNumber` (Non-null), `seatType` (Enum, Non-null).
*   **Unique Constraints:** `(room_id, seat_number)`.
*   **Relationships:**
    *   `Many-to-One` with `Rooms`: Each room seat belongs to one room (via `room_id` foreign key, non-null).

### **8. Table: `Seats`**
*   **Description:** Stores the status and pricing of individual seats for a specific showtime.
*   **Key Columns:** `id` (PK), `seatNumber` (Non-null), `seatType` (Enum, Non-null), `isAvailable`, `priceModifier`.
*   **Unique Constraints:** `(showtime_id, seat_number)`.
*   **Relationships:**
    *   `Many-to-One` with `Showtimes`: Each seat belongs to one showtime (via `showtime_id` foreign key, non-null).
    *   `One-to-Many` with `Tickets`: One seat in a showtime can be booked by one ticket.

### **9. Table: `Showtimes`**
*   **Description:** Stores information about movie showtimes.
*   **Key Columns:** `id` (PK), `startTime` (Non-null), `endTime` (Non-null), `priceModifier` (Non-null), `createdAt`.
*   **Relationships:**
    *   `Many-to-One` with `Movies`: Each showtime features one movie (via `movie_id` foreign key, non-null).
    *   `Many-to-One` with `Theaters`: Each showtime takes place at one theater (via `theater_id` foreign key, non-null).
    *   `Many-to-One` with `Rooms`: Each showtime takes place in one room (via `room_id` foreign key, non-null).
    *   `One-to-Many` with `Seats`: One showtime has many seats defined for it.
    *   `One-to-Many` with `Tickets`: One showtime has many tickets booked for it.

### **10. Table: `Theaters`**
*   **Description:** Stores information about cinema theaters.
*   **Key Columns:** `id` (PK), `name` (Non-null), `address`, `createdAt`.
*   **Relationships:**
    *   `One-to-Many` with `Rooms`: One theater can have many rooms.
    *   `One-to-Many` with `Showtimes`: One theater can host many showtimes.

### **11. Table: `Tickets`**
*   **Description:** Stores information about booked tickets.
*   **Key Columns:** `id` (PK), `price` (Non-null), `bookedAt` (Non-null), `isCancelled`, `cancelledAt`, `ticketSales`.
*   **Relationships:**
    *   `Many-to-One` with `Users`: Each ticket is booked by one user (via `user_id` foreign key).
    *   `Many-to-One` with `Showtimes`: Each ticket is for one showtime (via `showtime_id` foreign key, non-null).
    *   `Many-to-One` with `Seats`: Each ticket is for one specific seat in a showtime (via `seat_id` foreign key, non-null).
    *   `One-to-Many` with `Payments`: One ticket can have multiple associated payments.

### **12. Table: `trailers`**
*   **Description:** Stores URLs for movie trailers.
*   **Key Columns:** `id` (PK), `trailerUrl` (Non-null).
*   **Relationships:**
    *   `Many-to-One` with `Movies`: Each trailer belongs to one movie (via `movie_id` foreign key, non-null).

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
-   **Refresh Token:**
    *   `POST /api/auth/refresh-token`
    *   Description: Refresh an expired access token using a refresh token (sent via HttpOnly cookie).
-   **Logout:**
    *   `POST /api/auth/logout`
    *   Description: Invalidate the current access token and clear authentication cookies.

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
    *   `POST /api/movies/delete-film?id={movieId}`
    *   Description: Deletes a movie by its ID.
    *   Authentication: Requires `ADMIN` role.
-   **Get All Movies Paged:**
    *   `GET /api/movies/list-movies?page={page}&size={size}`
    *   Description: Returns a paginated list of all movies.
    *   Authentication: None required.

### Banner Endpoints

-   **Create Banner (Admin Only):**
    *   `POST /api/banner/create`
    *   Description: Creates a new banner.
    *   Authentication: Requires `ADMIN` role.
-   **Update Banner (Admin Only):**
    *   `PUT /api/banner/update/{id}`
    *   Description: Updates an existing banner.
    *   Authentication: Requires `ADMIN` role.
-   **Get Active Banners:**
    *   `GET /api/banner/active`
    *   Description: Retrieves a list of currently active banners.
    *   Authentication: None required.
-   **Delete Banner (Admin Only):**
    *   `DELETE /api/banner/delete/{id}`
    *   Description: Deletes a banner by marking it as inactive.
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