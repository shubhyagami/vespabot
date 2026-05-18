package com.example.vespa.config;

import com.example.vespa.entity.*;
import com.example.vespa.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final RobotRepository robotRepository;
    private final DeliveryTaskRepository taskRepository;
    private final NotificationRepository notificationRepository;
    private final RFIDLogRepository rfidLogRepository;

    public DataLoader(RobotRepository robotRepository,
                      DeliveryTaskRepository taskRepository,
                      NotificationRepository notificationRepository,
                      RFIDLogRepository rfidLogRepository) {
        this.robotRepository = robotRepository;
        this.taskRepository = taskRepository;
        this.notificationRepository = notificationRepository;
        this.rfidLogRepository = rfidLogRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (robotRepository.count() > 0) return;

        List<Robot> robots = Arrays.asList(
            Robot.builder().id("BOT-01").name("Atlas").status("MOVING").battery(85).ultrasonicDistance(45.0).rfidTag("PKG-301").speed(1.2).currentDestination("Warehouse A - Dock 1").online(true).latitude(8.5200).longitude(76.9300).totalDistance(1250.0).tasksCompleted(42).uptimeSeconds(36000L).isReal(false).build(),
            Robot.builder().id("BOT-02").name("Gemini").status("CHARGING").battery(15).ultrasonicDistance(120.0).rfidTag(null).speed(0.0).currentDestination("Charging Station").online(true).latitude(8.5220).longitude(76.9340).totalDistance(890.0).tasksCompleted(28).uptimeSeconds(28800L).isReal(false).build(),
            Robot.builder().id("BOT-03").name("Nova").status("IDLE").battery(100).ultrasonicDistance(200.0).rfidTag("PKG-102").speed(0.0).currentDestination("Inspection Zone H").online(true).latitude(8.5180).longitude(76.9360).totalDistance(2100.0).tasksCompleted(67).uptimeSeconds(72000L).isReal(false).build(),
            Robot.builder().id("BOT-04").name("Orion").status("MOVING").battery(62).ultrasonicDistance(30.0).rfidTag("PKG-201").speed(1.5).currentDestination("Shipping Bay D").online(true).latitude(8.5240).longitude(76.9320).totalDistance(1560.0).tasksCompleted(51).uptimeSeconds(50400L).isReal(false).build(),
            Robot.builder().id("BOT-05").name("Phoenix").status("OBSTACLE").battery(45).ultrasonicDistance(5.0).rfidTag(null).speed(0.0).currentDestination("Maintenance Bay").online(true).latitude(8.5260).longitude(76.9380).totalDistance(780.0).tasksCompleted(19).uptimeSeconds(21600L).isReal(false).build(),
            Robot.builder().id("BOT-06").name("Sentinel").status("IDLE").battery(92).ultrasonicDistance(150.0).rfidTag(null).speed(0.0).currentDestination(null).online(true).latitude(8.5160).longitude(76.9340).totalDistance(3200.0).tasksCompleted(95).uptimeSeconds(86400L).isReal(true).build()
        );
        robotRepository.saveAll(robots);

        List<DeliveryTask> tasks = Arrays.asList(
            DeliveryTask.builder().taskId("TASK-A1B2C3").robotId("BOT-01").pickupLocation("Warehouse A").dropLocation("Dock 1").status("IN_PROGRESS").packageId("PKG-301").priority(1).build(),
            DeliveryTask.builder().taskId("TASK-D4E5F6").robotId("BOT-03").pickupLocation("Storage Rack E12").dropLocation("Packaging Zone C").status("PENDING").packageId("PKG-102").priority(2).build(),
            DeliveryTask.builder().taskId("TASK-G7H8I9").robotId("BOT-04").pickupLocation("Warehouse B").dropLocation("Shipping Bay D").status("COMPLETED").packageId("PKG-201").priority(0).completedAt(LocalDateTime.now().minusMinutes(30)).build(),
            DeliveryTask.builder().taskId("TASK-J0K1L2").robotId("BOT-06").pickupLocation("Loading Bay G").dropLocation("Inspection Zone H").status("PENDING").packageId("PKG-401").priority(3).build()
        );
        taskRepository.saveAll(tasks);

        List<Notification> notifs = Arrays.asList(
            Notification.builder().type("INFO").message("System initialized with 6 robots").robotId("SYSTEM").isRead(false).build(),
            Notification.builder().type("TASK_COMPLETE").message("Orion completed delivery to Shipping Bay D").robotId("BOT-04").isRead(false).build(),
            Notification.builder().type("BATTERY_LOW").message("Gemini battery low: 15%").robotId("BOT-02").isRead(false).build(),
            Notification.builder().type("OBSTACLE").message("Phoenix detected obstacle at 5cm").robotId("BOT-05").isRead(false).build()
        );
        notificationRepository.saveAll(notifs);

        List<RFIDLog> rfidLogs = Arrays.asList(
            RFIDLog.builder().robotId("BOT-01").rfidTag("PKG-301").action("SCAN").description("Package PKG-301 loaded").build(),
            RFIDLog.builder().robotId("BOT-03").rfidTag("PKG-102").action("SCAN").description("Package PKG-102 scanned").build(),
            RFIDLog.builder().robotId("BOT-04").rfidTag("PKG-201").action("SCAN").description("Package PKG-201 verified").build()
        );
        rfidLogRepository.saveAll(rfidLogs);
    }
}
