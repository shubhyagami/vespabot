# React Frontend for Vespa Robot Fleet Manager

## Backend Info
- **Base URL:** `http://localhost:8080`
- **Auth:** HTTP Basic — `admin` / `admin123`
- **WebSocket:** STOMP over SockJS at `http://localhost:8080/ws`

## Tech Stack
- React 18+ with Vite
- Leaflet + react-leaflet for map
- STOMP.js + SockJS-client for WebSocket
- Recharts or Chart.js for analytics
- Tailwind CSS or styled-components

## Layout (Single Page, No Router Needed)

```
┌──────────────────────────────────────────────────┐
│ HEADER: "VESPA Fleet OS" | System Clock | Live   │
├──────────────────────────────────────────────────┤
│ SIDEBAR (collapsible): Dashboard, Analytics, etc  │
├──────────────────────────────────────────────────┤
│ Stats Bar: Total Robots | Active | Avg Battery    │
│            Tasks Done | Alerts | Uptime           │
├──────────────────────────────────────────────────┤
│ MAP (Leaflet, center 8.5241, 76.9366, zoom 15)   │
│  5 simulated bots: BLUE dotted circle markers     │
│  1 real bot (ESP8266): RED dashed circle marker   │
│  On click popup: name, status, battery, speed     │
│  Legend: Simulated(blue) / Real(red dashed)       │
├──────────────────────────────────────────────────┤
│ ESP8266 Live Sensor Data (6 boxes in a row)       │
│  Temperature | Humidity | Ultrasonic | Gas        │
│  Vibration | Light Intensity                      │
│  All updated from Zone-1 WebSocket data           │
├──────────────────────────────────────────────────┤
│ Zone Monitoring (2 rows x 3 cols = 6 cards)       │
│  ZONE-1 (Technopark):                            │
│    Badge: "REAL" in red, "ACTIVE" status         │
│    Shows live ESP8266 data + logs                 │
│  ZONE-2 to ZONE-6 (Kazhakuttom, Sreekariyam,     │
│    Peroorkada, Kovalam, Pangode):                 │
│    Badge: "SIM" in blue                          │
│    Shows random simulated data + logs            │
│  Each card shows: temp, humidity, distance, gas   │
│  Scrollable log list                              │
├──────────────────────────────────────────────────┤
│ AI Insights (left) | Activity Timeline (right)    │
└──────────────────────────────────────────────────┘
```

## WebSocket Subscriptions

```javascript
// Connect
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);
client.connect('admin', 'admin123', () => {
  // Subscribe to all topics
});

// Topic: /topic/robots (every 3 seconds)
{
  id: "BOT-01", name: "Atlas",
  status: "MOVING", battery: 85,
  latitude: 8.5300, longitude: 76.9400,
  speed: 1.2, ultrasonicDistance: 45.0,
  rfidTag: "PKG-301", currentDestination: "Technopark",
  online: true, totalDistance: 1250.0,
  tasksCompleted: 42, uptimeSeconds: 36000,
  isReal: false  // true for BOT-06 only
}

// Topic: /topic/zones (6 messages, every 3 seconds)
{
  zoneId: "ZONE-1", zoneName: "Technopark",
  temperature: 29.2, humidity: 68.5,
  distance: 152.3, gasLevel: 45.7,
  vibration: 1.2, lightIntensity: 720,
  status: "ACTIVE", // or "NORMAL", "ALERT"
  recentLogs: [
    "ESP8266 sensor reading @ 14:32:15",
    "Temp: 29.2C, Humidity: 68.5%",
    "Distance: 152 cm",
    "Gas: 45.7 ppm - SAFE"
  ],
  isReal: true  // ZONE-1 only
}

// Topic: /topic/notifications (on event)
{
  id: 1, type: "BATTERY_LOW",
  message: "Gemini battery low: 15%",
  robotId: "BOT-02", createdAt: "2026-...",
  isRead: false
}

// Topic: /topic/rfid (on RFID scan)
{
  id: 1, robotId: "BOT-01",
  rfidTag: "PKG-301", action: "SCAN",
  description: "RFID scanned: PKG-301",
  recordedAt: "2026-..."
}
```

## REST API Calls (for initial page load)

```javascript
// GET /api/robots — fetch all robots
fetch('http://localhost:8080/api/robots', {
  headers: { Authorization: 'Basic ' + btoa('admin:admin123') }
});

// GET /api/analytics
// GET /api/tasks
// GET /api/notifications
```

## Robot Status Colors (for map markers)
| Status  | Color   |
|---------|---------|
| MOVING  | #10b981 (green) |
| CHARGING| #f59e0b (amber) |
| ERROR/OBSTACLE | #ef4444 (red) |
| IDLE    | #6b7280 (gray)  |

### Marker style
- **Simulated bots** (`isReal: false`): Blue circle (`#3b82f6`), solid white border, status letter inside
- **Real bot** (`isReal: true`): Red circle (`#ef4444`), **dashed** white border, pulse animation
- Path trails: Blue dashed line for sim, Red dashed line for real

### Status letters inside marker
- M = Moving (green marker)
- C = Charging (amber marker)
- ! = Error/Obstacle (red marker)
- I = Idle (gray marker)

## Zone Card Details
- Zone name, REAL/SIM badge, status indicator (colored dot)
- 4 sensor values: temp, humidity, distance, gas
- Divider line
- Log list (4 most recent entries, scrollable)
- Background glow effect on hover

## Notes
- 6 simulated bots (BOT-01 to BOT-05, BOT-06 Sentinel is the real one)
- BOT-06 starts as simulated but gets marked as `isReal` after first tick
- Coordinates span ~0.015° around Trivandrum center
- Zone-1 (Technopark) is the "REAL" zone — its data comes from ESP8266 via the `POST /api/robots/live-update` endpoint
- Zones 2-6 generate random data in the simulator every 3s
- The ESP8266 sensor strip at top should update whenever Zone-1 data arrives
- Use `updateMapMarker(robotData)` pattern — interpolate marker movement with requestAnimationFrame
- Fit map bounds to show all markers (with padding)
- Store robot data in a map keyed by robot id
