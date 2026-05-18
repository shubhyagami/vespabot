# VESPA - Smart Multi-Robot Delivery Monitoring System

A production-quality Spring Boot + Thymeleaf dashboard for monitoring 6 delivery robots in a smart warehouse environment. Features 5 simulated robots and 1 real hardware-integrated robot with WebSocket real-time updates.

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
├── config/         # Security, WebSocket, DataLoader
├── controller/     # REST and MVC controllers
├── dto/            # Data transfer objects
├── entity/         # JPA entities
├── exception/      # Global exception handler
├── repository/     # Spring Data repositories
├── service/        # Business logic
├── simulator/      # Robot movement simulator
└── websocket/      # WebSocket message handler

src/main/resources/
├── static/
│   ├── css/style.css
│   ├── js/app.js
│   ├── js/map.js
│   └── js/charts.js
└── templates/
    ├── fragments/  # Navbar, sidebar, robot-cards, charts
    ├── login.html
    ├── dashboard.html
    ├── robot-details.html
    ├── analytics.html
    ├── sensor-logs.html
    ├── rfid-logs.html
    ├── tasks.html
    └── settings.html
```

## Architecture

- **Entities**: Robot, RobotLocation, SensorLog, RFIDLog, DeliveryTask, Notification
- **Real-time**: WebSocket STOMP broker with topics (/topic/robots, /topic/notifications, etc.)
- **Simulation**: 5 dummy robots moving randomly with changing battery/sensor values
- **Real Robot**: BOT-06 (Sentinel) accepts hardware API data, overrides simulation

## License

MIT
