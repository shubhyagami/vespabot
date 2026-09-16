# VESPA – Delivery‑Robot Monitoring Dashboard

*A lightweight, real‑time dashboard for visualising telemetry from fleets of delivery robots in smart warehouses.*

VESPA receives telemetry over a STOMP/SockJS WebSocket, stores it in a relational database, and exposes both REST and WebSocket endpoints for clients.

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
- [Quick start](#quick-start)
  - [Maven](#maven)
  - [Docker](#docker)
- [Configuration](#configuration)
- [Usage](#usage)
  - [Dashboard](#dashboard)
  - [API](#api)
- [Architecture](#architecture)
- [Tech stack](#tech-stack)
- [Deployment](#deployment)
  - [Docker](#docker-1)
  - [Helm chart](#helm-chart)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)

---

## Overview

VESPA streams live telemetry from robots, persists it in a database (MySQL by default, H2 for tests), and serves:

- **REST API** – query historical telemetry
- **WebSocket** – push live updates to dashboards
- **Frontend** – Thymeleaf + Bootstrap rendering robot positions, sensor feeds, and health metrics

---

## Features

| Category | Functionality |
|----------|---------------|
| **Map** | Interactive Leaflet map with movement traces |
| **Charts** | Battery, speed, and task‑progress graphs (Chart.js) |
| **Sensors** | RFID, ultrasonic distance, obstacle alerts |
| **Status** | Online/offline indicator, timestamps, low‑battery warnings |
| **API** | Endpoints for historical data |

---

## Quick start

### Maven

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
./mvnw spring-boot:run
```

Or build a fat JAR:

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### Docker

```bash
docker pull vespa/vespabot
docker run -p 8080:8080 vespa/vespabot
```

Open <http://localhost:8080> to view the dashboard.

---

## Configuration

All runtime settings live in `src/main/resources/application.yml`.  
Environment variables override them – dotted keys become uppercase with underscores.

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | JDBC URL |
| `spring.datasource.username` | `sa` | DB user |
| `spring.datasource.password` | *(empty)* | DB password |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic for robot clients |
| `vespa.websocket.enabled` | `true` | Enable WebSocket endpoint |

Example (Unix shell):

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=secret
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Usage

### Dashboard

Navigate to <http://localhost:8080>.  
The landing page shows:

- A Leaflet map with robot movement traces
- Real‑time charts (battery, speed, task progress)
- Sensor feeds (RFID, ultrasonic, obstacles)

### API

The REST API is documented via Swagger at `/swagger-ui.html`.  
Key endpoints:

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/telemetry` | Query historical telemetry |
| GET | `/api/telemetry/{id}` | Retrieve a single telemetry record |
| GET | `/websocket` | WebSocket endpoint for live updates |

---

## Architecture

```
Robot → STOMP/SockJS → WebSocket layer → Spring Boot App → JDBC → Database
```

* **WebSocket layer** – receives, persists, and forwards telemetry.
* **REST API** – queries historical data.
* **Frontend** – Thymeleaf + Bootstrap renders an interactive map and charts.

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket |
| Frontend | Thymeleaf, Bootstrap 5, Leaflet, Chart.js |
| Database | MySQL (primary), H2 (fallback) |
| Build | Maven |
| Container | Docker |

---

## Deployment

### Docker

```bash
docker run -d --name vespabot \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vespa/vespabot
```

### Helm chart

A Helm chart is available under `deploy/helm`.  
Install in Kubernetes:

```bash
helm repo add vespa https://shubhyagami.github.io/vespabot/charts
helm install vespa-vespabot vespa/vespabot
```

---

## Contributing

1. Fork the repo and create a feature branch.  
2. Follow the existing Java/Spring coding conventions.  
3. Run tests (`./mvnw test`) before pushing.  
4. Submit a pull request with a clear description.  
5. Update documentation if you add new features.

Pull requests are welcome.

---

## License

MIT – see the [LICENSE](LICENSE) file.

---

## Changelog

| Date | Change |
|------|--------|
| 2026‑09‑04 | Updated README, badge improvements |
| 2026‑08‑03 | Added async queue processing for telemetry |
| 2026‑07‑01 | Optimised MySQL connection pooling; added Helm chart |
