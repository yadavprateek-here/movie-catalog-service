# Movie Catalog Service

> **Director & Film Management REST API**  
> Developed for SymphonyAI Media - Associate Software Engineer (Java) Assessment.

---

## 📌 Overview

The **Movie Catalog Service** is a production-grade Spring Boot REST API built to manage movies, directors, and their genre associations. The service features in-memory H2 database persistence, JPA/Hibernate relationship mappings, full CRUD operations, externalized business configuration, bean validation, custom global error handling, and comprehensive unit and integration tests.

---

## 🛠️ Tech Stack & Key Conventions

- **Java**: 17+ (tested with Java 17 and 25)
- **Framework**: Spring Boot 3.3.4
- **ORM & Data**: Spring Data JPA / Hibernate
- **Database**: In-memory H2 Database (`/h2-console`)
- **API Documentation**: OpenAPI 3.0 specification (`openapi.yaml`) & Swagger UI (`/swagger-ui.html`)
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Testing**: JUnit 5, Mockito, Spring Boot Test, MockMvc
- **Containerization**: Docker & Docker Compose

---

## 🏗️ Architecture & Domain Design

```
   ┌──────────────┐             1 : N             ┌──────────────┐
   │   Director   │ ────────────────────────────< │    Movie     │
   └──────────────┘                               └──────────────┘
                                                         │
                                                     M : N (movie_genre)
                                                         │
                                                  ┌──────────────┐
                                                  │    Genre     │
                                                  └──────────────┘
```

### Layered Architecture
- **Controllers**: Expose REST endpoints, handle request mapping, Bean validation (`@Valid`), and HTTP response codes.
- **DTOs & Mappers**: Strict separation between internal JPA entities and API contracts. Prevents infinite JSON serialization loops and leaks.
- **Service Layer**: Encapsulates business logic, transactional integrity (`@Transactional`), and rule enforcement.
- **Repositories**: Spring Data JPA interfaces with custom JPQL queries and fetch-joins to eliminate N+1 query problems.
- **Global Exception Handler**: Centralized `@RestControllerAdvice` returning structured error payloads with HTTP status codes (400, 404, 409, 500).

---

## ⚙️ Externalized Configuration (`application.properties`)

The following values are externalized in `src/main/resources/application.properties` and injected via `@ConfigurationProperties` and `@Value`:

| Property Key | Default Value | Description |
|---|---|---|
| `app.name` | `Movie Catalog Service` | Application name, exposed via `GET /api/info` |
| `app.version` | `1.0.0` | Application version, exposed via `GET /api/info` |
| `catalog.max-genres-per-movie` | `5` | Maximum number of genres allowed per movie (enforced on create/update/associate) |
| `catalog.default-rating` | `5.0` | Default rating applied when a movie is created without a rating |

---

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.8+ (or use system maven)
- Git

### 1. Build & Run Locally
Clone the repository and run:
```bash
# Compile and run unit + integration tests
mvn clean test

# Package the application
mvn clean package -DskipTests

# Run the Spring Boot application
mvn spring-boot:run
```
The server will start on port `8080` (`http://localhost:8080`).

### 2. Run with Docker
```bash
# Build and run using Docker Compose
docker compose up --build
```

---

## 🌐 Interactive Documentation & Console

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **OpenAPI YAML Spec File**: Located at [`openapi.yaml`](./openapi.yaml) at the project root.
- **H2 Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:moviecatalogdb`
  - User: `sa`
  - Password: *(leave blank)*

---

## 🧪 Running Automated Tests

To execute all unit tests and integration tests:
```bash
mvn test
```
The test suite includes:
- **`DirectorServiceTest`**: Unit tests for director CRUD logic with Mockito.
- **`GenreServiceTest`**: Unit tests for genre creation, uniqueness enforcement, and retrieval.
- **`MovieServiceTest`**: Unit tests for movie business logic, max genre limits, default rating fallback, and associations.
- **`MovieCatalogIntegrationTest`**: End-to-end integration test validating HTTP flows, JSON contracts, and error responses using `@SpringBootTest` and `MockMvc`.

---

## 📡 Sample cURL Requests

### 1. Application Info
```bash
curl -X GET http://localhost:8080/api/info
```

### 2. Directors
```bash
# List all directors
curl -X GET http://localhost:8080/api/directors

# Create a new director
curl -X POST http://localhost:8080/api/directors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Christopher Nolan",
    "nationality": "British-American",
    "birthYear": 1970
  }'

# Get director by ID
curl -X GET http://localhost:8080/api/directors/1

# Update a director
curl -X PUT http://localhost:8080/api/directors/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sir Christopher Nolan",
    "nationality": "British-American",
    "birthYear": 1970
  }'

# Get all movies by a director
curl -X GET http://localhost:8080/api/directors/1/movies

# Delete a director
curl -X DELETE http://localhost:8080/api/directors/1
```

### 3. Genres
```bash
# List all genres
curl -X GET http://localhost:8080/api/genres

# Create a genre
curl -X POST http://localhost:8080/api/genres \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sci-Fi"
  }'

# Get genre by ID
curl -X GET http://localhost:8080/api/genres/1
```

### 4. Movies
```bash
# Create a movie with explicit rating and genres
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Inception",
    "releaseYear": 2010,
    "rating": 8.8,
    "directorId": 1,
    "genreIds": [1, 2]
  }'

# Create a movie without rating (verifies default rating fallback to 5.0)
curl -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Oppenheimer",
    "releaseYear": 2023,
    "directorId": 1,
    "genreIds": [2]
  }'

# List all movies
curl -X GET http://localhost:8080/api/movies

# Filter movies by genre and release year
curl -X GET "http://localhost:8080/api/movies?genre=Sci-Fi&year=2010"

# Get movie by ID with genres
curl -X GET http://localhost:8080/api/movies/1

# Update movie details
curl -X PUT http://localhost:8080/api/movies/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Inception - Director Cut",
    "releaseYear": 2010,
    "rating": 9.0
  }'

# Associate an additional genre with a movie
curl -X POST http://localhost:8080/api/movies/1/genres/3

# Remove a genre from a movie
curl -X DELETE http://localhost:8080/api/movies/1/genres/3

# Delete a movie
curl -X DELETE http://localhost:8080/api/movies/1
```

---

## 🛡️ Error Handling Response Format

All API errors return a standard JSON structure:
```json
{
  "timestamp": "2026-10-24T21:40:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more request parameters failed validation",
  "path": "/api/directors",
  "fieldErrors": {
    "name": "Name is required"
  }
}
```
Common HTTP status codes used:
- `200 OK` / `201 Created` / `204 No Content`
- `400 Bad Request`: Validation failures and business rule violations (e.g., maximum genres exceeded)
- `404 Not Found`: Resource does not exist
- `409 Conflict`: Duplicate unique resources (e.g., genre name already taken)
- `500 Internal Server Error`: Unhandled server-side exceptions

---

## 📁 Repository Structure
```
prateek-workspace/
├── Dockerfile
├── docker-compose.yml
├── openapi.yaml
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/symphonyai/moviecatalog/
    │   │   ├── MovieCatalogApplication.java
    │   │   ├── config/
    │   │   │   ├── CatalogProperties.java
    │   │   │   ├── OpenApiConfig.java
    │   │   │   └── SampleDataInitializer.java
    │   │   ├── controller/
    │   │   │   ├── DirectorController.java
    │   │   │   ├── GenreController.java
    │   │   │   ├── InfoController.java
    │   │   │   └── MovieController.java
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   └── response/
    │   │   ├── entity/
    │   │   │   ├── Director.java
    │   │   │   ├── Genre.java
    │   │   │   └── Movie.java
    │   │   ├── exception/
    │   │   │   ├── BusinessValidationException.java
    │   │   │   ├── DuplicateResourceException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── ResourceNotFoundException.java
    │   │   ├── mapper/
    │   │   │   └── EntityMapper.java
    │   │   ├── repository/
    │   │   │   ├── DirectorRepository.java
    │   │   │   ├── GenreRepository.java
    │   │   │   └── MovieRepository.java
    │   │   └── service/
    │   │       ├── DirectorService.java
    │   │       ├── GenreService.java
    │   │       ├── MovieService.java
    │   │       └── impl/
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/symphonyai/moviecatalog/
            ├── MovieCatalogIntegrationTest.java
            └── service/
                ├── DirectorServiceTest.java
                ├── GenreServiceTest.java
                └── MovieServiceTest.java
```
