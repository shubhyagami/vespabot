package com.example.vespa.service;

import com.example.vespa.dto.LiveUpdateRequest;
import com.example.vespa.dto.RobotDTO;
import com.example.vespa.entity.*;
import com.example.vespa.exception.ResourceNotFoundException;
import com.example.vespa.repository.*;
import com.example.vespa.websocket.WebSocketHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RobotService {

    private final RobotRepository robotRepository;
    private final RobotLocationRepository locationRepository;
    private final SensorLogRepository sensorLogRepository;
    private final RFIDLogRepository rfidLogRepository;
    private final NotificationRepository notificationRepository;
    private final WebSocketHandler wsHandler;

    public RobotService(RobotRepository robotRepository,
                        RobotLocationRepository locationRepository,
                        SensorLogRepository sensorLogRepository,
                        RFIDLogRepository rfidLogRepository,
                        NotificationRepository notificationRepository,
                        WebSocketHandler wsHandler) {
        this.robotRepository = robotRepository;
        this.locationRepository = locationRepository;
        this.sensorLogRepository = sensorLogRepository;
        this.rfidLogRepository = rfidLogRepository;
        this.notificationRepository = notificationRepository;
        this.wsHandler = wsHandler;
    }

    public List<RobotDTO> getAllRobots() {
        return robotRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RobotDTO getRobotById(String id) {
        Robot robot = robotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot", id));
        return toDTO(robot);
    }

    public RobotDTO updateRobot(String id, RobotDTO dto) {
        Robot robot = robotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Robot", id));
        BeanUtils.copyProperties(dto, robot, "id", "lastUpdated", "totalDistance", "tasksCompleted", "uptimeSeconds");
        robot.setLastUpdated(LocalDateTime.now());
        Robot saved = robotRepository.save(robot);
        RobotDTO result = toDTO(saved);
        wsHandler.broadcastRobotUpdate(result);
        return result;
    }

    public RobotDTO processLiveUpdate(LiveUpdateRequest request) {
        Robot robot = robotRepository.findById(request.getRobotId())
                .orElseGet(() -> {
                    Robot newBot = Robot.builder()
                            .id(request.getRobotId())
                            .name(request.getRobotId().replace("BOT-", "Robot "))
                            .isReal(true)
                            .build();
                    return robotRepository.save(newBot);
                });

        robot.setIsReal(true);
        robot.setOnline(true);
        robot.setLastUpdated(LocalDateTime.now());

        if (request.getBattery() != null) robot.setBattery(request.getBattery());
        if (request.getUltrasonicDistance() != null) robot.setUltrasonicDistance(request.getUltrasonicDistance());
        if (request.getRfidTag() != null && !request.getRfidTag().isBlank()) {
            robot.setRfidTag(request.getRfidTag());
            logRfidScan(request.getRobotId(), request.getRfidTag());
        }
        if (request.getLatitude() != null) robot.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) robot.setLongitude(request.getLongitude());
        if (request.getStatus() != null) robot.setStatus(request.getStatus());
        if (request.getSpeed() != null) robot.setSpeed(request.getSpeed());

        Robot saved = robotRepository.save(robot);

        saveSensorLog(request);
        saveLocation(request);

        String status = request.getStatus() != null ? request.getStatus() : saved.getStatus();
        checkBatteryAlert(request.getRobotId(), request.getBattery());
        checkObstacle(request.getRobotId(), request.getUltrasonicDistance(), status);

        RobotDTO result = toDTO(saved);
        wsHandler.broadcastRobotUpdate(result);
        return result;
    }

    private void saveSensorLog(LiveUpdateRequest request) {
        SensorLog log = SensorLog.builder()
                .robotId(request.getRobotId())
                .ultrasonicDistance(request.getUltrasonicDistance())
                .batteryLevel(request.getBattery())
                .status(request.getStatus())
                .speed(request.getSpeed() != null ? request.getSpeed() : 0.0)
                .build();
        sensorLogRepository.save(log);
        wsHandler.broadcastSensorUpdate(log);
    }

    private void saveLocation(LiveUpdateRequest request) {
        if (request.getLatitude() != null && request.getLongitude() != null) {
            RobotLocation loc = RobotLocation.builder()
                    .robotId(request.getRobotId())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .build();
            locationRepository.save(loc);
        }
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
        createNotification("RFID_SCAN", "Robot " + robotId + " scanned RFID: " + rfidTag, robotId);
    }

    private void checkBatteryAlert(String robotId, Integer battery) {
        if (battery != null && battery <= 20) {
            String msg = "Robot " + robotId + " battery low: " + battery + "%";
            createNotification("BATTERY_LOW", msg, robotId);
            wsHandler.broadcastBatteryAlert(msg);
        }
    }

    private void checkObstacle(String robotId, Double distance, String status) {
        if (distance != null && distance <= 10 && distance > 0) {
            String msg = "Robot " + robotId + " obstacle detected at " + distance + "cm";
            createNotification("OBSTACLE", msg, robotId);
            wsHandler.broadcastObstacleWarning(msg);
        }
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

    public void updateRobotFromSimulation(Robot robot) {
        if (Boolean.TRUE.equals(robot.getIsReal())) return;
        robot.setLastUpdated(LocalDateTime.now());
        robotRepository.save(robot);
        wsHandler.broadcastRobotUpdate(toDTO(robot));
    }

    private RobotDTO toDTO(Robot robot) {
        RobotDTO dto = new RobotDTO();
        BeanUtils.copyProperties(robot, dto);
        return dto;
    }
}
