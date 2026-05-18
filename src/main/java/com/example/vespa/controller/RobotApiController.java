package com.example.vespa.controller;

import com.example.vespa.dto.*;
import com.example.vespa.entity.RobotLocation;
import com.example.vespa.entity.SensorLog;
import com.example.vespa.repository.RobotLocationRepository;
import com.example.vespa.repository.SensorLogRepository;
import com.example.vespa.service.RobotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/robots")
public class RobotApiController {

    private final RobotService robotService;
    private final RobotLocationRepository locationRepository;
    private final SensorLogRepository sensorLogRepository;

    public RobotApiController(RobotService robotService,
                              RobotLocationRepository locationRepository,
                              SensorLogRepository sensorLogRepository) {
        this.robotService = robotService;
        this.locationRepository = locationRepository;
        this.sensorLogRepository = sensorLogRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RobotDTO>>> getAllRobots() {
        return ResponseEntity.ok(ApiResponse.success(robotService.getAllRobots()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RobotDTO>> getRobot(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(robotService.getRobotById(id)));
    }

    @PostMapping("/live-update")
    public ResponseEntity<ApiResponse<RobotDTO>> liveUpdate(@Valid @RequestBody LiveUpdateRequest request) {
        RobotDTO updated = robotService.processLiveUpdate(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Robot data updated", updated));
    }

    @GetMapping("/{id}/locations")
    public ResponseEntity<ApiResponse<List<RobotLocation>>> getLocations(
            @PathVariable String id,
            @RequestParam(required = false) Integer minutes) {
        List<RobotLocation> locations;
        if (minutes != null) {
            locations = locationRepository.findByRobotIdAndRecordedAtAfterOrderByRecordedAtAsc(
                    id, LocalDateTime.now().minusMinutes(minutes));
        } else {
            locations = locationRepository.findByRobotIdOrderByRecordedAtAsc(id);
        }
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    @GetMapping("/{id}/sensors")
    public ResponseEntity<ApiResponse<List<SensorLog>>> getSensorLogs(
            @PathVariable String id,
            @RequestParam(required = false) Integer minutes) {
        List<SensorLog> logs;
        if (minutes != null) {
            logs = sensorLogRepository.findByRobotIdAndRecordedAtAfterOrderByRecordedAtAsc(
                    id, LocalDateTime.now().minusMinutes(minutes));
        } else {
            logs = sensorLogRepository.findByRobotIdOrderByRecordedAtDesc(id);
        }
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
