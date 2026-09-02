# VESPA – Smart Delivery Robot Monitoring

[![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)](https://search.maven.org/artifact/com.github.shubhyagami/vespabot)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)](https://github.com/shubhyagami/vespabot)

> A lightweight, real‑time dashboard for monitoring fleets of delivery robots in smart warehouses.

---

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Running the Application](#running-the-application)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)

---

## Overview

VESPA collects telemetry from delivery robots over a STOMP/SockJS WebSocket channel and visualises robot status, battery levels, navigation data, and alerts on an interactive web dashboard. The stack is built on Java 17 and Spring Boot 3.2, with a lightweight Thymeleaf/Bootstrap front‑end powered by Leaflet and Chart.js.

---

## Getting Started

1. **Clone the repo**

   ```bash
   git clone https://github.com/shubhyagami/vespabot.git
   cd vespabot
   ```

2. **Configure the database**  
   Edit `src/main/resources/application.yml` (you can also use `application.properties`) and point to your database.  
   For quick local testing you can keep the default H2 settings:

   ```yaml
   spring:
     datasource:
       url: jdbc:h2:mem:vespa_db;DB_CLOSE_DELAY=-1
       driver-class-name: org.h2.Driver
   ```

3. **Build and run**

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Open the dashboard**

   Browse to `http://localhost:8080` to see the live map and charts.

---

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | JDBC URL for the database. |
| `spring.datasource.username` | `sa` | Database username. |
| `spring.datasource.password` | (empty) | Database password. |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic name. |
| `vespa.websocket.enabled` | `true` | Enable/disable WebSocket. |

All other properties are loaded from the Spring configuration hierarchy; see the [Spring Boot reference guide](https://docs.spring.io/spring-boot/docs/current/reference/html/) for more details.

---

## Features

- **Live Dashboard**
  - Interactive map with robot markers and movement traces.
  - Real‑time charts: battery, speed, tasks, and health metrics.
  - Color‑coded markers for active, charging, error, and idle states.

- **Robot Detail View**
  - Battery level, ultrasonic distance, RFID tags, speed, and destination.
  - Live sensor data feed.

- **Status Indicators**
  - Online/offline flags with last‑update timestamps.
  - Low‑battery warnings, obstacle‑detection alerts, RFID‑scan notifications.

- **Robust Telemetry Handling**
  - Streams every 3 s over STOMP/SockJS.
  - Handles bursty packets gracefully.
  - Modular firmware interfaces for easy integration with new robot hardware.

- **Persistence**
  - Telemetry persisted in MySQL (default) with an H2 fallback for testing.
  - Spring Data JPA for CRUD operations.

---

## Architecture

```
+-------------------+           +-------------------+          +---------------------------+
| Delivery Robot    | <------>  | WebSocket Layer   | <------> | Frontend (Thymeleaf/JS)  |
| (Telemetry)      |           | (STOMP/SockJS)    |          | (Leaflet, Chart.js)      |
+-------------------+           +-------------------+          +---------------------------+
                                            |
                                            v
                                +--------------------+
                                | Spring Boot App    |
                                | (Java 17)          |
                                +--------------------+
                                            |
                                            v
                                +--------------------+
                                | Database           |
                                | (MySQL / H2)       |
                                +--------------------+
```

The backend exposes REST endpoints and a WebSocket endpoint, stores telemetry in a relational database, and serves static resources via Thymeleaf.

---

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA, Spring Security, Spring WebSocket
- **Frontend**: Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js
- **Database**: MySQL (primary), H2 (fallback)
- **Build**: Maven

---

## Running the Application

### With Maven

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### Docker (optional)

```bash
docker build -t vespa/vespabot .
docker run -p 8080:8080 vespa/vespabot
```

---

## Contributing

1. Fork the repository.  
2. Create a feature branch: `git checkout -b feature/awesome-feature`.  
3. Commit changes and push the branch.  
4. Open a pull request.

Please follow the existing code style, run tests, and update documentation where relevant.

---

## License

MIT – see the [LICENSE](LICENSE) file.

---

## Changelog

- **2026‑09‑03** – Readme cleanup and updated badges.  
- **2026‑08‑03** – Added asynchronous queue processing for telemetry streams.  
- **2026‑07‑15** – Introduced navigation markers; tightened RFID limits.  
- **2026‑07‑01** – Optimised MySQL connection pooling; added Helm charts for K8s.  

---
