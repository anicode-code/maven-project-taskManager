# Task Manager API

Spring Boot REST API for managing tasks, featuring CRUD endpoints, validation and H2 in-memory persistence.

## Getting Started

```bash
mvn spring-boot:run
```

API becomes available at `http://localhost:8080`.

## Running Tests with Coverage

Jacoco is configured through `jacoco-maven-plugin`. Generate a coverage report with:

```bash
mvn clean verify
```

Open `target/site/jacoco/index.html` in a browser to review coverage details.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA & H2 Database
- JUnit 5 & Mockito for testing
