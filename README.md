[K[2m  [2mmodel z-ai/glm-5.3-flash failed, trying next...[0m[0m
[K[2m  [2mmodel deepseek-ai/deepseek-v4.1-flash failed, trying next...[0m[0m
# VESPA – Delivery Robot Monitoring Dashboard  

A lightweight, real‑time dashboard for visualizing telemetry from delivery‑robot fleets in smart warehouses. VESPA ingests telemetry via STOMP/SockJS over WebSockets, persists it in a relational database, and exposes both REST and WebSocket endpoints for client consumption.

![Java 17](https://img.shields.io/badge/Java-17-blue?logo=openjdk)  
![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)  
![Maven Central](https://img.shields.io/maven-central/v/io.github.shubhyagami/vespabot)  
![Docker Pulls](https://img.shields.io/docker/pulls/vespa/vespabot)  
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

VESPA acts as the central telemetry hub for robot fleets. Operators can:

- View live robot positions on an interactive map  
- Inspect real‑time telemetry streams and sensor feeds  
- Query historical data via REST or WebSocket  
- Visualise key metrics such as battery level, speed, and task progress  

All data is stored in a relational database (MySQL in production, H2 for local dev).

---

## Features  

| Category | What’s possible |
|----------|-----------------|
| **Real‑time Map** | Leaflet map with live robot positions and path traces |
| **Analytics** | Battery, speed and task progress charts powered by Chart.js |
| **Sensors** | Live RFID scans, ultrasonic distance, and obstacle alerts |
| **Health** | Online/offline status, low‑battery warnings |
| **APIs** | JSON endpoints for historical telemetry |
| **WebSockets** | STOMP/SockJS for low‑latency updates |

---

## Getting Started  

> **TL;DR**  
> ```bash
> git clone https://github.com/shubhyagami/vespabot.git
> cd vespabot
> ./mvnw spring-boot:run
> ```  
> Open `http://localhost:8080` in your browser.

### Prerequisites

- Java 17 or newer  
- Maven (the wrapper `./mvnw` is included)  
- Docker (optional, for containerised runs)

### Quick local run

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
./mvnw spring-boot:run
```

The default profile uses an in‑memory H2 database, so no external services are required for local dev.

### Building a self‑contained JAR

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### Docker

```bash
docker pull vespa/vespabot
docker run -p 8080:8080 vespa/vespabot
```

For a database backed instance:

```bash
docker run -d --name vespabot \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vespa/vespabot
```

---

## Configuration  

Application properties live in `src/main/resources/application.yml`.  
Spring Boot’s relaxed binding allows you to override any property with an environment variable by converting dotted keys to uppercase with underscores.

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

### Example environment overrides

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=secret
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Usage  

### Dashboard  

Navigate to `http://localhost:8080` to view:

- **Live Map** – see robot positions and movement paths in real time.  
- **Telemetry Charts** – battery depletion, speed, and task progress.  
- **Sensor Feed** – live RFID and distance readings.

### REST API  

Open Swagger UI at `http://localhost:8080/swagger-ui.html`. Key endpoints:

| Method | Path | Description |
|--------|------|------------|
| `GET` | `/api/telemetry` | List recent telemetry records |
| `GET` | `/api/telemetry/{id}` | Retrieve a single record by ID |

### WebSocket  

Connect to `ws://localhost:8080/websocket` using STOMP/SockJS. Live updates are broadcast on `/topic/telemetry` by default.

```javascript
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
```

---

## Architecture  

```
Robot → STOMP/SockJS → WebSocket Layer → Spring Boot Logic → JPA/JDBC → Database
```

* **WebSocket Layer** – handles bi‑directional communication, persistence, and broadcasting.  
* **REST API** – stateless access to historical telemetry.  
* **Frontend** – server‑side rendered Thymeleaf pages using Bootstrap 5, Leaflet, and Chart.js.

---

## Tech Stack  

* **Backend** – Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket  
* **Frontend** – Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js  
* **Database** – MySQL (production), H2 (dev / test)  
* **Build / CI** – Maven, GitHub Actions  
* **Containerisation** – Docker, Helm charts for Kubernetes  

---

## Deployment  

### Docker (stand‑alone)

```bash
docker run -d --name vespabot \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vespa/vespabot
```

### Helm 3 (Kubernetes)

```bash
helm repo add vespa https://shubhyagami.github.io/vespabot/charts
helm install vespa-vespabot vespa/vespabot
```

The chart includes configurable values for image, replicas, database credentials, and more.

---

## Contributing  

1. Fork the repo and create a feature branch.  
2. Keep code consistent with existing Java & Spring conventions.  
3. Run `./mvnw test` and ensure all tests pass.  
4. Submit a pull request with a clear description of the changes.  
5. Update documentation if the change adds new functionality.  

We appreciate all contributions – bug reports, small fixes, feature ideas, and documentation improvements.

---

## License  

MIT – see [LICENSE](LICENSE).

---

## Changelog  

| Version | Date | Change |
|---------|------|--------|
| **1.0.1** | 2026‑08‑15 | Fixed WebSocket disconnect handling; improved Swagger UI documentation. |
| **1.0.0** | 2026‑05‑22 | First stable release. Added real‑time map, sensor feed, and REST API. |

---
