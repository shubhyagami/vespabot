# VESPA – Smart Delivery Robot Monitoring Dashboard

A lightweight, real‑time dashboard for monitoring fleets of delivery robots in smart warehouses.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)
![Maven](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)
![Docker](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Table of Contents

1. [Overview](#overview)  
2. [Quick Start](#quick-start)  
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

VESPA consumes telemetry sent by delivery robots over a STOMP/SockJS WebSocket channel. It stores the data in a relational database (MySQL by default, H2 for tests) and exposes:

* REST endpoints for historical queries
* A WebSocket endpoint for live updates
* A static front‑end (Thymeleaf + Bootstrap) that visualises the data

The dashboard shows:

- Robot locations on an interactive map with movement traces  
- Battery level, speed, task status, and sensor readings  
- Online/offline status, last‑update timestamps, low‑battery warnings, obstacle alerts  
- A detail panel with live sensor data

---

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
```

### 2. Run locally

#### With Maven

```bash
./mvnw spring-boot:run
```

Open <http://localhost:8080> in a browser.

#### With Docker (recommended for production)

```bash
docker build -t vespa/vespabot .
docker run -p 8080:8080 vespa/vespabot
```

---

## Configuration

All settings live in `src/main/resources/application.yml`.  
They can also be overridden with environment variables (variable name is the upper‑case form of the property key, dots replaced with underscores).

| Property                     | Default                                   | Description                                |
|------------------------------|-------------------------------------------|--------------------------------------------|
| `spring.datasource.url`     | `jdbc:h2:mem:vespa_db`                   | JDBC URL for the database                  |
| `spring.datasource.username` | `sa`                                      | Database user                              |
| `spring.datasource.password` | *empty*                                  | Database password                           |
| `vespa.telemetry.topic`     | `/topic/telemetry`                        | STOMP topic robots publish telemetry      |
| `vespa.websocket.enabled`   | `true`                                    | Enable / disable the WebSocket layer       |

Example environment variable overrides:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Features

- **Live Dashboard** – interactive map, movement traces, real‑time charts (battery, speed, tasks, health).  
- **Robot Detail View** – battery, ultrasonic distance, RFID tags, speed, destination, and live sensor feed.  
- **Status Indicators** – online/offline, last‑update timestamp, low‑battery warnings, obstacle alerts.  
- **Telemetry Stream Handling** – streams every 3 s over STOMP/SockJS, graceful burst handling, modular firmware interfaces.  
- **Persistence** – telemetry stored in MySQL (primary) with H2 as an in‑memory fallback; Spring Data JPA used for data access.

---

## Architecture

```
Robot ─[STOMP/SockJS]─► WebSocket Layer ─[REST/WS]─► Spring Boot App ─[JDBC]─► Database
```

The Spring Boot application exposes REST endpoints, a WebSocket endpoint, persists data, and serves the static front‑end through Thymeleaf.

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

### Maven

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### Docker (recommended)

```bash
docker build -t vespa/vespabot .
docker run -p 8080:8080 vespa/vespabot
```

---

## Contributing

1. Fork the repository.  
2. Create a feature branch (`git checkout -b feature/…`).  
3. Commit your changes following the existing coding style.  
4. Run the test suite before pushing.  
5. Open a pull request and describe the changes.  
6. Update this documentation if you add or modify features.

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
