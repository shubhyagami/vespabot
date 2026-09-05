# VESPA – Smart Delivery Robot Monitoring Dashboard

A lightweight, real‑time dashboard for monitoring fleets of delivery robots in smart warehouses.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)
![Maven](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)
![Docker Pulls](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Table of Contents

1. [Overview](#overview)
2. [Getting Started](#getting-started)
3. [Configuration](#configuration)
4. [Features](#features)
5. [Architecture](#architecture)
6. [Technology Stack](#technology-stack)
7. [Running the Application](#running-the-application)
8. [Contributing](#contributing)
9. [License](#license)
10. [Changelog](#changelog)

---

## Overview

VESPA receives telemetry from delivery robots via a STOMP over SockJS WebSocket channel. The data is persisted in a relational database (MySQL by default, H2 for tests) and served through:

* **REST endpoints** for historical queries
* **WebSocket endpoint** for live updates
* A **static front‑end** (Thymeleaf + Bootstrap) that visualizes robot movements and sensor data

The dashboard shows:

| Item | Description |
|------|-------------|
| Robot locations | Interactive map with movement traces |
| Battery & speed | Real‑time charts |
| Task status & sensors | Live sensor feed and alerts |
| Connectivity | Online/offline status, last‑update timestamp, low‑battery and obstacle warnings |

---

## Getting Started

> **Prerequisites**  
> - Java 17 (or newer)  
> - Maven 3.9+ (or use the provided wrapper)  
> - Docker (if you prefer containerized deployment)

1. **Clone the repo**

   ```bash
   git clone https://github.com/shubhyagami/vespabot.git
   cd vespabot
   ```

2. **Run locally**

   ```bash
   ./mvnw spring-boot:run
   # or
   ./mvnw clean package
   java -jar target/vespabot-*.jar
   ```

   Open <http://localhost:8080> in a browser.

3. **Run with Docker (recommended for production)**

   ```bash
   docker build -t vespa/vespabot .
   docker run -p 8080:8080 vespa/vespabot
   ```

---

## Configuration

All settings are in `src/main/resources/application.yml`.  
Environment variables can override properties: replace dots with underscores and use upper‑case names.

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | JDBC URL for the database |
| `spring.datasource.username` | `sa` | Database user |
| `spring.datasource.password` | *(empty)* | Database password |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic used by robots |
| `vespa.websocket.enabled` | `true` | Enable or disable the WebSocket endpoint |

**Example:**

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Features

- **Live Dashboard**: interactive map, movement traces, real‑time charts for battery, speed, tasks, and health metrics.
- **Robot Detail View**: battery level, ultrasonic distance, RFID tags, speed, destination, and a live sensor feed.
- **Status Indicators**: online/offline status, last‑update timestamp, low‑battery warnings, and obstacle alerts.
- **Telemetry Stream Handling**: streams every 3 s over STOMP/SockJS, with graceful burst handling and modular firmware interfaces.
- **Persistence**: stores telemetry in MySQL (primary) with an H2 in‑memory fallback; data access via Spring Data JPA.

---

## Architecture

```
Robot ─[STOMP/SockJS]─► WebSocket Layer ─[REST/WS]─► Spring Boot App ─[JDBC]─► Database
```

The Spring Boot application exposes REST endpoints for historical data, a WebSocket endpoint for real‑time updates, persists telemetry, and serves a static front‑end through Thymeleaf.

---

## Technology Stack

| Category | Tools / Libraries |
|----------|--------------------|
| **Backend** | Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket |
| **Frontend** | Thymeleaf, Bootstrap 5, Leaflet, Chart.js |
| **Database** | MySQL (primary), H2 (fallback) |
| **Build** | Maven |
| **Container** | Docker |

---

## Running the Application

### With Maven

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### With Docker (recommended)

```bash
docker build -t vespa/vespabot .
docker run -p 8080:8080 vespa/vespabot
```

---

## Contributing

1. Fork the repository.  
2. Create a feature branch (`git checkout -b feature/…`).  
3. Follow the existing coding style.  
4. Run the test suite locally before pushing (`./mvnw test`).  
5. Open a pull request with a clear description of the changes.  
6. Update the documentation if you add or modify features.

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
