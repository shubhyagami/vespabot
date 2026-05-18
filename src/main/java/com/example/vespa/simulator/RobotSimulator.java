package com.example.vespa.simulator;

import com.example.vespa.entity.*;
import com.example.vespa.dto.ZoneDTO;
import com.example.vespa.repository.*;
import com.example.vespa.websocket.WebSocketHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@lombok.RequiredArgsConstructor
public class RobotSimulator {

    private final RobotRepository robotRepository;
    private final RobotLocationRepository locationRepository;
    private final SensorLogRepository sensorLogRepository;
    private final RFIDLogRepository rfidLogRepository;
    private final NotificationRepository notificationRepository;
    private final WebSocketHandler wsHandler;

    @Value("${robot.simulation.enabled:true}")
    private boolean simulationEnabled;

    @Value("${robot.real-robot-id:BOT-06}")
    private String realRobotId;

    private static final double TVM_LAT = 8.5241;
    private static final double TVM_LNG = 76.9366;

    private final Map<String, double[]> currentTargets = new HashMap<>();
    private final Map<String, Integer> rfidCounter = new HashMap<>();
    private final List<String> rfidTags = Arrays.asList(
            "PKG-101", "PKG-102", "PKG-103", "PKG-104", "PKG-105",
            "PKG-201", "PKG-202", "PKG-301", "PKG-302", "PKG-401"
    );
    private final List<String> destinations_list = Arrays.asList(
            "Technopark Phase 1", "Technopark Phase 3", "Kazhakuttom",
            "Sreekariyam", "Kovalam Junction", "Pangode",
            "Peroorkada", "Vellayambalam", "PMG Junction",
            "Charging Station"
    );
    private final List<String> statusList = Arrays.asList("MOVING", "IDLE", "CHARGING", "MOVING", "MOVING");

    private static final String[][] ZONE_DESTINATIONS = {
        {"Technopark Main Gate", "Kazhakuttom", "Mangalapuram"},      // Zone 1
        {"Sreekariyam", "Kesavadasapuram", "Pattom"},                // Zone 2
        {"Vellayambalam", "PMG", "Museum Junction"},                 // Zone 3
        {"Peroorkada", "Thycadu", "Vazhuthacaud"},                   // Zone 4
        {"Kovalam Bypass", "Akkulam", "Kulathoor"},                  // Zone 5
        {"Pangode", "Pappanamcode", "International Airport"}         // Zone 6
    };

    @PostConstruct
    public void init() {
        if (simulationEnabled) {
            initializeRobots();
        }
    }

    private void initializeRobots() {
        if (robotRepository.count() > 0) return;

        List<Robot> initialRobots = Arrays.asList(
            Robot.builder().id("BOT-01").name("Atlas").status("MOVING").battery(85).ultrasonicDistance(45.0).rfidTag("PKG-301").speed(1.2).currentDestination("Technopark Phase 1").online(true).latitude(8.5300).longitude(76.9400).totalDistance(1250.0).tasksCompleted(42).uptimeSeconds(36000L).isReal(false).build(),
            Robot.builder().id("BOT-02").name("Gemini").status("CHARGING").battery(15).ultrasonicDistance(120.0).rfidTag(null).speed(0.0).currentDestination("Charging Station").online(true).latitude(8.5180).longitude(76.9420).totalDistance(890.0).tasksCompleted(28).uptimeSeconds(28800L).isReal(false).build(),
            Robot.builder().id("BOT-03").name("Nova").status("IDLE").battery(100).ultrasonicDistance(200.0).rfidTag("PKG-102").speed(0.0).currentDestination("Kovalam Junction").online(true).latitude(8.5280).longitude(76.9280).totalDistance(2100.0).tasksCompleted(67).uptimeSeconds(72000L).isReal(false).build(),
            Robot.builder().id("BOT-04").name("Orion").status("MOVING").battery(62).ultrasonicDistance(30.0).rfidTag("PKG-201").speed(1.5).currentDestination("Sreekariyam").online(true).latitude(8.5200).longitude(76.9300).totalDistance(1560.0).tasksCompleted(51).uptimeSeconds(50400L).isReal(false).build(),
            Robot.builder().id("BOT-05").name("Phoenix").status("ERROR").battery(45).ultrasonicDistance(5.0).rfidTag(null).speed(0.0).currentDestination("Maintenance Bay").online(true).latitude(8.5260).longitude(76.9340).totalDistance(780.0).tasksCompleted(19).uptimeSeconds(21600L).isReal(false).build(),
            Robot.builder().id("BOT-06").name("Sentinel").status("IDLE").battery(92).ultrasonicDistance(150.0).rfidTag(null).speed(0.0).currentDestination(null).online(true).latitude(TVM_LAT).longitude(TVM_LNG).totalDistance(3200.0).tasksCompleted(95).uptimeSeconds(86400L).isReal(false).build()
        );

        robotRepository.saveAll(initialRobots);

        for (Robot r : initialRobots) {
            double[] dest = {TVM_LAT + ThreadLocalRandom.current().nextDouble() * 0.02 - 0.01, TVM_LNG + ThreadLocalRandom.current().nextDouble() * 0.02 - 0.01};
            currentTargets.put(r.getId(), dest);
            rfidCounter.put(r.getId(), 0);
        }
    }

    @Scheduled(fixedRateString = "${robot.simulation.interval-ms:3000}")
    @Transactional
    public void simulate() {
        if (!simulationEnabled) return;

        List<Robot> robots = robotRepository.findAll();
        for (Robot robot : robots) {
            if (robot.getIsReal() == null) robot.setIsReal(false);
            if (Boolean.TRUE.equals(robot.getIsReal()) || realRobotId.equals(robot.getId())) {
                if (Boolean.FALSE.equals(robot.getIsReal())) {
                    robot.setIsReal(true);
                    robotRepository.save(robot);
                }
                continue;
            }
            simulateRobot(robot);
        }

        broadcastRobotStatuses();
        broadcastZoneData();
    }

    private void normalizeRobot(Robot robot) {
        if (robot.getBattery() == null) robot.setBattery(100);
        if (robot.getTotalDistance() == null) robot.setTotalDistance(0.0);
        if (robot.getUptimeSeconds() == null) robot.setUptimeSeconds(0L);
        if (robot.getTasksCompleted() == null) robot.setTasksCompleted(0);
        if (robot.getLatitude() == null) robot.setLatitude(TVM_LAT);
        if (robot.getLongitude() == null) robot.setLongitude(TVM_LNG);
        if (robot.getStatus() == null) robot.setStatus("IDLE");
        if (robot.getOnline() == null) robot.setOnline(true);
        if (robot.getIsReal() == null) robot.setIsReal(false);
    }

    private void simulateRobot(Robot robot) {
        normalizeRobot(robot);

        String status = statusList.get(ThreadLocalRandom.current().nextInt(statusList.size()));
        robot.setStatus(status);

        int batteryChange = ThreadLocalRandom.current().nextInt(-3, 2);
        int newBattery = Math.max(0, Math.min(100, robot.getBattery() + batteryChange));
        if ("CHARGING".equals(status) && robot.getBattery() < 100) {
            newBattery = Math.min(100, robot.getBattery() + ThreadLocalRandom.current().nextInt(2, 6));
        }
        robot.setBattery(newBattery);

        if ("MOVING".equals(status)) {
            double[] target = currentTargets.get(robot.getId());
            if (target == null) {
                target = new double[]{TVM_LAT + ThreadLocalRandom.current().nextDouble() * 0.02 - 0.01, TVM_LNG + ThreadLocalRandom.current().nextDouble() * 0.02 - 0.01};
                currentTargets.put(robot.getId(), target);
            }

            double lat = robot.getLatitude() + (target[0] - robot.getLatitude()) * 0.1;
            double lng = robot.getLongitude() + (target[1] - robot.getLongitude()) * 0.1;

            if (Math.abs(lat - target[0]) < 0.0005 && Math.abs(lng - target[1]) < 0.0005) {
                double[] newTarget = {TVM_LAT + ThreadLocalRandom.current().nextDouble() * 0.02 - 0.01, TVM_LNG + ThreadLocalRandom.current().nextDouble() * 0.02 - 0.01};
                currentTargets.put(robot.getId(), newTarget);
                robot.setCurrentDestination(destinations_list.get(ThreadLocalRandom.current().nextInt(destinations_list.size())));
                robot.setTasksCompleted(robot.getTasksCompleted() + 1);
                createNotification("TASK_COMPLETE", robot.getName() + " completed delivery", robot.getId());
            } else {
                robot.setLatitude(Math.round(lat * 10000.0) / 10000.0);
                robot.setLongitude(Math.round(lng * 10000.0) / 10000.0);
            }

            robot.setUltrasonicDistance(20 + ThreadLocalRandom.current().nextDouble() * 180);
            robot.setSpeed(0.5 + ThreadLocalRandom.current().nextDouble() * 2.0);
            double dist = robot.getTotalDistance() + 0.01;
            robot.setTotalDistance(Math.round(dist * 100.0) / 100.0);

            saveLocation(robot.getId(), robot.getLatitude(), robot.getLongitude());
        } else if ("IDLE".equals(status)) {
            robot.setSpeed(0.0);
            robot.setUltrasonicDistance(150 + ThreadLocalRandom.current().nextDouble() * 100);
        } else if ("CHARGING".equals(status)) {
            robot.setSpeed(0.0);
            robot.setUltrasonicDistance(50 + ThreadLocalRandom.current().nextDouble() * 50);
        } else if ("ERROR".equals(status)) {
            robot.setSpeed(0.0);
            robot.setUltrasonicDistance(ThreadLocalRandom.current().nextDouble() * 15);
            robot.setStatus("OBSTACLE");
            createNotification("OBSTACLE", robot.getName() + " detected obstacle!", robot.getId());
        }

        robot.setUptimeSeconds(robot.getUptimeSeconds() + 3);

        int counter = rfidCounter.getOrDefault(robot.getId(), 0);
        if (counter >= 5) {
            String tag = rfidTags.get(ThreadLocalRandom.current().nextInt(rfidTags.size()));
            robot.setRfidTag(tag);
            logRfidScan(robot.getId(), tag);
            rfidCounter.put(robot.getId(), 0);
        } else {
            rfidCounter.put(robot.getId(), counter + 1);
        }

        double ultrasonic = 20 + ThreadLocalRandom.current().nextDouble() * 180;
        if ("OBSTACLE".equals(robot.getStatus())) {
            ultrasonic = ThreadLocalRandom.current().nextDouble() * 10;
        }
        robot.setUltrasonicDistance(ultrasonic);

        saveSensorLog(robot.getId(), ultrasonic, robot.getBattery(), robot.getStatus(), robot.getSpeed());

        if (robot.getBattery() <= 20) {
            createNotification("BATTERY_LOW", robot.getName() + " battery low: " + robot.getBattery() + "%", robot.getId());
        }

        robot.setLastUpdated(LocalDateTime.now());
        robotRepository.save(robot);
    }

    private void saveLocation(String robotId, double lat, double lng) {
        RobotLocation loc = RobotLocation.builder()
                .robotId(robotId)
                .latitude(lat)
                .longitude(lng)
                .build();
        locationRepository.save(loc);
    }

    private void saveSensorLog(String robotId, double ultrasonic, int battery, String status, double speed) {
        SensorLog log = SensorLog.builder()
                .robotId(robotId)
                .ultrasonicDistance(ultrasonic)
                .batteryLevel(battery)
                .status(status)
                .speed(speed)
                .build();
        sensorLogRepository.save(log);
    }

    private void logRfidScan(String robotId, String rfidTag) {
        RFIDLog log = RFIDLog.builder()
                .robotId(robotId)
                .rfidTag(rfidTag)
                .action("SCAN")
                .description("RFID scanned: " + rfidTag)
                .build();
        rfidLogRepository.save(log);
        wsHandler.broadcastRfidScan(log);
    }

    private void createNotification(String type, String message, String robotId) {
        Notification notif = Notification.builder()
                .type(type)
                .message(message)
                .robotId(robotId)
                .build();
        Notification saved = notificationRepository.save(notif);
        wsHandler.broadcastNotification(saved);
    }

    private void broadcastRobotStatuses() {
        List<Robot> robots = robotRepository.findAll();
        robots.forEach(r -> wsHandler.broadcastRobotUpdate(
                com.example.vespa.dto.RobotDTO.builder()
                        .id(r.getId()).name(r.getName()).status(r.getStatus())
                        .battery(r.getBattery()).ultrasonicDistance(r.getUltrasonicDistance())
                        .rfidTag(r.getRfidTag()).speed(r.getSpeed())
                        .currentDestination(r.getCurrentDestination()).online(r.getOnline())
                        .latitude(r.getLatitude()).longitude(r.getLongitude())
                        .lastUpdated(r.getLastUpdated()).totalDistance(r.getTotalDistance())
                        .tasksCompleted(r.getTasksCompleted()).uptimeSeconds(r.getUptimeSeconds())
                        .isReal(r.getIsReal())
                        .build()
        ));
    }

    private final String[] zoneNames = {
        "Technopark", "Kazhakuttom", "Sreekariyam",
        "Peroorkada", "Kovalam", "Pangode"
    };

    private void broadcastZoneData() {
        for (int i = 0; i < 6; i++) {
            boolean isReal = (i == 0);
            ZoneDTO zone = ZoneDTO.builder()
                    .zoneId("ZONE-" + (i + 1))
                    .zoneName(zoneNames[i])
                    .temperature(isReal ? 28.0 + ThreadLocalRandom.current().nextDouble() * 4 : 26.0 + ThreadLocalRandom.current().nextDouble() * 6)
                    .humidity(isReal ? 65.0 + ThreadLocalRandom.current().nextDouble() * 10 : 55.0 + ThreadLocalRandom.current().nextDouble() * 25)
                    .distance(isReal ? ThreadLocalRandom.current().nextDouble() * 400 : ThreadLocalRandom.current().nextDouble() * 800)
                    .gasLevel(isReal ? ThreadLocalRandom.current().nextDouble() * 200 : ThreadLocalRandom.current().nextDouble() * 500)
                    .vibration(isReal ? ThreadLocalRandom.current().nextDouble() * 5 : ThreadLocalRandom.current().nextDouble() * 10)
                    .lightIntensity(isReal ? 500 + ThreadLocalRandom.current().nextDouble() * 1000 : 200 + ThreadLocalRandom.current().nextDouble() * 1500)
                    .status(isReal ? "ACTIVE" : ThreadLocalRandom.current().nextInt(5) == 0 ? "ALERT" : "NORMAL")
                    .recentLogs(Arrays.asList(
                            isReal ? "ESP8266 sensor reading @ " + LocalDateTime.now().toString().substring(11, 19) : "Zone " + (i + 1) + " heartbeat OK @ " + LocalDateTime.now().toString().substring(11, 19),
                            isReal ? "Temp: " + String.format("%.1f", 28.0 + ThreadLocalRandom.current().nextDouble() * 4) + "C, Humidity: " + String.format("%.1f", 65.0 + ThreadLocalRandom.current().nextDouble() * 10) + "%" : "Routine scan complete",
                            isReal ? "Distance: " + String.format("%.0f", ThreadLocalRandom.current().nextDouble() * 400) + " cm" : "Battery: " + ThreadLocalRandom.current().nextInt(60, 100) + "%",
                            isReal ? "Gas: " + String.format("%.1f", ThreadLocalRandom.current().nextDouble() * 200) + " ppm - SAFE" : "No anomalies detected"
                    ))
                    .isReal(isReal)
                    .build();
            wsHandler.broadcastZoneUpdate(zone);
        }
    }
}
