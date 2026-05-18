package com.example.vespa.controller;

import com.example.vespa.dto.ApiResponse;
import com.example.vespa.dto.DeliveryTaskRequest;
import com.example.vespa.entity.DeliveryTask;
import com.example.vespa.service.DeliveryTaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class DeliveryTaskApiController {

    private final DeliveryTaskService taskService;

    public DeliveryTaskApiController(DeliveryTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryTask>>> getAllTasks() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getAllTasks()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryTask>> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryTask>> createTask(@Valid @RequestBody DeliveryTaskRequest request) {
        DeliveryTask task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created", task));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DeliveryTask>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        DeliveryTask task = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Task status updated", task));
    }

    @GetMapping("/robot/{robotId}")
    public ResponseEntity<ApiResponse<List<DeliveryTask>>> getTasksByRobot(@PathVariable String robotId) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTasksByRobot(robotId)));
    }
}
