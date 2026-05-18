package com.example.vespa.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneDTO {
    private String zoneId;
    private String zoneName;
    private double temperature;
    private double humidity;
    private double distance;
    private double gasLevel;
    private double vibration;
    private double lightIntensity;
    private String status;
    private List<String> recentLogs;
    private boolean isReal;
}
