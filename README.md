# VESPA - Smart Multi-Robot Delivery Monitoring System

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-success?logo=apachemaven)](https://maven.apache.org/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%2FSockJS-ff69b4)](https://stomp-js.github.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)](https://github.com/shubhyagami/vespabot)

A Spring Boot dashboard for monitoring a fleet of delivery robots in a smart warehouse environment. It tracks 5 simulated robots and 1 real hardware-integrated robot, providing WebSocket-driven real-time telemetry updates.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security, Spring WebSocket
- **Frontend**: Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js, STOMP + SockJS
- **Database**: MySQL (primary), H2 (fallback)
- **Build**: Maven

## Features

- **Live Dashboard**: Animated map with robot markers, movement paths, sensor data cards, and real-time analytics charts (battery, speed, tasks, status).
- **Robot Monitoring**: Individual detail pages for 6 robots displaying battery level, ultrasonic distance, RFID tags, speed, and destination.
- **Status Indicators**: Color-coded map markers (Green=Active, Yellow=Charging, Red=Error, Blue=Idle), online/offline status, and last updated timestamps.
- **Real-Time Updates**: Live data streaming via WebSocket + STOMP. Robot movement updates every 3 seconds, alongside low battery alerts, obstacle detection warnings, and RFID scan notifications.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/robots` | Get all robots |
| `GET` | `/api/robots/{id}` | Get robot by ID |
| `POST` | `/api/robots/live-update` | Receive real robot sensor data |
| `GET` | `/` | Dashboard home |

## Getting Started

To run this project locally, you will need JDK 17+ and Maven installed.

### 1. Clone the Repository
```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
```

### 2. Configure the Database
Update your `src/main/resources/application.properties` to point to your MySQL instance, or rely on the automatic H2 fallback for a quick local test run:
```properties
# Primary MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/vespa_db
spring.datasource.username=root
spring.datasource.password=your_password

# H2 Fallback (Enabled automatically if MySQL is unreachable)
spring.h2.console.enabled=true
```

### 3. Run the Application
Use the Maven wrapper to bootstrap the Spring Boot server:
```bash
./mvnw spring-boot:run
```

### 4. Access the Dashboard
Once started, open your browser and navigate to `http://localhost:8080` to view the live bot telemetry.

## Operational Notes

- **WebSocket Tuning**: If you experience stale data during high-frequency packet bursts, adjust the heartbeat interval in `WebSocketConfig.java`. The default 3-second heartbeat should match your backend publish rate.
- **Hardware Integration**: The `/api/robots/live-update` endpoint accepts millisecond-precision timestamps. When connecting physical hardware, modularize the firmware's interval schedule to avoid flooding the client browser with DOM updates.
- **Debugging Data Flows**: Use your browser's dev tools network tab to filter by WS (WebSocket) and watch live JSON frames to monitor exactly when the dashboard requests robot movements.

## Changelog

### 2026-08-03
- Implemented asynchronous queue processing for incoming WebSocket telemetry data.
- Optimized map rendering for concurrently simulated robots across 4 warehouse flights.
- Fixed a bug where offline simulated robots remained scheduled for active tasks.
- Enhanced dashboard UI for mobile resolution support using responsive Canvas wrappers.

### 2026-07-15
- Introduced an initial A/V prototype for "adaptive speed shuffle mode".
- Corrected H2 database fallback mode to safely load test fixtures on legacy hardware pagination.
- Optimized MySQL connection pooling to restrict stale threads during dashboard concurrency.

### 2026-07-01
- Launched initial dashboard track events.
- Enforced strict RFID scanning limits to resolve map stutters.
- Added a Helm chart package option to quickly integrate the dashboard against existing K8s clusters.

## Contributing

We welcome framework integrations and feature extensions! If you have a suggestion for extending the dashboard:

1. Fork the repository.
2. Create a feature branch for your proposed backend or frontend changes.
3. Submit a pull request.

> **Note:** For all telemetry changes, please tag your PR with the label `telemetry-edit` so we can closely monitor dashboard topology updates.

## License

This repository is distributed under the MIT License. See the `LICENSE` file for more information.
