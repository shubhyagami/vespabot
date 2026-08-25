# VESPA - Smart Multi-Robot Delivery Monitoring System

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-success?logo=apachemaven)](https://maven.apache.org/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%2FSockJS-ff69b4)](https://stomp-js.github.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)](https://github.com/shubhyagami/vespabot)

## Overview

VESPA is a Spring Boot-based ecosystem designed for monitoring a fleet of delivery robots in a smart warehouse environment. It provides real-time telemetry updates through WebSocket connections, offering insights into the robots' movement, sensor data, and system status.

## Key Features

### Live Dashboard

- Animated map with robot markers and movement paths
- Real-time analytics charts for battery, speed, tasks, and status
- Color-coded map markers for active, charging, error, and idle robots

### Robot Monitoring

- Individual detail pages for each of the 6 robots
- Displaying battery level, ultrasonic distance, RFID tags, speed, and destination

### Status Indicators

- Online/offline status and last updated timestamps
- Low battery alerts, obstacle detection warnings, and RFID scan notifications

### Real-Time Updates

- Live data streaming via WebSocket + STOMP (every 3 seconds)
- Automatic adaptation to high-frequency packet bursts
- Modularized firmware for physical hardware integration

## Technologies and Tools

- **Backend**: Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security, Spring WebSocket
- **Frontend**: Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js
- **Database**: MySQL (primary) and H2 (fallback)
- **Build Tool**: Maven
- **WebSocket Library**: STOMP + SockJS

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/shubhyagami/vespabot.git
cd vespabot
```

### Configure the Database
Update `src/main/resources/application.properties` to point to your MySQL instance or rely on the automatic H2 fallback:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vespa_db
spring.datasource.username=root
spring.datasource.password=your_password

# H2 Fallback (Enabled automatically if MySQL is unreachable)
spring.h2.console.enabled=true
```

### Run the Application
```bash
./mvnw spring-boot:run
```

### Access the Dashboard
```http://localhost:8080```

## Operational Notes

- **WebSocket Tuning**: Adjust the heartbeat interval in `WebSocketConfig.java` for optimal performance.
- **Hardware Integration**: Modularize the firmware's interval schedule to avoid flooding the client browser with DOM updates.

## Contributing

We welcome framework integrations and feature extensions! If you have a suggestion for extending the dashboard:

1. Fork the repository
2. Create a feature branch for your proposed changes
3. Submit a pull request

## License

This repository is distributed under the MIT License. See the `LICENSE` file for more information.

### Recent Changes

* **2026-08-03**: Implemented asynchronous queue processing for incoming WebSocket telemetry data
* **2026-07-15**: Launched initial dashboard track events and enforced strict RFID scanning limits
* **2026-07-01**: Optimized MySQL connection pooling and added a Helm chart package option for Kubernetes integration.
