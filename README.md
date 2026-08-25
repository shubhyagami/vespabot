# VESPA: Smart Multi-Robot Delivery Monitoring System

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-success?logo=apachemaven)](https://maven.apache.org/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%2FSockJS-ff69b4)](https://stomp-js.github.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)](https://github.com/shubhyagami/vespabot)

## Overview

VESPA is a cutting-edge monitoring system designed for managing a fleet of delivery robots in a smart warehouse environment. Built on top of Spring Boot, this ecosystem provides real-time telemetry updates via WebSocket connections, offering valuable insights into the robots' movement, sensor data, and system status.

## Features

### Live Dashboard

- Animated map with robot markers and movement paths for easy navigation
- Real-time analytics charts for battery level, speed, tasks, and status to track performance
- Color-coded map markers for active, charging, error, and idle robots for at-a-glance status

### Robot Monitoring

- Individual detail pages for each robot with live data updates
- Displaying essential metrics such as battery level, ultrasonic distance, RFID tags, speed, and destination

### Status Indicators

- Online/offline status and last updated timestamps for real-time tracking
- Low battery alerts, obstacle detection warnings, and RFID scan notifications for timely interventions

### Real-Time Updates

- Live data streaming via WebSocket and STOMP (every 3 seconds) for maximum responsiveness
- Automatic adaptation to high-frequency packet bursts for a seamless user experience
- Modularized firmware for easy physical hardware integration

## Technologies and Tools

* **Backend**: Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security, Spring WebSocket
* **Frontend**: Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js
* **Database**: MySQL (primary) and H2 (fallback) for reliable data storage
* **Build Tool**: Maven
* **WebSocket Library**: STOMP + SockJS for robust and high-performance communication

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

## Contributing

We welcome framework integrations, feature extensions, and code improvements! If you'd like to contribute:

1. Fork the repository
2. Create a feature branch for your proposed changes
3. Submit a pull request for review and discussion

## License

This repository is distributed under the MIT License. See the `LICENSE` file for more information.

## Recent Changes

* **2026-08-03**: Enhanced asynchronous queue processing for incoming WebSocket telemetry data
* **2026-07-15**: Introduced initial dashboard track events and enforced strict RFID scanning limits
* **2026-07-01**: Optimized MySQL connection pooling and added a Helm chart package option for Kubernetes integration
