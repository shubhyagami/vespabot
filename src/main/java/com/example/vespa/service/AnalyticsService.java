package com.example.vespa.service;

import com.example.vespa.dto.AnalyticsDTO;
import com.example.vespa.entity.Robot;
import com.example.vespa.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final RobotRepository robotRepository;
    private final DeliveryTaskRepository taskRepository;
    private final NotificationRepository notificationRepository;
    private final SensorLogRepository sensorLogRepository;

    public AnalyticsService(RobotRepository robotRepository,
                            DeliveryTaskRepository taskRepository,
                            NotificationRepository notificationRepository,
                            SensorLogRepository sensorLogRepository) {
        this.robotRepository = robotRepository;
        this.taskRepository = taskRepository;
        this.notificationRepository = notificationRepository;
        this.sensorLogRepository = sensorLogRepository;
    }

    public AnalyticsDTO getAnalytics() {
        List<Robot> robots = robotRepository.findAll();

        long totalRobots = robots.size();
        long activeRobots = robotRepository.countByStatus("MOVING") + robotRepository.countByStatus("ACTIVE");
        long chargingRobots = robotRepository.countByStatus("CHARGING");
        long idleRobots = robotRepository.countByStatus("IDLE");
        long errorRobots = robotRepository.countByStatus("ERROR") + robotRepository.countByStatus("OBSTACLE");
        double avgBattery = robotRepository.getAverageBattery();
        long totalTasks = robotRepository.getTotalTasksCompleted();
        double totalDist = robotRepository.getTotalDistanceTraveled();
        long totalNotifs = notificationRepository.countByIsReadFalse();

        List<AnalyticsDTO.BatteryDataPoint> batteryHistory = robots.stream()
                .map(r -> AnalyticsDTO.BatteryDataPoint.builder()
                        .robotId(r.getId())
                        .robotName(r.getName())
                        .battery(r.getBattery())
                        .timestamp(r.getLastUpdated() != null ? r.getLastUpdated().toString() : "")
                        .build())
                .collect(Collectors.toList());

        List<Object[]> rawTaskStats = taskRepository.getTaskStatsByDate();
        List<AnalyticsDTO.TaskDataPoint> taskHistory = rawTaskStats.stream()
                .map(row -> AnalyticsDTO.TaskDataPoint.builder()
                        .date(String.valueOf(row[0]))
                        .completed(row[1] != null ? ((Number) row[1]).longValue() : 0)
                        .pending(row[2] != null ? ((Number) row[2]).longValue() : 0)
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDTO.builder()
                .totalRobots(totalRobots)
                .activeRobots(activeRobots)
                .chargingRobots(chargingRobots)
                .idleRobots(idleRobots)
                .errorRobots(errorRobots)
                .averageBattery(avgBattery)
                .totalTasksCompleted(totalTasks)
                .totalDistanceTraveled(totalDist)
                .totalNotifications(totalNotifs)
                .batteryHistory(batteryHistory)
                .taskHistory(taskHistory)
                .build();
    }
}
