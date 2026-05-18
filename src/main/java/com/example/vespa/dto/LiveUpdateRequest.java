package com.example.vespa.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveUpdateRequest {
    @NotBlank
    private String robotId;

    private Integer battery;

    private Double ultrasonicDistance;

    private String rfidTag;

    private Double latitude;

    private Double longitude;

    private String status;

    private Double speed;
}
