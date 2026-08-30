# VESPA: Smart Delivery Robot Monitoring System  

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://openjdk.org/)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)  
[![Build](https://img.shields.io/badge/Build-Maven-success?logo=apachemaven)](https://maven.apache.org/)  
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%2FSockJS-ff69b4)](https://stomp-js.github.io/)  
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)  
[![GitHub stars](https://img.shields.io/github/stars/shubhyagami/vespabot?style=social)](https://github.com/shubhyagami/vespabot)  

---  

## Overview  
VESPA is a real‑time monitoring platform for fleets of delivery robots in smart warehouses. It ingests telemetry over a WebSocket (STOMP/SockJS) channel and visualizes robot status, battery levels, navigation data, and alerts on an interactive dashboard.

## Key Features  

- **Live Dashboard**  
  - Interactive map with robot markers and movement traces  
  - Real‑time charts for battery, speed, tasks, and system health  
  - Color‑coded markers for active, charging, error, and idle robots  

- **Robot Details**  
  - Per‑robot pages showing battery level, ultrasonic distance, RFID tags, speed, destination, and live sensor data  

- **Status Indicators**  
  - Online/offline flags and timestamps for the latest update  
  - Low‑battery warnings, obstacle‑detection alerts, and RFID‑scan notifications  

- **Real‑Time Updates**  
  - Telemetry streamed every 3 seconds via STOMP/SockJS  
  - Robust handling of bursty data packets  
  - Modular firmware design for easy hardware integration  

## Technology Stack  

- **Backend:** Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security, Spring WebSocket  
- **Frontend:** Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js  
- **Database:** MySQL (primary) with automatic H2 fallback  
- **Build Tool:** Maven  
- **WebSocket Library:** STOMP + SockJS  

## Getting Started  

1. **Prerequisites**  
   - Java 17 or later  
   - Maven 3.9+  
   - MySQL server (or use the built‑in H2 fallback for quick testing)  

2. **Clone the Repository**  
   ```bash
   git clone https://github.com/shubhyagami/vespabot.git
   cd vespabot
   ```  

3. **Configure the Database**  
   Edit `src/main/resources/application.properties` to point to your MySQL instance. For rapid local testing you can keep the default H2 settings:  
   ```properties
   spring.datasource.url=jdbc:h2:mem:vespa_db;DB_CLOSE_DELAY=-1
   spring.datasource.driverClassName=org.h2.Driver
   ```  

4. **Run the Application**  
   ```bash
   ./mvnw spring-boot:run
   ```  

5. **Open the Dashboard**  
   Navigate to `http://localhost:8080` in your browser.  

## Contributing  

Contributions are welcome—whether you’re adding new integration modules, extending features, or polishing the codebase:

1. Fork the repository.  
2. Create a dedicated branch for your work.  
3. Submit a pull request for review.  

## License  

Distributed under the MIT License. See the `LICENSE` file for details.  

## Recent Updates  

- **2026‑08‑03:** Enhanced asynchronous queue processing for incoming telemetry streams.  
- **2026‑07‑15:** Added dashboard navigation markers and tightened RFID scanning limits.  
- **2026‑07‑01:** Optimized MySQL connection pooling and introduced Helm‑chart support for Kubernetes deployments.  

---  

*VESPA – Real‑time insight for smarter warehouse logistics.*
