# VESPA – Smart Delivery Robot Monitoring

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-success?logo=apachemaven)](https://maven.apache.org/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%2FSockJS-ff69b4)](https://stomp-js.github.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)](https://github.com/shubhyagami/vespabot)

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Contributing](#contributing)
- [License](#license)
- [Changelog](#changelog)

---

## Overview
VESPA is a real‑time monitoring platform for fleets of delivery robots operating in smart warehouses.  
It receives telemetry over a STOMP/SockJS WebSocket channel and visualises robot status, battery levels, navigation data, and alerts on an interactive dashboard.

---

## Features
- **Live Dashboard**
  - Interactive map with robot markers and movement traces
  - Real‑time charts for battery, speed, tasks, and system health
  - Color‑coded markers: active, charging, error, idle
- **Robot Details Page**
  - Battery level, ultrasonic distance, RFID tags, speed, destination, and live sensor data
- **Status Indicators**
  - Online/offline flags with last‑update timestamps
  - Low‑battery warnings, obstacle‑detection alerts, RFID‑scan notifications
- **Real‑Time Updates**
  - Telemetry streamed every 3 seconds via STOMP/SockJS
  - Robust handling of bursty data packets
  - Modular firmware design for easy hardware integration

---

## Architecture
```
+-----------------+        +-----------------+        +-----------------+
|  Delivery Robot| <----> |  WebSocket      | <----> |  Frontend (Thymeleaf/Bootstrap) |
|  (Telemetry)   |        |  (STOMP/SockJS) |        |  (Leaflet, Chart.js)     |
+-----------------+        +-----------------+        +-----------------+
                                   |
                                   v
                           +-----------------+
                           |  Spring Boot    |
                           |  (Java 17)      |
                           +-----------------+
                                   |
                                   v
                           +-----------------+
                           |  Database       |
                           |  (MySQL / H2)   |
                           +-----------------+
```

The backend exposes REST endpoints and WebSocket endpoints, persists telemetry in MySQL (or H2 for testing), and serves static resources via Thymeleaf.

---

## Technology Stack
- **Backend**: Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security, Spring WebSocket
- **Frontend**: Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js
- **Database**: MySQL (primary) with automatic H2 fallback
- **Build Tool**: Maven

---

## Prerequisites
- Java 17 or newer
- Maven 3.9 or newer
- MySQL server (or simply use the H2 fallback)

---

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/shubhyagami/vespabot.git
   cd vespabot
   ```

2. **Configure the database**  
   Edit `src/main/resources/application.properties` to point to your database.  
   For quick local testing you can keep the default H2 settings:
   ```properties
   spring.datasource.url=jdbc:h2:mem:vespa_db;DB_CLOSE_DELAY=-1
   spring.datasource.driverClassName=org.h2.Driver
   ```

3. **Build and run**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the dashboard**  
   Open your browser and navigate to `http://localhost:8080`.

---

## Contributing
Contributions are welcome! Here’s the preferred workflow:

1. Fork the repository.  
2. Create a new branch: `git checkout -b feature/your-feature`.  
3. Commit your changes.  
4. Push the branch and open a pull request.  

Please follow the existing code style and include tests where appropriate.

---

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

---

## Changelog
- **2026‑08‑03** – Asynchronous queue processing for incoming telemetry streams.  
- **2026‑07‑15** – Added navigation markers and tightened RFID scanning limits.  
- **2026‑07‑01** – Optimised MySQL connection pooling and added Helm charts for Kubernetes deployments.  

## Contact
Questions or feature requests? Open an issue on GitHub or reach out to the maintainer directly.
