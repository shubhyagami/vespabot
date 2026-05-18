package com.example.vespa.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RobotDTO {
    private String id;
    private String name;
    private String status;
    private Integer battery;
    private Double ultrasonicDistance;
    private String rfidTag;
    private Double speed;
    private String currentDestination;
    private Boolean online;
    private Double latitude;
    private Double longitude;
    private LocalDateTime lastUpdated;
    private Double totalDistance;
    private Integer tasksCompleted;
    private Long uptimeSeconds;
    private Boolean isReal;
}
