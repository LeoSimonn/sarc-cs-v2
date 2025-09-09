# Student Subject API

## Overview
The Student Subject API is a microservices-based application built with Spring Boot that provides functionalities for managing students and subjects. This API allows for the registration of students, querying students by registration number or name, registering subjects, and enrolling students in subjects.

## Features
- Register students
- Query students by registration number or name
- Register subjects
- Enroll students in subjects

## Project Structure
```
student-subject-api
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── studentsubjectapi
│   │   │               ├── StudentSubjectApiApplication.java
│   │   │               ├── controller
│   │   │               │   ├── StudentController.java
│   │   │               │   └── SubjectController.java
│   │   │               ├── model
│   │   │               │   ├── Student.java
│   │   │               │   └── Subject.java
│   │   │               ├── service
│   │   │               │   ├── StudentService.java
│   │   │               │   └── SubjectService.java
│   │   │               └── repository
│   │   │                   ├── StudentRepository.java
│   │   │                   └── SubjectRepository.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── api
│   │           └── openapi.yaml
├── pom.xml
└── README.md
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd student-subject-api
   ```
3. Build the project using Maven:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn spring-boot:run
   ```

## API Documentation
API endpoints are defined using the OpenAPI specification. You can find the API contract in the `src/main/resources/api/openapi.yaml` file.

## Dependencies
This project uses Maven for dependency management. The `pom.xml` file contains all the necessary dependencies for the Spring Boot application.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.