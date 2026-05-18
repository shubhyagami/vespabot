package com.example.vespa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rfid_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFIDLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "robot_id", nullable = false, length = 20)
    private String robotId;

    @Column(name = "rfid_tag", length = 50)
    private String rfidTag;

    @Column(length = 20)
    @Builder.Default
    private String action = "SCAN";

    @Column(length = 100)
    private String description;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
