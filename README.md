# Cinebee - Backend API

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)](https://www.docker.com/)

This repository contains the backend source code for **Cinebee**, a comprehensive movie ticket booking platform. It is built with Spring Boot and provides a complete set of RESTful APIs designed to power client applications (Web and Mobile).

## Core Features

-    **Secure User Authentication**: JWT-based login, registration, and session management. Includes Google OAuth2 integration, password reset via OTP, and token refresh capabilities.
-    **Role-Based Access Control (RBAC)**: Differentiated permissions for `USER` and `ADMIN` roles using Spring Security.
-    **Complete Movie & Showtime Management**: Full CRUD operations for movies, theaters, rooms, and showtimes.
-    **End-to-End Booking Flow**: A seamless process for users to select seats, create a booking, and receive a confirmation.
-    **Payment Gateway Integration**: Integrated with **MoMo** for online payments, including handling of IPN (Instant Payment Notification) and return URLs.
-    **Automated Email & QR Code Generation**: Sends HTML-based ticket confirmation emails (using Thymeleaf) with a **QR Code** (using ZXing) for each ticket.
-    **Cloud Media Storage**: Utilizes **Cloudinary** for efficient storage and delivery of media assets like movie posters and banners.
-    **Performance Optimization**: Leverages **Redis** for caching frequently accessed data (e.g., trending movies, active banners) and for temporary storage of OTPs and CAPTCHA codes.
-    **Enhanced Security**: Employs image-based **Kaptcha** for CAPTCHA verification to prevent automated abuse.

## Tech Stack

| Category               | Technology                                                |
| ---------------------- | --------------------------------------------------------- |
| **Backend Framework**  | Spring Boot 3.3.3                                         |
| **Language**           | Java 21                                                   |
| **Database**           | Spring Data JPA, Hibernate, MySQL                         |
| **Security**           | Spring Security, JWT (`jjwt`), OAuth2 (`nimbus-jose-jwt`) |
| **Caching & Session**  | Redis, Spring Cache                                       |
| **Media Storage**      | Cloudinary                                                |
| **Payment**            | MoMo API                                                  |
| **Email**              | Jakarta Mail, Thymeleaf (for HTML templates)              |
| **Image Generation**   | Kaptcha (CAPTCHA), Google ZXing (QR Code)                 |
| **Build Tool**         | Apache Maven                                              |
| **Containerization**   | Docker, Docker Compose                                    |
| **Environment Config** | DotEnv                                                    |

## Setup and Configuration

### 1. Prerequisites

-    JDK 21
-    Apache Maven 3.8+
-    MySQL 8.0+
-    Redis 6.2+
-    Docker & Docker Compose (optional)

### 2. Clone the Repository

```bash
git clone https://github.com/Olin2004/Cinebee-BackEnd.git
cd Cinebee-BackEnd
```

### 3. Configure Environment Variables

This project uses a `.env` file to manage secrets and environment-specific configuration. Create a file named `.env` in the root directory of the project.

**`.env` file contents:**

```env
# Database Configuration
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password

# Redis Configuration (optional, leave empty if no password)
REDIS_PASSWORD=

# Email Configuration
MAIL_USERNAME=your_gmail_address@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

# MoMo Payment Gateway Configuration
MOMO_PARTNER_CODE=your_momo_partner_code
MOMO_ACCESS_KEY=your_momo_access_key
MOMO_SECRET_KEY=your_momo_secret_key

# reCAPTCHA (if used)
RECAPTCHA_SECRET=your_recaptcha_secret
```

_Note: The `application.yml` file will automatically import these variables._

### 4. Database Setup

Create a MySQL database named `cinebee`:

```sql
CREATE DATABASE cinebee CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 5. Build and Run

**Option A: Using Docker Compose (Redis only)**
The `docker-compose.yml` file provides Redis service. Start Redis first:

```bash
docker-compose up -d
```

Then run the application locally:

```bash
mvn spring-boot:run
```

**Option B: Running Locally with Maven**
Ensure your MySQL and Redis instances are running locally, then:

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

All protected endpoints require a JWT `Bearer` token in the `Authorization` header.

### Authentication (`/api/auth`)

| Method | Endpoint           | Description                                   | Access |
| :----- | :----------------- | :-------------------------------------------- | :----- |
| `POST` | `/login`           | Authenticate a user and get tokens.           | Public |
| `POST` | `/register`        | Create a new user account.                    | Public |
| `GET`  | `/captcha`         | Get a new CAPTCHA image and key.              | Public |
| `POST` | `/google`          | Authenticate using a Google ID token.         | Public |
| `POST` | `/forgot-password` | Request a password reset OTP via email.       | Public |
| `POST` | `/verify-otp`      | Verify the OTP for password reset.            | Public |
| `POST` | `/reset-password`  | Set a new password using a temporary token.   | Public |
| `POST` | `/refresh-token`   | Get a new access token using a refresh token. | User   |
| `POST` | `/logout`          | Log out the user.                             | User   |

### User Profile (`/api`)

| Method | Endpoint   | Description                                      | Access |
| :----- | :--------- | :----------------------------------------------- | :----- |
| `GET`  | `/profile` | Get the profile of the currently logged-in user. | User   |

### Movies (`/api/movies`)

| Method | Endpoint        | Description                            | Access |
| :----- | :-------------- | :------------------------------------- | :----- |
| `GET`  | `/trending`     | Get a list of top 10 trending movies.  | Public |
| `GET`  | `/search`       | Search for movies by title.            | Public |
| `GET`  | `/list-movies`  | Get a paginated list of all movies.    | Admin  |
| `GET`  | `/clear-cache`  | Clear trending movies cache.           | Public |
| `POST` | `/add-new-film` | Add a new movie (multipart/form-data). | Admin  |
| `POST` | `/update-film`  | Update a movie (multipart/form-data).  | Admin  |
| `POST` | `/delete-film`  | Delete a movie.                        | Admin  |

### Banners (`/api/banner`)

| Method   | Endpoint                   | Description                               | Access |
| :------- | :------------------------- | :---------------------------------------- | :----- |
| `GET`    | `/active`                  | Get all active banners for the homepage.  | Public |
| `GET`    | `/all`                     | Get all banners, including inactive ones. | Admin  |
| `POST`   | `/add-banner`              | Add a new banner.                         | Admin  |
| `POST`   | `/update-banner/{movieId}` | Update or create banner for a movie.      | Admin  |
| `DELETE` | `/delete-banner/{id}`      | Delete a banner.                          | Admin  |

### Theaters (`/api/v1/theaters`)

| Method   | Endpoint | Description                           | Access |
| :------- | :------- | :------------------------------------ | :----- |
| `GET`    | `/`      | Get a paginated list of all theaters. | Public |
| `GET`    | `/{id}`  | Get details for a single theater.     | Public |
| `POST`   | `/`      | Create a new theater.                 | Admin  |
| `PUT`    | `/{id}`  | Update an existing theater.           | Admin  |
| `DELETE` | `/{id}`  | Soft-delete a theater.                | Admin  |

### Showtimes (`/api/v1/showtimes`)

| Method   | Endpoint                               | Description                                     | Access |
| :------- | :------------------------------------- | :---------------------------------------------- | :----- |
| `GET`    | `/`                                    | Get all showtimes with pagination.              | Public |
| `GET`    | `/{id}`                                | Get details for a single showtime.              | Public |
| `GET`    | `/movie/{movieId}`                     | Get all showtimes for a specific movie.         | Public |
| `GET`    | `/theater/{theaterId}`                 | Get all showtimes for a specific theater.       | Public |
| `GET`    | `/movie/{movieId}/theater/{theaterId}` | Get showtimes for movie in specific theater.    | Public |
| `GET`    | `/date/{date}`                         | Get showtimes for a specific date.              | Public |
| `GET`    | `/{id}/seats`                          | Get available seats for a showtime.             | Public |
| `GET`    | `/booking`                             | Get showtimes filtered by movie, theater, date. | Public |
| `POST`   | `/`                                    | Create a new showtime.                          | Admin  |
| `PUT`    | `/{id}`                                | Update an existing showtime.                    | Admin  |
| `DELETE` | `/{id}`                                | Delete a showtime.                              | Admin  |

### Tickets & Booking (`/api/v1/tickets`)

| Method   | Endpoint                        | Description                                  | Access |
| :------- | :------------------------------ | :------------------------------------------- | :----- |
| `GET`    | `/showtimes/{showtimeId}/seats` | Get available seats for a showtime.          | Public |
| `POST`   | `/book`                         | Create a booking (returns MoMo payment URL). | User   |
| `GET`    | `/my-bookings`                  | Get the current user's booking history.      | User   |
| `GET`    | `/{ticketId}`                   | Get details for a specific booking.          | User   |
| `DELETE` | `/{ticketId}`                   | Cancel a booking (if not yet paid).          | User   |

## Project Structure

```
src/
├── main/
│   ├── java/com/cinebee/
│   │   ├── CineBeeApplication.java          # Main Spring Boot application
│   │   ├── common/                          # Enums and constants
│   │   │   ├── Role.java
│   │   │   └── UserStatus.java
│   │   ├── config/                          # Configuration classes
│   │   │   ├── CacheConfig.java            # Redis cache configuration
│   │   │   ├── CaptchaConfig.java          # Kaptcha CAPTCHA configuration
│   │   │   ├── CloudinaryConfig.java       # Cloudinary media storage
│   │   │   ├── JwtConfig.java              # JWT token configuration
│   │   │   ├── MomoConfig.java             # MoMo payment gateway
│   │   │   ├── RestTemplateConfig.java     # HTTP client configuration
│   │   │   └── SecurityConfig.java         # Spring Security configuration
│   │   ├── controller/                      # REST API controllers
│   │   │   ├── AuthController.java         # Authentication endpoints
│   │   │   ├── BannerController.java       # Banner management
│   │   │   ├── CaptchaController.java      # CAPTCHA generation
│   │   │   ├── LoginController.java        # Login endpoint
│   │   │   ├── MovieController.java        # Movie management
│   │   │   ├── PaymentController.java      # Payment processing
│   │   │   ├── ProfileController.java      # User profile
│   │   │   ├── RegisterController.java     # User registration
│   │   │   ├── ShowtimeController.java     # Showtime management
│   │   │   ├── TheaterController.java      # Theater management
│   │   │   └── TicketController.java       # Ticket booking
│   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── request/                    # Request DTOs
│   │   │   └── response/                   # Response DTOs
│   │   ├── entity/                         # JPA Entities
│   │   │   ├── Banner.java
│   │   │   ├── Comment.java
│   │   │   ├── Movie.java
│   │   │   ├── Payment.java
│   │   │   ├── Room.java
│   │   │   ├── RoomSeat.java
│   │   │   ├── Seat.java
│   │   │   ├── Showtime.java
│   │   │   ├── Theater.java
│   │   │   ├── Ticket.java
│   │   │   ├── Trailer.java
│   │   │   └── User.java
│   │   ├── exception/                      # Exception handling
│   │   │   ├── ApiException.java
│   │   │   ├── ErrorCode.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── mapper/                         # Entity to DTO mappers
│   │   │   ├── BannerMapper.java
│   │   │   ├── MovieMapper.java
│   │   │   └── UserMapper.java
│   │   ├── repository/                     # JPA Repositories
│   │   │   ├── BannerRepository.java
│   │   │   ├── CommentRepository.java
│   │   │   ├── MovieRepository.java
│   │   │   ├── PaymentRepository.java
│   │   │   ├── RoomRepository.java
│   │   │   ├── SeatRepository.java
│   │   │   ├── ShowtimeRepository.java
│   │   │   ├── TheaterRepository.java
│   │   │   ├── TicketRepository.java
│   │   │   └── UserRepository.java
│   │   ├── scheduler/                      # Scheduled tasks
│   │   ├── security/                       # Security implementations
│   │   ├── service/                        # Business logic services
│   │   └── util/                          # Utility classes
│   └── resources/
│       ├── application.yml                # Application configuration
│       └── templates/
│           └── ticket-confirmation.html   # Email template
├── docker-compose.yml                     # Docker services (Redis)
├── pom.xml                               # Maven dependencies
└── README.md                             # Project documentation
```

## Features

### ✅ Implemented Features

-    [x] User Authentication (Register, Login, JWT)
-    [x] Google OAuth2 Integration
-    [x] Password Reset with OTP
-    [x] Role-based Access Control (USER, ADMIN)
-    [x] Movie Management (CRUD)
-    [x] Theater Management (CRUD)
-    [x] Showtime Management (CRUD)
-    [x] Seat Selection and Booking
-    [x] MoMo Payment Integration
-    [x] Email Notifications with QR Code
-    [x] Banner Management
-    [x] CAPTCHA Security
-    [x] Redis Caching
-    [x] Cloudinary Media Storage
-    [x] Global Exception Handling

### 🚧 Planned Features

-    [ ] Comment and Rating System
-    [ ] Advanced Movie Search and Filtering
-    [ ] Loyalty Points System
-    [ ] Multiple Payment Gateways
-    [ ] Real-time Notifications
-    [ ] Advanced Analytics Dashboard

## Development Notes

### Database Configuration

-    Database name: `cinebee`
-    Port: 3306 (MySQL default)
-    Character set: `utf8mb4_unicode_ci`
-    Hibernate DDL: `update` (auto-creates/updates tables)

### Security Configuration

-    JWT expiration: 1 hour (3600000 ms)
-    Refresh token stored in HTTP-only cookies
-    CORS enabled for `http://localhost:3000`
-    Protected routes require `Bearer` token

### Payment Configuration

-    MoMo test environment endpoint
-    IPN (Instant Payment Notification) webhook: `/api/v1/payments/momo/ipn`
-    Return URL after payment: `/api/v1/payments/momo/return`

### Cache Configuration

-    Redis used for caching trending movies
-    CAPTCHA codes stored with TTL
-    OTP codes stored temporarily

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

-    **Project Repository**: [https://github.com/Olin2004/Cinebee-BackEnd](https://github.com/Olin2004/Cinebee-BackEnd)
-    **Issues**: [https://github.com/Olin2004/Cinebee-BackEnd/issues](https://github.com/Olin2004/Cinebee-BackEnd/issues)
