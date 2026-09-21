# VESPA – Delivery Robot Monitoring Dashboard

A lightweight, real‑time dashboard for visualizing telemetry from fleets of delivery robots in smart warehouse environments.  
VESPA ingests telemetry via STOMP/SockJS WebSockets, persists the data in a relational database, and exposes both REST and WebSocket endpoints for clients.

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

VESPA acts as the central telemetry hub for robot fleets. It streams live data, stores historical records, and delivers a comprehensive monitoring interface.

**Core workflow**

1. **Ingestion** – Receives real‑time telemetry from robots via WebSockets.  
2. **Persistence** – Stores data in MySQL (production) or H2 (development).  
3. **Distribution** – Broadcasts live updates to the frontend and exposes a REST API for historical queries.  
4. **Visualization** – Renders robot positions, health metrics, and sensor data in a Thymeleaf‑based dashboard.

---

## Features

| Category | Functionality |
|----------|---------------|
| **Mapping** | Interactive Leaflet map with real‑time movement traces |
| **Analytics** | Battery, speed, and task‑progress charts via Chart.js |
| **Sensors** | RFID, ultrasonic distance, and obstacle alerts |
| **Health** | Online/offline status, low‑battery warnings |
| **API** | JSON endpoints for historical telemetry |

---

## Getting Started

> **TL;DR**  
> Clone the repo, run it with Maven, and open `http://localhost:8080`. For Docker, simply `docker run -p 8080:8080 vespa/vespabot`.

### 1. Local Development (Maven)

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
./mvnw spring-boot:run
```

To build a fat JAR:

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### 2. Docker Quick Run

```bash
docker pull vespa/vespabot
docker run -p 8080:8080 vespa/vespabot
```

Open the dashboard at `http://localhost:8080`.

---

## Configuration

All runtime settings are defined in `src/main/resources/application.yml`.  
Environment variables can override the values; convert dotted keys to uppercase with underscores.

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

### Example Environment Variables

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=secret
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Usage

### Monitoring Dashboard

Navigate to `http://localhost:8080` to access:

- **Live Map** – Real‑time robot movement and paths.  
- **Telemetry Charts** – Battery depletion, speed, and task progress.  
- **Sensor Feed** – Current RFID scans and ultrasonic readings.

### REST API

Swagger UI is available at `/swagger-ui.html`.  
Common endpoints:

| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/telemetry` | Retrieve a list of historical telemetry records |
| `GET` | `/api/telemetry/{id}` | Retrieve a specific telemetry record |
| `GET` | `/websocket` | Establish a WebSocket connection for live updates |

---

## Architecture

```
Robot ➜ STOMP/SockJS ➜ WebSocket layer ➜ Spring Boot logic ➜ JPA/JDBC ➜ Database
```

* **WebSocket Layer** – Handles bidirectional communication, persistence, and broadcasting.  
* **REST API** – Stateless interface for querying historical data.  
* **Frontend** – Server‑side rendered Thymeleaf app with Bootstrap, Leaflet, and Chart.js.

---

## Tech Stack

- **Backend** – Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket  
- **Frontend** – Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js  
- **Database** – MySQL (production), H2 (dev/test)  
- **Build & DevOps** – Maven, Docker, Helm

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

```bash
helm repo add vespa https://shubhyagami.github.io/vespabot/charts
helm install vespa-vespabot vespa/vespabot
```

---

## Contributing

1. Fork the repository and create a feature branch.  
2. Follow existing coding conventions (Java/Spring).  
3. Run tests with `./mvnw test` before submitting.  
4. Create a pull request with a clear description.  
5. Update the documentation if new features are added.

---

## License

Distributed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Changelog

| Date | Version | Changes |
|------|---------|---------|
| 2026‑09‑19 | – | Polished README for clarity and structure |
| 2026‑09‑04 | – | Updated badges and formatting |
| 2026‑08‑03 | – | Implemented async queue processing for telemetry ingestion |
| 2026‑07‑01 | – | Optimized MySQL connection pooling; released Helm chart |
