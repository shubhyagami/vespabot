# VESPA Fleet OS — API Documentation

**Base URL:** `https://vespabot.onrender.com` (production) or `http://localhost:8080` (local)

**Auth:** HTTP Basic — `admin` / `admin123` (except ESP8266 live-update which is public)

---

## 1. REST Endpoints

### 1.1 Robots

#### `GET /api/robots`
Returns all robots.

**Auth:** Required

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "id": "BOT-01",
      "name": "Atlas",
      "status": "MOVING",
      "battery": 78,
      "ultrasonicDistance": 45.2,
      "rfidTag": "PKG-301",
      "speed": 1.2,
      "currentDestination": "Warehouse",
      "online": true,
      "latitude": 8.5241,
      "longitude": 76.9366,
      "totalDistance": 1250.0,
      "tasksCompleted": 42,
      "uptimeSeconds": 36000,
      "isReal": true,
      "lastUpdated": "2026-05-18T22:00:00"
    }
  ],
  "timestamp": "2026-05-18T22:00:00"
}
```

#### `GET /api/robots/{id}`
Returns a single robot by ID.

**Auth:** Required

**Path params:** `id` — robot ID (e.g. `BOT-01`)

---

#### `POST /api/robots/live-update`
Push real-time sensor data from ESP8266.

**Auth:** Not required (`permitAll`)

**Body:**
```json
{
  "robotId": "BOT-01",
  "battery": 78,
  "ultrasonicDistance": 45.2,
  "rfidTag": "PKG-301",
  "status": "MOVING",
  "speed": 1.2
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `robotId` | string | **yes** | `"BOT-01"` for the real bot |
| `battery` | integer | no | 0–100 |
| `ultrasonicDistance` | double | no | cm |
| `rfidTag` | string | no | Scanned package tag |
| `status` | string | no | `MOVING`, `CHARGING`, `ERROR`, `IDLE` |
| `speed` | double | no | m/s |

**Triggers:**
- If `battery ≤ 20` → `BATTERY_LOW` notification + alert to `/topic/notifications`
- If `ultrasonicDistance ≤ 10` → `OBSTACLE` warning
- If `rfidTag` present → logs RFID scan to `/topic/rfid`
- Broadcasts updated robot to `/topic/robots`

---

#### `GET /api/robots/{id}/locations`
Returns location history for a robot.

**Auth:** Required

**Query params:**

| Param | Type | Description |
|---|---|---|
| `minutes` | integer | (optional) Only return records from last N minutes |

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "robotId": "BOT-01",
      "latitude": 8.5241,
      "longitude": 76.9366,
      "recordedAt": "2026-05-18T22:00:00"
    }
  ]
}
```

#### `GET /api/robots/{id}/sensors`
Returns sensor log history.

**Auth:** Required

**Query params:**

| Param | Type | Description |
|---|---|---|
| `minutes` | integer | (optional) Only return records from last N minutes |

---

### 1.2 Analytics

#### `GET /api/analytics`
Returns fleet-wide analytics.

**Auth:** Required

**Response:**
```json
{
  "success": true,
  "data": {
    "totalRobots": 6,
    "activeRobots": 3,
    "chargingRobots": 1,
    "idleRobots": 2,
    "errorRobots": 0,
    "averageBattery": 72.5,
    "totalTasksCompleted": 156,
    "totalDistanceTraveled": 8500.0,
    "totalNotifications": 5,
    "batteryHistory": [
      { "robotId": "BOT-01", "robotName": "Atlas", "battery": 78, "timestamp": "..." }
    ],
    "taskHistory": [
      { "date": "2026-05-18", "completed": 12, "pending": 3 }
    ]
  }
}
```

---

### 1.3 Delivery Tasks

#### `GET /api/tasks`
Returns all delivery tasks.

**Auth:** Required

#### `GET /api/tasks/{id}`
Returns a single task.

**Auth:** Required

#### `POST /api/tasks`
Create a new delivery task.

**Auth:** Required

**Body:**
```json
{
  "robotId": "BOT-01",
  "packageId": "PKG-301",
  "pickupLocation": "Warehouse A",
  "deliveryLocation": "Zone 3",
  "priority": "HIGH"
}
```

#### `PATCH /api/tasks/{id}/status`
Update task status.

**Auth:** Required

**Query params:** `status` — `PENDING`, `IN_PROGRESS`, `COMPLETED`, `FAILED`

#### `GET /api/tasks/robot/{robotId}`
Returns tasks assigned to a specific robot.

**Auth:** Required

---

### 1.4 Notifications

#### `GET /api/notifications`
Returns recent notifications (latest 50).

**Auth:** Required

#### `GET /api/notifications/unread`
Returns unread notification count + list.

**Auth:** Required

#### `POST /api/notifications/{id}/read`
Mark a single notification as read.

**Auth:** Required

#### `POST /api/notifications/read-all`
Mark all notifications as read.

**Auth:** Required

---

## 2. WebSocket / STOMP

### Connection

```
Endpoint: /ws
Protocol: STOMP over SockJS
```

**Client example:**
```js
const socket = new SockJS('https://vespabot.onrender.com/ws');
const client = Stomp.over(socket);
client.connect('admin', 'admin123', () => {
  // subscribed
});
```

### Subscribe Topics

| Topic | Payload | Frequency |
|---|---|---|
| `/topic/robots` | `RobotDTO` (single robot update) | Every 3s |
| `/topic/zones` | `ZoneDTO` | Every 3s |
| `/topic/notifications` | `Notification` | On event |
| `/topic/rfid` | `RFIDLog` | On RFID scan |

### `/topic/robots` payload
```json
{
  "id": "BOT-01",
  "name": "Atlas",
  "status": "MOVING",
  "battery": 78,
  "ultrasonicDistance": 45.2,
  "speed": 1.2,
  "currentDestination": "Warehouse",
  "online": true,
  "totalDistance": 1250.0,
  "tasksCompleted": 42,
  "uptimeSeconds": 36000,
  "isReal": true
}
```

### `/topic/zones` payload
```json
{
  "zoneId": "ZONE-1",
  "zoneName": "Technopark",
  "temperature": 29.2,
  "humidity": 68.5,
  "distance": 152.3,
  "gasLevel": 45.7,
  "vibration": 1.2,
  "lightIntensity": 720,
  "status": "ACTIVE",
  "isReal": true,
  "recentLogs": [
    "ESP8266 sensor reading @ 14:32:15",
    "Temp: 29.2C, Humidity: 68.5%"
  ]
}
```

### `/topic/notifications` payload
```json
{
  "id": 1,
  "type": "BATTERY_LOW",
  "message": "Robot BOT-02 battery low: 15%",
  "robotId": "BOT-02",
  "createdAt": "2026-05-18T22:00:00",
  "isRead": false
}
```

### `/topic/rfid` payload
```json
{
  "id": 1,
  "robotId": "BOT-01",
  "rfidTag": "PKG-301",
  "action": "SCAN",
  "description": "RFID scanned: PKG-301",
  "recordedAt": "2026-05-18T22:00:00"
}
```

---

## 3. Common Response Wrapper

All REST endpoints return:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-05-18T22:00:00"
}
```

On error:

```json
{
  "success": false,
  "message": "Robot not found with id: BOT-99",
  "data": null,
  "timestamp": "2026-05-18T22:00:00"
}
```

---

## 4. Status Values

| Robot Status | Meaning | Color |
|---|---|---|
| `MOVING` | In transit | Green `#10b981` |
| `CHARGING` | At charging station | Amber `#f59e0b` |
| `ERROR` | System error | Red `#ef4444` |
| `OBSTACLE` | Obstacle detected | Red `#ef4444` |
| `IDLE` | Waiting for task | Gray `#6b7280` |

| Notification Type | Description |
|---|---|
| `BATTERY_LOW` | Battery ≤ 20% |
| `OBSTACLE` | Ultrasonic distance ≤ 10cm |
| `RFID_SCAN` | Package RFID scanned |
| `INFO` | General info |

---

## 5. ESP8266 Integration

Push sensor data every 3 seconds:

```cpp
POST /api/robots/live-update
Content-Type: application/json

{
  "robotId": "BOT-01",
  "battery": 78,
  "ultrasonicDistance": 45.2,
  "status": "MOVING",
  "speed": 1.2
}
```

No auth required. See Arduino example in `/docs/arduino/esp8266.ino`.
