# VESPA – Delivery‑Robot Monitoring Dashboard

VESPA is a lightweight, real‑time dashboard that visualises telemetry from fleets of delivery robots operating in smart warehouses.  
It receives data over a STOMP/SockJS WebSocket, persists it in a relational database, and exposes both REST and WebSocket endpoints for clients.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![Maven Central](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)
![Docker Pulls](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Table of contents

- [Overview](#overview)
- [Features](#features)
- [Quick start](#quick-start)
  - [With Maven](#with-maven)
  - [With Docker](#with-docker)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Architecture](#architecture)
- [Technology stack](#technology-stack)
- [Deployment](#deployment)
- [API reference](#api-reference)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)

---

## Overview

VESPA collects live telemetry from robots via a STOMP/SockJS WebSocket channel.  
Incoming data is persisted in a relational database (MySQL by default, H2 for tests) and made available through:

- **REST API** – query historical telemetry  
- **WebSocket** – push live updates to dashboards  
- **Frontend** – Thymeleaf + Bootstrap rendering robot positions, sensor feeds, and health metrics

---

## Features

| Area          | What it does                                                                 |
|---------------|------------------------------------------------------------------------------|
| Map           | Interactive Leaflet map with movement traces                                 |
| Charts        | Battery level, speed, and task progress using Chart.js                        |
| Sensors       | Displays RFID tags, ultrasonic distance, obstacle alerts                    |
| Status        | Online/offline, last‑update timestamp, low‑battery warnings                |
| API           | REST endpoints for querying historical data                                 |
| Telemetry     | Handles bursts, forwards to DB and WebSocket layer                         |

---

## Quick start

### With Maven

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

### With Docker

```bash
docker build -t vespa/vespabot .
docker run -p 8080:8080 vespa/vespabot
```

Open <http://localhost:8080> to view the dashboard.

---

## Getting started

1. **Configure the database** – edit `src/main/resources/application.yml` or override via environment variables.  
2. **Start robot clients** – they must publish to the STOMP topic defined by `vespa.telemetry.topic`.  
3. **Open the dashboard** – the landing page shows the map, charts, and sensor feeds.  
4. **Query the API** – `GET /api/telemetry?robotId=…` for historical data.

Example environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=secret
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Configuration

All runtime settings are in `src/main/resources/application.yml`.  
Environment variables override these values; dotted keys become uppercase with underscores.

| Property                | Default                          | Description                                 |
|------------------------ |----------------------------------|---------------------------------------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db`          | JDBC URL for the database                  |
| `spring.datasource.username` | `sa`                          | Database user                               |
| `spring.datasource.password` | *(empty)*                    | Database password                          |
| `vespa.telemetry.topic`   | `/topic/telemetry`              | STOMP topic used by robots                  |
| `vespa.websocket.enabled` | `true`                         | Enable the WebSocket endpoint               |

---

## Architecture

```
Robot ── [STOMP/SockJS] ─► WebSocket Layer ── [REST/WS] ─► Spring Boot App ── [JDBC] ─► Database
```

* The **WebSocket layer** receives telemetry, persists it, and forwards it to connected clients.  
* The **REST API** exposes historical queries.  
* The **frontend** (Thymeleaf + Bootstrap) renders data on an interactive map and charts.

---

## Technology stack

| Layer      | Technology |
|------------|------------|
| Backend    | Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket |
| Frontend   | Thymeleaf, Bootstrap 5, Leaflet, Chart.js |
| Database   | MySQL (primary), H2 (fallback) |
| Build      | Maven |
| Container  | Docker |

---

## Deployment

### Docker

```bash
docker pull vespa/vespabot
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vespa/vespabot
```

### Helm (optional)

A Helm chart is available in `deploy/helm`.  
Install it into a Kubernetes cluster:

```bash
helm repo add vespa https://shubhyagami.github.io/vespabot/charts
helm install my-vespa vespa/vespabot
```

---

## API reference

| Method | Endpoint            | Description                         |
|--------|---------------------|-------------------------------------|
| GET    | `/api/telemetry`    | Query historical telemetry           |
| GET    | `/api/telemetry/{id}` | Retrieve a single telemetry record |
| GET    | `/websocket`        | WebSocket endpoint for live updates |

All endpoints are documented via Swagger at `/swagger-ui.html` when the application is running.

---

## Contributing

1. Fork the repo and create a feature branch.  
2. Follow the existing coding style (Java, Spring, Thymeleaf).  
3. Run the test suite (`./mvnw test`) before submitting.  
4. Open a pull request with a clear description of the change.  
5. Update the documentation if new features are added.

Pull requests are always welcome.

---

## License

MIT – see the [LICENSE](LICENSE) file.

---

## Changelog

| Date | Summary |
|------|---------|
| 2026‑09‑04 | README cleanup, badge updates |
| 2026‑09‑03 | Minor documentation fixes |
| 2026‑08‑03 | Added async queue processing for telemetry |
| 2026‑07‑15 | Navigation markers; RFID limits tightened |
| 2026‑07‑01 | MySQL connection pooling optimized; Helm charts added |
