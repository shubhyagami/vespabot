package com.example.vespa.service;

import com.example.vespa.dto.DeliveryTaskRequest;
import com.example.vespa.entity.DeliveryTask;
import com.example.vespa.entity.Robot;
import com.example.vespa.exception.ResourceNotFoundException;
import com.example.vespa.repository.DeliveryTaskRepository;
import com.example.vespa.repository.RobotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DeliveryTaskService {

    private final DeliveryTaskRepository taskRepository;
    private final RobotRepository robotRepository;

    public DeliveryTaskService(DeliveryTaskRepository taskRepository, RobotRepository robotRepository) {
        this.taskRepository = taskRepository;
        this.robotRepository = robotRepository;
    }

    public List<DeliveryTask> getAllTasks() {
        return taskRepository.findAll();
    }

    public DeliveryTask getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryTask", id.toString()));
    }

    public DeliveryTask createTask(DeliveryTaskRequest request) {
        if (!robotRepository.existsById(request.getRobotId())) {
            throw new ResourceNotFoundException("Robot", request.getRobotId());
        }
        DeliveryTask task = DeliveryTask.builder()
                .taskId("TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .robotId(request.getRobotId())
                .pickupLocation(request.getPickupLocation())
                .dropLocation(request.getDropLocation())
                .packageId(request.getPackageId())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .status("PENDING")
                .build();
        DeliveryTask saved = taskRepository.save(task);

        Robot robot = robotRepository.findById(request.getRobotId()).orElse(null);
        if (robot != null) {
            robot.setCurrentDestination(request.getDropLocation());
            robotRepository.save(robot);
        }
        return saved;
    }

    public DeliveryTask updateTaskStatus(Long id, String status) {
        DeliveryTask task = getTaskById(id);
        task.setStatus(status);
        if ("COMPLETED".equalsIgnoreCase(status)) {
            task.setCompletedAt(LocalDateTime.now());
            Robot robot = robotRepository.findById(task.getRobotId()).orElse(null);
            if (robot != null) {
                robot.setTasksCompleted(robot.getTasksCompleted() + 1);
                robot.setStatus("IDLE");
                robotRepository.save(robot);
            }
        }
        return taskRepository.save(task);
    }

    public List<DeliveryTask> getTasksByRobot(String robotId) {
        return taskRepository.findByRobotIdOrderByCreatedAtDesc(robotId);
    }

    public List<DeliveryTask> getTasksByStatus(String status) {
        return taskRepository.findByStatusOrderByCreatedAtDesc(status);
    }
}
