# VESPA – Smart Delivery Robot Monitoring Dashboard

VESPA is a lightweight, real‑time dashboard that visualises telemetry from fleets of delivery robots in smart warehouses.  
It ingests data over a STOMP/SockJS WebSocket, stores it in a relational database, and exposes both REST and WebSocket endpoints for clients.

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![Maven](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)
![Docker](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)

---

## Overview

VESPA receives live telemetry from robots via a STOMP/SockJS WebSocket channel.  
Incoming data is persisted in a relational database (MySQL by default, H2 for tests) and made available through:

* **REST API** – query historical telemetry
* **WebSocket** – push live updates to dashboards
* **Static Front‑end** – Thymeleaf + Bootstrap that shows robot positions, sensor feeds, and health metrics

---

## Features

| Area | What you get |
|------|--------------|
| **Map** | Interactive view with movement traces (Leaflet) |
| **Charts** | Battery level, speed, task progress (Chart.js) |
| **Sensor Feed** | RFID tags, ultrasonic distance, obstacle alerts |
| **Status** | Online/offline, last‑update timestamp, low‑battery warnings |
| **Controller** | REST endpoints for historical queries |
| **Telemetry** | Handles bursts from robots, forwards to the database and WS layer |

---

## Quick Start

> **Prerequisites**  
> • Java 17 or later  
> • Maven 3.9+ (or use the wrapped `./mvnw`)  
> • Docker (recommended for production)

1. **Clone the repository**

   ```bash
   git clone https://github.com/shubhyagami/vespabot.git
   cd vespabot
   ```

2. **Run locally (Maven)**

   ```bash
   ./mvnw spring-boot:run
   # or build a fat jar
   ./mvnw clean package
   java -jar target/vespabot-*.jar
   ```

3. **Run with Docker**

   ```bash
   docker build -t vespa/vespabot .
   docker run -p 8080:8080 vespa/vespabot
   ```

4. Open [http://localhost:8080](http://localhost:8080) in a browser to see the dashboard.

---

## Configuration

All runtime settings are in `src/main/resources/application.yml`.  
Environment variables override these values – replace dots with underscores and use uppercase names.

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | JDBC URL for the database |
| `spring.datasource.username` | `sa` | Database user |
| `spring.datasource.password` | *(empty)* | Database password |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic used by robots |
| `vespa.websocket.enabled` | `true` | Enable the WebSocket endpoint |

**Example**

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vespa
export VESPA_TELEMETRY_TOPIC=/topic/robot/telemetry
```

---

## Architecture

```
Robot ─[STOMP/SockJS]─► WebSocket Layer ─[REST/WS]─► Spring Boot App ─[JDBC]─► Database
```

* The **WebSocket layer** receives telemetry, pushes it to the database, and forwards it to connected clients.
* The **REST API** exposes historical queries.
* The **frontend** (Thymeleaf + Bootstrap) renders the data on an interactive map and charts.

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket |
| Frontend | Thymeleaf, Bootstrap 5, Leaflet, Chart.js |
| Database | MySQL (primary), H2 (fallback) |
| Build | Maven |
| Container | Docker |

---

## Contributing

1. Fork the repository.  
2. Create a feature branch (`git checkout -b feature/...`).  
3. Follow the existing coding style.  
4. Run tests (`./mvnw test`) before pushing.  
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
