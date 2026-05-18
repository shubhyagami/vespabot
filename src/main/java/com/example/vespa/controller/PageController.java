package com.example.vespa.controller;

import com.example.vespa.dto.RobotDTO;
import com.example.vespa.service.RobotService;
import com.example.vespa.service.NotificationService;
import com.example.vespa.service.DeliveryTaskService;
import com.example.vespa.repository.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {

    private final RobotService robotService;
    private final NotificationService notificationService;
    private final DeliveryTaskService taskService;
    private final NotificationRepository notificationRepository;

    public PageController(RobotService robotService,
                          NotificationService notificationService,
                          DeliveryTaskService taskService,
                          NotificationRepository notificationRepository) {
        this.robotService = robotService;
        this.notificationService = notificationService;
        this.taskService = taskService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<RobotDTO> robots = robotService.getAllRobots();
        model.addAttribute("robots", robots);

        long totalTasks = robots.stream().mapToLong(r -> r.getTasksCompleted() != null ? r.getTasksCompleted() : 0).sum();
        double avgBattery = robots.stream().mapToInt(RobotDTO::getBattery).average().orElse(0);
        long activeCount = robots.stream().filter(r -> "MOVING".equals(r.getStatus())).count();

        model.addAttribute("totalRobots", robots.size());
        model.addAttribute("activeRobots", activeCount);
        model.addAttribute("avgBattery", Math.round(avgBattery));
        model.addAttribute("totalTasks", totalTasks);

        model.addAttribute("notifications", notificationRepository.findTop50ByOrderByCreatedAtDesc());
        model.addAttribute("unreadNotifs", notificationRepository.countByIsReadFalse());

        return "dashboard";
    }
}
