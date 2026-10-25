# SkillSwap — Skill Exchange Platform

A web application built with **Java, Spring Boot, Maven, and Docker**, with a Jenkins CI/CD pipeline for automated building and container image creation.

## Project Overview

SkillSwap is a skill-exchange platform project designed to support sharing and exchanging skills. The project demonstrates Java web application development, containerization, source code management, and continuous integration using modern development tools.

Key Highlights

- **Spring Boot:** Backend application development.
- **Java 21:** Application runtime and development.
- **Maven:** Dependency management and application builds.
- **Docker:** Containerization of the application.
- **Jenkins:** CI/CD pipeline automation.
- **GitHub:** Source code hosting and version control.
- **Cloud deployment:** Public hosting setup in progress.

##  Technology Stack

| Technology | Purpose |
| Java 21 | Programming language |
| Spring Boot | Backend application framework |
| Maven | Build and dependency management |
| Docker | Application containerization |
| Jenkins | Continuous integration pipeline |
| Git and GitHub | Version control and source code hosting |
| Render | Intended cloud hosting platform |

## Project Structure


Skill_Swap/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
├── Dockerfile
├── Jenkinsfile
└── README.md


##  Prerequisites

Install the following tools before running the project locally:

- Java Development Kit (JDK) 21
- Apache Maven, or use the project's Maven Wrapper if available
- Git
- Docker Desktop
- Jenkins (required for running the CI/CD pipeline)

##  Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/mantrilikhita11127-rgb/Skill_Swap.git
cd Skill_Swap
```

### 2. Build the application

Using Maven:

```bash
mvn clean package -DskipTests
```

This command compiles the project and packages the application into a JAR file.

### 3. Run the application locally

```bash
java -jar target/skillswap-0.0.1-SNAPSHOT.jar
```

After the application starts, open:

http://localhost:8080

The application port depends on the Spring Boot configuration.

##  Running with Docker

The project includes a Dockerfile for packaging the Spring Boot application into a container image.

### Build the Docker image

```bash
docker build -t skillswap:1.0 .
```

### Run the container

```bash
docker run -d --name skillswap-app -p 9092:8080 skillswap:1.0
```

Open the application at:

http://localhost:9092

### Useful Docker commands

Check running
