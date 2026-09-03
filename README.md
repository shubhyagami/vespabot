# VESPA – Smart Delivery Robot Monitoring Dashboard

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)  
![Maven](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)  
![Docker](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)  
![License](https://img.shields.io/badge/License-MIT-yellow)  
![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)

A lightweight, real‑time dashboard for monitoring fleets of delivery robots in smart warehouses.

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

VESPA receives telemetry from delivery robots through a STOMP/SockJS WebSocket channel and displays the data on an interactive web dashboard. The UI shows:

- Robot locations on a map with movement traces  
- Battery level, speed, task status and sensor readings  
- Online/offline indicators, low‑battery warnings and obstacle alerts  
- A detailed panel with live sensor data

Telemetry is persisted in a relational database (MySQL by default, H2 for testing). The backend exposes REST endpoints and a WebSocket endpoint and serves the static frontend via Thymeleaf.

---

## Getting Started

1. **Clone the repository**

   ```bash
   git clone https://github.com/shubhyagami/vespabot.git
   cd vespabot
   ```

2. **Run locally (Maven)**

   ```bash
   ./mvnw spring-boot:run
   ```

   The dashboard will be accessible at <http://localhost:8080>.

3. **Run locally (Docker)** – suitable for production

   ```bash
   docker build -t vespa/vespabot .
   docker run -p 8080:8080 vespa/vespabot
   ```

---

## Configuration

All settings can be defined in `src/main/resources/application.yml` or overridden with environment variables.

| Property               | Default Value                | Description                                 |
|------------------------|-----------------------------|---------------------------------------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | JDBC URL for the database                  |
| `spring.datasource.username` | `sa` | Database user                             |
| `spring.datasource.password` | *empty* | Database password                          |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic that robots publish telemetry |
| `vespa.websocket.enabled` | `true` | Enable or disable the WebSocket layer      |

Example environment variable override:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Features

- **Live Dashboard** – interactive map, movement trace, real‑time charts (battery, speed, tasks, health).  
- **Robot Detail View** – shows battery, ultrasonic distance, RFID tags, speed, destination, and live sensor feed.  
- **Status Indicators** – online/offline, last‑update timestamp, low‑battery warnings, obstacle alerts.  
- **Telemetry Handling** – streams every 3 s over STOMP/SockJS, graceful burst handling, modular firmware interfaces.  
- **Persistence** – telemetry stored in MySQL (primary) with H2 as an in‑memory fallback; Spring Data JPA used for data access.

---

## Architecture

```
Robot  <–STOMP/SockJS→  WebSocket Layer  <–REST/WS→  Spring Boot App  <–JDBC→  Database
```

The backend exposes REST endpoints and a WebSocket endpoint, persists data, and serves the static resources via Thymeleaf.

---

## Technology Stack

| Category | Libraries / Frameworks |
|----------|-----------------------|
| **Backend** | Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket |
| **Frontend** | Thymeleaf, Bootstrap 5, Leaflet, Chart.js |
| **Database** | MySQL (primary), H2 (fallback) |
| **Build** | Maven |

---

## Running the Application

### Maven

```bash
./mvnw clean package
java -jar target/vespabot-*.jar
```

### Docker (recommended for production)

```bash
docker build -t vespa/vespabot .
docker run -p 8080:8080 vespa/vespabot
```

---

## Contributing

1. Fork the repo.  
2. Create a feature branch (`git checkout -b feature/…`).  
3. Commit your changes.  
4. Push to your fork.  
5. Open a pull request.

Please run the test suite, follow the existing coding style, and update the documentation when you add or modify features.

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
