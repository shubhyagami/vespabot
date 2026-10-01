[K[2m  [2mmodel z-ai/glm-5.3-flash failed, trying next...[0m[0m
[K[2m  [2mmodel deepseek-ai/deepseek-v4.1-flash failed, trying next...[0m[0m
# VESPA – Delivery Robot Monitoring Dashboard

A lightweight, real-time dashboard for visualizing telemetry from delivery‑robot fleets in smart warehouses.

---

## Badges

[![Java 17](https://img.shields.io/badge/Java-17-blue?logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.shubhyagami/vespabot)](https://central.sonatype.com/artifact/io.github.shubhyagami/vespabot)
[![Docker Pulls](https://img.shields.io/docker/pulls/vespa/vespabot)](https://hub.docker.com/r/vespa/vespabot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Table of Contents

- Overview
- Features
- Quick Start
- Configuration
- Usage
  - Dashboard
  - REST API
  - WebSocket
- Architecture
- Tech Stack
- Deployment
- Changelog
- Contributing
- License

---

## Overview

VESPA collects telemetry over STOMP/SockJS WebSockets, stores it in a relational database, and exposes both REST and WebSocket interfaces for clients. Operators can:

- Visualise live robot positions on an interactive map.
- Inspect telemetry streams and sensor feeds in real time.
- Query historical data via HTTP or WebSocket.
- Monitor key metrics such as battery level, speed, and task progress.

The production database is MySQL; a lightweight H2 database is used for local development.

---

## Features

- **Real‑time map** – Leaflet map with live positions and path traces.
- **Analytics** – Battery, speed, and task‑progress charts (Chart.js).
- **Sensors** – Live RFID scans, ultrasonic distance, obstacle alerts.
- **Health** – Online/offline status, low‑battery notifications.
- **REST API** – JSON endpoints for historic telemetry.
- **WebSocket** – STOMP/SockJS for low‑latency streaming.

---

## Quick Start

### Prerequisites

- Java 17 or newer
- Maven (the `./mvnw` wrapper is bundled)
- Docker (optional)

### Run locally

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
./mvnw spring-boot:run
```

Open <http://localhost:8080> in a browser.  
The default `dev` profile uses an in‑memory H2 database, so no external services are required.

### Build a standalone JAR

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

---

## Configuration

Application properties live in `src/main/resources/application.yml`.  
Spring Boot’s relaxed binding lets you override any key with an environment variable by converting dotted keys to uppercase and replacing dots with underscores.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:vespa_db
    username: sa
    password: ""

vespa:
  telemetry:
    topic: /topic/telemetry
  websocket:
    enabled: true
```

Examples of environment overrides (Linux/macOS):

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://db:3306/vespa"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="secret"
export VESPA_TELEMETRY_TOPIC="/topic/robot/telemetry"
```

---

## Usage

### Dashboard

Navigate to <http://localhost:8080>:

- **Live Map** – Robot positions and movement paths.
- **Telemetry Charts** – Battery, speed, task progress.
- **Sensor Feed** – RFID and distance readings.

### REST API

Swagger UI is available at <http://localhost:8080/swagger-ui/index.html>.

Key endpoints:

- `GET /api/telemetry` – List recent telemetry records.
- `GET /api/telemetry/{id}` – Retrieve a single record by ID.

### WebSocket

Connect to `ws://localhost:8080/websocket` using STOMP/SockJS.  
Live updates are broadcast on `/topic/telemetry` by default.

```javascript
import SockJS from "https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js";
import Stomp from "https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js";

const socket = new SockJS("http://localhost:8080/websocket");
const client = Stomp.over(socket);
client.connect({}, frame => {
  client.subscribe("/topic/telemetry", message => {
    console.log(JSON.parse(message.body));
  });
});
```

---

## Architecture

```
Robot → STOMP/SockJS → WebSocket Layer → Spring Boot → JPA/JDBC → Database
```

- **WebSocket Layer** – Handles communication, persistence, and broadcasting.
- **REST API** – Stateless access to historic telemetry.
- **Frontend** – Server‑side rendered Thymeleaf pages with Bootstrap 5, Leaflet, and Chart.js.

---

## Tech Stack

| Layer | Technologies |
|-------|--------------|
| Backend | Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket |
| Frontend | Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js |
| Database | MySQL (prod), H2 (dev/test) |
| Build / CI | Maven, GitHub Actions |
| Container | Docker, Helm (Kubernetes) |

---

## Deployment

### Docker (standalone)

```bash
docker pull vespa/vespabot
docker run -p 8080:8080 vespa/vespabot
```

### Docker with external MySQL

```bash
docker run -d --name vespabot \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://db:3306/vespa" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  vespa/vespabot
```

### Kubernetes

Helm charts are located under `charts/vespabot`. Adjust `values.yaml` for your cluster and install:

```bash
helm repo add vespa https://example.com/charts
helm install vespabot vespa/vespabot
```

---

## Changelog

**v1.4.0 – 2026‑10‑01**

- Updated Spring Boot to 3.2.
- Added WebSocket health‑check endpoint.
- Improved configuration documentation.
- Minor bug fixes in telemetry persistence.

<!-- Keep only the most recent change; older entries can be archived in a separate file. -->

---

## Contributing

Feel free to open an issue or submit a pull request. Please run `./mvnw test` before submitting. For larger changes, discuss first in an issue.

---

## License

MIT – see [LICENSE](LICENSE).
