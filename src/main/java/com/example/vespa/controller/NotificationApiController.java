package com.example.vespa.controller;

import com.example.vespa.dto.ApiResponse;
import com.example.vespa.entity.Notification;
import com.example.vespa.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    public NotificationApiController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications() {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getRecentNotifications()));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnread() {
        Map<String, Object> data = Map.of(
                "count", notificationService.getUnreadCount(),
                "notifications", notificationService.getUnreadNotifications()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("All marked as read", null));
    }
}
