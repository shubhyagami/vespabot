package com.example.vespa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "robot_id", nullable = false, length = 20)
    private String robotId;

    @Column(name = "ultrasonic_distance")
    private Double ultrasonicDistance;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(length = 20)
    private String status;

    @Builder.Default
    private Double speed = 0.0;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
