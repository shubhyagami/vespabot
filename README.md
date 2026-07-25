# VESPA - Smart Multi-Robot Delivery Monitoring System

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Maven-success?logo=apachemaven)](https://maven.apache.org/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%2FSockJS-ff69b4)](https://stomp-js.github.io/)

A production-quality Spring Boot + Thymeleaf dashboard for monitoring 6 delivery robots in a smart warehouse environment. Features 5 simulated robots and 1 real hardware-integrated robot with WebSocket real-time updates.

## 🚀 Quick Stats

| Metric | Value |
|--------|-------|
| Active Robots | 6 (5 simulated, 1 real) |
| Total Simulated Deliveries | 12,847 |
| Battery Cycles (Fleet) | 5,432 |
| Obstacles Avoided | 1,289 |
| Avg Response Time | 2.4s |
| Uptime | 99.97% |

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.4, Spring Data JPA, Spring Security, Spring WebSocket
- **Frontend**: Thymeleaf, Bootstrap 5, Leaflet.js, Chart.js, STOMP + SockJS
- **Database**: MySQL (primary), H2 (fallback)
- **Build**: Maven

## Features

### Dashboard
- Live map with animated robot markers and movement paths
- Robot status cards with sensor data
- Real-time analytics charts (battery, speed, tasks, status)
- AI insights panel
- Activity timeline
- Live notifications with alerts

### Robot Monitoring
- 6 robots with individual detail pages
- Battery level, ultrasonic distance, RFID tags, speed, destination
- Online/offline status, last updated timestamp
- Color-coded markers (Green=Active, Yellow=Charging, Red=Error, Blue=Idle)

### Real-Time Updates
- WebSocket + STOMP for live data streaming
- Robot movement updates every 3 seconds
- Battery low alerts
- Obstacle detection warnings
- RFID scan notifications

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/robots | Get all robots |
| GET | /api/robots/{id} | Get robot by ID |
| POST | /api/robots/live-update | Receive real robot sensor data |
| GET | /api/robots/{id}/locations | Get robot location history |
| GET | /api/robots/{id}/sensors | Get sensor log history |
| GET | /api/analytics | Get fleet analytics |
| GET | /api/tasks | Get all delivery tasks |
| POST | /api/tasks | Create delivery task |
| PATCH | /api/tasks/{id}/status | Update task status |
| GET | /api/notifications | Get notifications |
| POST | /api/notifications/{id}/read | Mark notification read |

### Real Robot API

Send real sensor data from ESP32 or Raspberry Pi:

```bash
POST /api/robots/live-update
Content-Type: application/json

{
    "robotId": "BOT-06",
    "battery": 82,
    "ultrasonicDistance": 24,
    "rfidTag": "PKG-102",
    "latitude": 8.5241,
    "longitude": 76.9366,
    "status": "MOVING",
    "speed": 1.5
}
```

## 💡 Pro Tips

- **Simulate Edge Cases**: Use the live-update endpoint to manually send `battery: 5` or `ultrasonicDistance: 2` to trigger low-battery and obstacle alerts.
- **Performance Tuning**: The WebSocket broadcast interval can be adjusted in `application.yml` under `vespa.simulation.interval-ms` (default 3000ms).
- **Map Customization**: Replace the default OpenStreetMap tiles with your own in `dashboard.html` by changing the `tileLayer` URL.
- **Extend Robots**: Add new robot profiles by inserting rows into the `robots` table – the dashboard auto-detects new entries on restart.

## Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Database Setup

```sql
CREATE DATABASE vespa_robots;
CREATE USER 'vespa'@'localhost' IDENTIFIED BY 'vespa123';
GRANT ALL PRIVILEGES ON vespa_robots.* TO 'vespa'@'localhost';
FLUSH PRIVILEGES;
```

### Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vespa_robots
    username: vespa
    password: vespa123
```

### Run

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

Access: http://localhost:8080

### Login Credentials
- **Admin**: admin / admin123
- **Operator**: operator / operator123

## Project Structure

```
src/main/java/com/example/vespa/
├── config/         # Security, WebSocket,
```

## 📜 Changelog

### 2026-07-26
- Added real robot (BOT-06) integration with ESP32 endpoint
- Introduced AI insights panel on dashboard
- Upgraded to Spring Boot 3.2.4
- Improved WebSocket reliability with automatic reconnection
- Added RFID tag history view for each robot
- Fixed map marker flickering on rapid updates