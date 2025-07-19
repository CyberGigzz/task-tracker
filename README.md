# Task Tracker API

A RESTful Spring Boot application designed for managing projects and tasks with a robust Role-Based Access Control (RBAC) system. This project demonstrates best practices in modern Java backend development, including clean architecture, JWT-based security, and comprehensive API documentation.

## Table of Contents

- [Task Tracker API](#task-tracker-api)
  - [Table of Contents](#table-of-contents)
  - [Key Features](#key-features)
  - [Tech Stack](#tech-stack)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Configuration](#configuration)
    - [Running the Application](#running-the-application)
  - [API Documentation](#api-documentation)
    - [Swagger UI](#swagger-ui)
    - [H2 Console](#h2-console)
  - [Roles and Permissions](#roles-and-permissions)
  - [Authentication](#authentication)
  - [API Endpoints](#api-endpoints)
  - [Testing](#testing)
    - [1. Running Tests from the Command Line (Recommended for CI/CD \& Consistency)](#1-running-tests-from-the-command-line-recommended-for-cicd--consistency)
    - [2. Running Tests from an Integrated Development Environment (IDE)](#2-running-tests-from-an-integrated-development-environment-ide)

## Key Features

- ✅ **Full CRUD Operations:** For Users, Projects, and Tasks.
- ✅ **JWT-Based Security:** Secure, stateless authentication using JSON Web Tokens.
- ✅ **Role-Based Access Control (RBAC):** Three distinct user roles (`ADMIN`, `MANAGER`, `USER`) with granular permissions.
- ✅ **Pagination and Filtering:** Task lists can be easily paginated and filtered by status or priority.
- ✅ **Global Exception Handling:** Centralized and consistent error responses for a clean API experience.
- ✅ **Automatic Data Seeding:** The application populates the database with realistic sample data on startup for easy development and testing.
- ✅ **API Documentation:** Automatically generated and interactive API documentation via Swagger UI.
- ✅ **Unit Tested:** Key business logic in the service layer is covered by unit tests using JUnit 5 and Mockito.

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.3**
  - **Spring Web:** For building RESTful APIs.
  - **Spring Data JPA:** For database interaction with Hibernate.
  - **Spring Security:** For authentication and authorization.
- **Database:**
  - **H2 (In-Memory):** For development and testing.
  - **PostgreSQL:** Driver included for easy transition to a production database.
- **Tooling:**
  - **Maven:** For project and dependency management.
  - **Lombok:** To reduce boilerplate code.
  - **MapStruct:** For high-performance DTO-to-Entity mapping.
- **API & Documentation:**
  - **Springdoc OpenAPI (Swagger):** For generating interactive API documentation.
- **Testing:**
  - **JUnit 5 & Mockito:** For unit testing the service layer.

## Getting Started

Follow these instructions to get the project up and running on your local machine.

### Prerequisites

- **JDK 17** or newer.
- **Apache Maven** 3.6 or newer(You don't really need Maven to run this).
- An IDE of your choice (e.g., IntelliJ IDEA, VS Code, Eclipse).

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/CyberGigzz/task-tracker.git
    cd task-tracker
    ```


### Configuration

The main configuration is located in `src/main/resources/application.properties`.

- **JWT Secret:** The `app.jwt.secret` key is used for signing tokens. The default value is for development only. For a production environment, this should be replaced with a strong, securely stored secret.
- **Database:** The application is configured to use the H2 in-memory database by default. Configuration for a PostgreSQL database is also included and can be enabled by changing the `spring.datasource` properties.

### Running the Application

You can run the application in two ways:

1.  **Using the Maven Spring Boot plugin:**
    ```bash
    mvn spring-boot:run
    ```

2.  **From your IDE:**
    - Open the project in your IDE.
    - Locate the `Application.java` class in `src/main/java/com/example/tasktracker`.
    - Run the `main` method.

The application will start on `http://localhost:8080`.

## API Documentation

### Swagger UI

Once the application is running, you can access the interactive Swagger UI documentation at:
- **URL:** `http://localhost:8080/swagger-ui.html`

Here you can view all available endpoints, see their request/response structures, and test them directly from your browser.

### H2 Console

For development, you can access the H2 in-memory database console to view the tables and data:
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:tasktrackerdb`
- **Username:** `sa`
- **Password:** `password`

## Roles and Permissions

The application uses a simple but effective RBAC model.

| Role      | Permissions                                                                                             |
| :-------- | :------------------------------------------------------------------------------------------------------ |
| **ADMIN** | Full access to all resources (future-proofed). Currently has the same permissions as a MANAGER.           |
| **MANAGER** | - Can create, read, update, and delete projects they own.<br>- Can create, read, update, and delete tasks within their projects.<br>- Can assign tasks to any user. |
| **USER**    | - Can view projects and tasks.<br>- Can only update the **status** of tasks that are **assigned to them**. |

## Authentication

Authentication is handled via JWT.

1.  Send a `POST` request to `/auth/login` with your email and password.
2.  The API will return a JWT token upon successful authentication.
3.  For all subsequent requests to protected endpoints, include the token in the `Authorization` header.
    ```
    Authorization: Bearer <your_jwt_token>
    ```

The `DataLoader` creates several sample users with the password `password123`. For example:
- `manager1@example.com` (MANAGER)
- `user.dev1@example.com` (USER)

## API Endpoints

A summary of the main API endpoints:

| Method | Endpoint                    | Description                           | Required Role(s) |
| :----- | :-------------------------- | :------------------------------------ | :--------------- |
| `POST` | `/auth/register`            | Register a new user.                  | Public           |
| `POST` | `/auth/login`               | Log in to get a JWT.                  | Public           |
| `POST` | `/api/projects`             | Create a new project.                 | `MANAGER`        |
| `GET`  | `/api/projects`             | Get a list of all projects.           | `MANAGER`, `ADMIN` |
| `GET`  | `/api/projects/{id}`        | Get a single project by ID.           | `MANAGER`, `ADMIN` |
| `PUT`  | `/api/projects/{id}`        | Update a project.                     | `MANAGER`        |
| `DELETE`|`/api/projects/{id}`        | Delete a project.                     | `MANAGER`        |
| `POST` | `/api/tasks`                | Create a new task in a project.       | `MANAGER`        |
| `GET`  | `/api/tasks/project/{id}`   | Get tasks for a project (paginated).  | Authenticated    |
| `GET`  | `/api/tasks/{id}`           | Get a single task by ID.              | Authenticated    |
| `PUT`  | `/api/tasks/{id}`           | Update a task's details.              | `MANAGER`        |
| `PATCH`| `/api/tasks/{id}/status`    | Update only the status of a task.     | `USER` (assigned)  |
| `DELETE`|`/api/tasks/{id}`           | Delete a task.                        | `MANAGER`        |


## Testing

This project is equipped with a comprehensive suite of automated tests to ensure reliability and correctness. The tests are primarily focused on the **service layer**, utilizing **JUnit 5** for the testing framework and **Mockito** for mocking dependencies, allowing for isolated verification of business logic without requiring a live database or web server.

There are two main ways to execute these tests:

### 1. Running Tests from the Command Line (Recommended for CI/CD & Consistency)

The most consistent way to run all tests is by using the **Maven Wrapper** scripts included in the project. This method ensures that everyone uses the exact same Maven version, leading to reproducible build and test results, making it ideal for continuous integration/continuous deployment (CI/CD) pipelines.

* **To run all unit and integration tests:**

    * **For Linux/macOS:**
        ```bash
        ./mvnw test
        ```
    * **For Windows (Command Prompt):**
        ```cmd
        mvnw test
        ```
    * **For Windows (PowerShell):**
        ```powershell
        ./mvnw test
        ```

* **To skip tests during a full build (e.g., if you only want to compile and package the application quickly):**

    * **For Linux/macOS:**
        ```bash
        ./mvnw clean install -DskipTests
        ```
    * **For Windows (Command Prompt):**
        ```cmd
        mvnw clean install -DskipTests
        ```
    * **For Windows (PowerShell):**
        ```powershell
        ./mvnw clean install -DskipTests
        ```

### 2. Running Tests from an Integrated Development Environment (IDE)

If you are working within an IDE, you can leverage its built-in testing capabilities for a more interactive and focused testing experience, which is great for debugging and local development.

* **Run all tests in the project:**
    After importing the project into your IDE (e.g., IntelliJ IDEA, Eclipse with STS, VS Code with Java extensions), navigate to the `src/test/java` directory in your project explorer. Right-click on the root test package (e.g., `com.example.tasktracker` or `com.example.tasktracker.service`) and select the option to "Run Tests" or "Run All Tests".

* **Run a specific test class:**
    Open the test class you wish to run (e.g., `UserServiceTest.java`). You can typically right-click on the class name within the editor or in the project explorer and select "Run 'ClassNameTest'".

* **Run a specific test method:**
    For targeted testing or debugging, you can run an individual test method. Open the test class, right-click directly on the `@Test` annotated method you want to execute, and select "Run 'methodName'".
