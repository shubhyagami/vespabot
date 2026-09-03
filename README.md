# VESPA – Smart Delivery Robot Monitoring Dashboard

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)  
![Build](https://img.shields.io/maven-central/v/com.github.shubhyagami/vespabot?label=maven)  
![Docker](https://img.shields.io/docker/pulls/vespa/vespabot?label=docker)  
![License](https://img.shields.io/badge/License-MIT-yellow)  
![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)

> A lightweight, real‑time dashboard for monitoring fleets of delivery robots in smart warehouses.

---

## Overview
VESPA receives telemetry from delivery robots via a STOMP/SockJS WebSocket channel and visualises the data on an interactive web dashboard. It shows robot health, battery status, navigation paths and sensor readings in real time.

---

## Quick Start

```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
./mvnw spring-boot:run
```

Open `http://localhost:8080` to view the dashboard.

---

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:h2:mem:vespa_db` | JDBC URL. |
| `spring.datasource.username` | `sa` | Database user. |
| `spring.datasource.password` | *empty* | Database password. |
| `vespa.telemetry.topic` | `/topic/telemetry` | STOMP topic name. |
| `vespa.websocket.enabled` | `true` | WebSocket toggle. |

Edit `src/main/resources/application.yml` or supply environment variables.

---

## Features

- **Live Dashboard** – map with robot markers, movement traces, and real‑time charts (battery, speed, tasks, health).  
- **Robot Detail View** – battery, ultrasonic distance, RFID tags, speed, destination, live sensor feed.  
- **Status Indicators** – online/offline, last‑update time, low‑battery warnings, obstacle alerts.  
- **Telemetry Handling** – streams every 3 s over STOMP/SockJS, graceful burst handling, modular firmware interfaces.  
- **Persistence** – telemetry stored in MySQL (default) with H2 for testing; Spring Data JPA.

---

## Architecture

```
Robot  <–STOMP/SockJS→  WebSocket Layer  <–REST/WS→  Spring Boot App  <–JDBC→  Database
```

The backend exposes REST endpoints and a WebSocket endpoint, persists data, and serves static resources via Thymeleaf.

---

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA, Spring WebSocket  
- **Frontend**: Thymeleaf, Bootstrap 5, Leaflet, Chart.js  
- **Database**: MySQL (primary), H2 (fallback)  
- **Build**: Maven

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
2. Create a feature branch `git checkout -b feature/…`.  
3. Commit and push.  
4. Open a pull request.  

Please run tests, keep the coding style, and update documentation when necessary.

---

## License

MIT – see the [LICENSE](LICENSE) file.

---

## Changelog

- **2026‑09‑03** – Readme cleanup, updated badges.  
- **2026‑08‑03** – Added async queue processing for telemetry.  
- **2026‑07‑15** – Navigation markers; RFID limits tightened.  
- **2026‑07‑01** – MySQL connection pooling optimised; Helm charts added.
