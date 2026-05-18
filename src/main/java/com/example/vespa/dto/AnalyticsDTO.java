package com.example.vespa.dto;

import lombok.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDTO {
    private long totalRobots;
    private long activeRobots;
    private long chargingRobots;
    private long idleRobots;
    private long errorRobots;
    private double averageBattery;
    private long totalTasksCompleted;
    private double totalDistanceTraveled;
    private long totalNotifications;
    private List<BatteryDataPoint> batteryHistory;
    private List<TaskDataPoint> taskHistory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BatteryDataPoint {
        private String robotId;
        private String robotName;
        private Integer battery;
        private String timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskDataPoint {
        private String date;
        private long completed;
        private long pending;
    }
}
