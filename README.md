# VESPA – Smart Delivery Robot Monitoring Dashboard

A lightweight, real‑time dashboard for monitoring fleets of delivery robots in smart warehouses.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![Maven](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)
![Docker](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Table of Contents

- [Overview](#overview)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)

---

## Overview

VESPA receives live telemetry from delivery robots over a **STOMP/SockJS WebSocket** channel. The data is stored in a relational database (MySQL by default, H2 for tests) and exposed through:

- **REST endpoints** for historical queries
- **WebSocket endpoint** for live updates
- A **static front‑end** (Thymeleaf + Bootstrap) that visualizes robot positions, sensor data, and health metrics

The dashboard features:

| Feature | What you see |
|---------|--------------|
| Map | Interactive view with movement traces |
| Charts | Battery level, speed, task progress |
| Sensor feed | RFID tags, ultrasonic distance, obstacle alerts |
| Status | Online/offline, last‑updated timestamp, low‑battery warnings |

---

## Quick Start

> **Prerequisites**  
> - Java 17 (or later)  
> - Maven 3.9+ (or use the wrapper)  
> - Docker (optional, for containerized deployment)

1. **Clone the repository**

   ```bash
   git clone https://github.com/shubhyagami/vespabot.git
   cd vespabot
   ```

2. **Run locally (Maven)**

   ```bash
   ./mvnw spring-boot:run
   # or build a jar
   ./mvnw clean package
   java -jar target/vespabot-*.jar
   ```

3. **Run with Docker (recommended for production)**

   ```bash
   docker build -t vespa/vespabot .
   docker run -p 8080:8080 vespa/vespabot
   ```

4. Open <http://localhost:8080> in a browser.

---

## Configuration

All properties live in `src/main/resources/application.yml`.  
Environment variables can override any property; replace dots (`.`) with underscores (`_`) and use upper‑case names.

| Property                | Default                     | Description |
|-------------------------|-----------------------------|-------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | JDBC URL for the database |
| `spring.datasource.username` | `sa` | Database user |
| `spring.datasource.password` | *(empty)* | Database password |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic used by robots |
| `vespa.websocket.enabled` | `true` | Enable/disable the WebSocket endpoint |

**Example**

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Features

- **Real‑time dashboard** – interactive map, movement traces, live charts (battery, speed, tasks)
- **Robot detail view** – battery level, ultrasonic distance, RFID tags, destination
- **Status indicators** – online/offline, last‑update, low‑battery warnings, obstacle alerts
- **Telemetry handling** – streams every 3 s over STOMP/SockJS, burst‑tolerant, modular firmware interfaces
- **Persistence** – JPA repository backed by MySQL (primary) with an H2 fallback

---

## Architecture

```text
Robot ─[STOMP/SockJS]─► WebSocket Layer ─[REST/WS]─► Spring Boot App ─[JDBC]─► Database
```

The Spring Boot application:

- Exposes REST endpoints for querying historical telemetry
- Provides a WebSocket endpoint for client subscriptions
- Persists incoming telemetry in the database
- Serves the static web front‑end through Thymeleaf

---

## Technology Stack

| Layer | Tech |
|-------|------|
| **Backend** | Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket |
| **Frontend** | Thymeleaf, Bootstrap 5, Leaflet, Chart.js |
| **Database** | MySQL (primary), H2 (fallback) |
| **Build** | Maven |
| **Container** | Docker |

---

## Contributing

1. Fork the repository.  
2. Create a feature branch (`git checkout -b feature/…`).  
3. Follow the existing coding style.  
4. Run the test suite before pushing (`./mvnw test`).  
5. Open a pull request with a clear description.  
6. Update the documentation if you add or modify features.

Pull requests are welcome!

---

## License

MIT – see the [LICENSE](LICENSE) file.

---

## Changelog

- **2026‑09‑04** – README cleanup, badge updates.  
- **2026‑09‑03** – Minor documentation fixes.  
- **2026‑08‑03** – Added async queue processing for telemetry.  
- **2026‑07‑15** – Navigation markers; RFID limits tightened.  
- **2026‑07‑01** – MySQL connection pooling optimized; Helm charts added.
