# Spring Boot Authentication Sample

This is a sample authentication project built using Spring Boot. It demonstrates how to implement authentication and authorization using Spring Security, JWT, and a database-backed user management system.

## Features

- User Registration & Login
- Password Encryption (BCrypt)
- JWT-based Authentication
- Role-based Authorization
- Spring Security Configuration
- Database Persistence (JPA & Hibernate)
- RESTful API Endpoints

## Technologies Used

- Spring Boot
- Spring Security
- Spring Data JPA
- H2
- JSON Web Tokens (JWT)

## Getting Started

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/ivanDsky/cmde.git
   cd cmde
   ```
2. Configure environmental variables in `.env`:
   ```properties
    SPRING_DATASOURCE_URL=jdbc:h2:file:./data/testdb
    SPRING_DATASOURCE_USERNAME=sa
    SPRING_DATASOURCE_PASSWORD=password
    
    SPRING_MAIL_USERNAME=
    SPRING_MAIL_PASSWORD=
    
    JWT_SECRET=
    JWT_EXPIRATION=
    
    GITHUB_CLIENT_ID=
    GITHUB_CLIENT_SECRET=
    
    GOOGLE_CLIENT_ID=
    GOOGLE_CLIENT_SECRET=
   ```
3. Build and run the project:
   ```sh
   ./gradlew bootRun
   ```

### Testing Authentication

1. Register a new user by sending a `POST` request to `/auth/sign-up` with the following JSON:
   ```json
   {
     "username": "testuser",
     "email": "test@mail.com",
     "password": "password123"
   }
   ```
2. Verify email of a user sending a `POST` request to `/auth/verify` with verification key that was sent on provided email:
   ```json
   {
     "email": "test@mail.com",
     "verificationCode": "123456"
   }
   ```
3. Authenticate the user by sending a `POST` request to `/auth/login` with the same credentials.
4. Use the returned JWT token in the `Authorization` header (`Bearer <token>`) to access protected endpoints.

