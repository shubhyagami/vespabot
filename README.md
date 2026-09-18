[K[2m  [2mmodel openai/gpt-oss-20b failed, trying next...[0m[0m
[K[2m  [2mmodel openai/gpt-oss-120b failed, trying next...[0m[0m
# VESPA – Delivery Robot Monitoring Dashboard

A lightweight, real-time dashboard for visualizing telemetry from fleets of delivery robots in smart warehouse environments.

VESPA ingests telemetry via STOMP/SockJS WebSockets, persists the data in a relational database, and exposes both REST and WebSocket endpoints for client-side consumption.

---

## Badges

![Java 17](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![Maven Central](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)
![Docker Pulls](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow)

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Quick Start](#quick-start)
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

VESPA acts as the central telemetry hub for robot fleets. It streams live data, stores historical records for analysis, and provides a comprehensive monitoring interface.

**Core Workflow:**
- **Ingestion:** Receives real-time telemetry from robots via WebSockets.
- **Persistence:** Stores data in MySQL (production) or H2 (development).
- **Distribution:** Pushes live updates to the frontend and provides a REST API for historical queries.
- **Visualization:** Renders robot positions and health metrics via a Thymeleaf-based dashboard.

---

## Features

| Category | Functionality |
|----------|---------------|
| **Mapping** | Interactive Leaflet map with real-time movement traces |
| **Analytics** | Battery level, speed, and task progress graphs via Chart.js |
| **Sensors** | Monitoring for RFID tags, ultrasonic distances, and obstacle alerts |
| **Health** | Online/offline status indicators and low-battery warnings |
| **API** | JSON endpoints for querying historical telemetry data |

---

## Quick Start

### Local Development (Maven)

Clone the repository and run the application using the Maven wrapper:

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
./mvnw spring-boot:run
```

Alternatively, build a fat JAR:

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### Docker Quick Run

```bash
docker pull vespa/vespabot
docker run -p 8080:8080 vespa/vespabot
```

Access the dashboard at `http://localhost:8080`.

---

## Configuration

Configuration is managed via `src/main/resources/application.yml`. You can override these settings using environment variables (convert dotted keys to uppercase with underscores).

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | Database JDBC URL |
| `spring.datasource.username` | `sa` | Database username |
| `spring.datasource.password` | `(empty)` | Database password |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic for robot clients |
| `vespa.websocket.enabled` | `true` | Flag to enable/disable WebSocket endpoints |

**Example Environment Setup:**

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=secret
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Usage

### Monitoring Dashboard
Navigate to `http://localhost:8080` to access the visual interface:
- **Live Map:** Track robot movement and paths in real-time.
- **Telemetry Charts:** Monitor battery depletion and speed fluctuations.
- **Sensor Feed:** View current RFID scans and ultrasonic distance readings.

### REST API
API documentation is available via Swagger at `/swagger-ui.html`.

| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/telemetry` | Retrieve historical telemetry logs |
| `GET` | `/api/telemetry/{id}` | Retrieve a specific telemetry record |
| `GET` | `/websocket` | Establish a WebSocket connection for live updates |

---

## Architecture

`Robot` $\rightarrow$ `STOMP/SockJS` $\rightarrow$ `WebSocket Layer` $\rightarrow$ `Spring Boot Logic` $\rightarrow$ `JPA/JDBC` $\rightarrow$ `Database`

- **WebSocket Layer:** Handles bidirectional communication, persistence, and broadcasting.
- **REST API:** Provides a stateless interface for historical data retrieval.
- **Frontend:** A server-side rendered Thymeleaf application enhanced with Bootstrap and JavaScript libraries.

---

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket
- **Frontend:** Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js
- **Database:** MySQL (Production), H2 (In-memory / Testing)
- **Build/DevOps:** Maven, Docker, Helm

---

## Deployment

### Docker Compose / Standalone
```bash
docker run -d --name vespabot \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vespa/vespabot
```

### Kubernetes (Helm)
Deploy using the official Helm chart:

```bash
helm repo add vespa https://shubhyagami.github.io/vespabot/charts
helm install vespa-vespabot vespa/vespabot
```

---

## Contributing

1. Fork the repository and create your feature branch.
2. Ensure code adheres to existing Java/Spring coding standards.
3. Run the test suite using `./mvnw test` before submitting.
4. Submit a Pull Request with a concise description of changes.
5. Update documentation if new features are introduced.

---

## License

Distributed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Changelog

| Date | Version | Changes |
|------|---------|---------|
| 2026-09-19 | - | Polished README for clarity and structure |
| 2026-09-04 | - | Updated badges and README formatting |
| 2026-08-03 | - | Implemented async queue processing for telemetry ingestion |
| 2026-07-01 | - | Optimized MySQL connection pooling; released Helm chart |
