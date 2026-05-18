package com.example.vespa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "robots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Robot {
    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(length = 20)
    @Builder.Default
    private String status = "IDLE";

    @Builder.Default
    private Integer battery = 100;

    @Column(name = "ultrasonic_distance")
    @Builder.Default
    private Double ultrasonicDistance = 0.0;

    @Column(name = "rfid_tag", length = 50)
    private String rfidTag;

    @Builder.Default
    private Double speed = 0.0;

    @Column(name = "current_destination", length = 100)
    private String currentDestination;

    @Builder.Default
    private Boolean online = true;

    @Builder.Default
    private Double latitude = 8.5200;

    @Builder.Default
    private Double longitude = 76.9340;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "total_distance")
    private Double totalDistance = 0.0;

    @Column(name = "tasks_completed")
    private Integer tasksCompleted = 0;

    @Column(name = "uptime_seconds")
    private Long uptimeSeconds = 0L;

    @Column(name = "is_real")
    @Builder.Default
    private Boolean isReal = false;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
