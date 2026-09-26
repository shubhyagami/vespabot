[K[2m  [2mmodel z-ai/glm-5.3-flash failed, trying next...[0m[0m
# VESPA – Delivery Robot Monitoring Dashboard

A lightweight, real-time dashboard for visualizing telemetry from delivery robot fleets in smart warehouse environments. VESPA ingests telemetry over STOMP/SockJS WebSockets, persists it in a relational database, and exposes REST and WebSocket endpoints for client consumption.

![Java 17](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![Maven Central](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)
![Docker Pulls](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow)

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Usage](#usage)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)

---

## Overview

VESPA acts as the central telemetry hub for robot fleets, giving warehouse operators live streaming data, historical records, and a monitoring interface.

**Core workflow:**

1. **Ingestion:** Receives real-time telemetry packets from robots over WebSockets.
2. **Persistence:** Stores telemetry in MySQL (production) or H2 (development).
3. **Distribution:** Broadcasts live updates to the frontend and provides a REST API for historical queries.
4. **Visualization:** Displays robot positions, health metrics, and sensor data through a Thymeleaf dashboard.

---

## Features

| Category | Functionality |
|----------|---------------|
| **Mapping** | Interactive Leaflet map with real-time robot movement and path traces |
| **Analytics** | Battery level, speed, and task progress charts powered by Chart.js |
| **Sensors** | Real-time RFID scans, ultrasonic distance monitoring, and obstacle alerts |
| **Health** | Connection status (online/offline) and low-battery warnings |
| **API** | JSON endpoints for historical telemetry retrieval |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven (wrapper included)
- Docker (optional, for containerized runs)

### Quick Start

Clone the repository, run it with Maven, and open `http://localhost:8080`.

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
./mvnw spring-boot:run
```

For a containerized setup:

```bash
docker run -p 8080:8080 vespa/vespabot
```

### Local Development

Build a standalone executable JAR:

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### Docker Execution

```bash
docker pull vespa/vespabot
docker run -p 8080:8080 vespa/vespabot
```

---

## Configuration

Settings are managed in `src/main/resources/application.yml`. You can override them with environment variables by converting dotted keys to uppercase with underscores.

**Example configuration:**

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:vespa_db
    username: sa
    password:
vespa:
  telemetry:
    topic: /topic/telemetry
  websocket:
    enabled: true
```

**Environment variable overrides:**

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=secret
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Usage

### Monitoring Dashboard

Open `http://localhost:8080` to view:

- **Live Map:** Real-time coordinates and movement paths.
- **Telemetry Charts:** Trends for battery depletion and speed.
- **Sensor Feed:** Live RFID scans and ultrasonic distance readings.

### REST API

Swagger UI is available at `/swagger-ui.html`.

| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/telemetry` | List historical telemetry records |
| `GET` | `/api/telemetry/{id}` | Retrieve a specific telemetry record by ID |
| `GET` | `/websocket` | Establish a WebSocket connection for live updates |

---

## Architecture

`Robot` → `STOMP/SockJS` → `WebSocket Layer` → `Spring Boot Logic` → `JPA/JDBC` → `Database`

- **WebSocket Layer:** Manages bidirectional communication, data persistence, and broadcasting.
- **REST API:** Provides a stateless interface for querying historical data.
- **Frontend:** Server-side rendered Thymeleaf application using Bootstrap 5, Leaflet.js, and Chart.js.

---

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket
- **Frontend:** Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js
- **Database:** MySQL (production), H2 (dev/test)
- **DevOps:** Maven, Docker, Helm

---

## Deployment

### Docker Standalone

```bash
docker run -d --name vespabot \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vespa/vespabot
```

### Kubernetes (Helm)

```bash
helm repo add vespa https://shubhyagami.github.io/vespabot/charts
helm install vespa-vespabot vespa/vespabot
```

---

## Contributing

1. Fork the repository and create a feature branch.
2. Follow the existing Java and Spring coding conventions.
3. Run `./mvnw test` and ensure all tests pass before submitting.
4. Submit a pull request with a clear description of the changes.
5. Update relevant documentation for any new features.

---

## License

Distributed under the MIT License. See the [LICENSE](LICENSE) file for more information.

---

## Changelog

| Date | Version | Changes |
|------|---------|---------|
| 2026-09-26 | – | Cleaned up README formatting, clarified usage, and organized sections |
| 2026-08-03 | – | Implemented async queue processing for telemetry ingestion |
| 2026-07-01 | – | Optimized MySQL connection pooling; released Helm chart |
