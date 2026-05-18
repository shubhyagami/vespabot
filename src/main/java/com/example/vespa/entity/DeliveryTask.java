package com.example.vespa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String taskId;

    @Column(name = "robot_id", length = 20)
    private String robotId;

    @Column(name = "pickup_location", length = 200)
    private String pickupLocation;

    @Column(name = "drop_location", length = 200)
    private String dropLocation;

    @Column(length = 50)
    private String status;

    @Column(length = 100)
    private String packageId;

    @Builder.Default
    private Integer priority = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
