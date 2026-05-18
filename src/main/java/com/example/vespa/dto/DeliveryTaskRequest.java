package com.example.vespa.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTaskRequest {
    @NotBlank
    private String robotId;

    @NotBlank
    private String pickupLocation;

    @NotBlank
    private String dropLocation;

    private String packageId;

    private Integer priority;
}
