package com.example.vespa.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastRobotUpdate(Object robotDTO) {
        messagingTemplate.convertAndSend("/topic/robots", robotDTO);
    }

    public void broadcastLocationUpdate(Object locationData) {
        messagingTemplate.convertAndSend("/topic/locations", locationData);
    }

    public void broadcastSensorUpdate(Object sensorData) {
        messagingTemplate.convertAndSend("/topic/sensors", sensorData);
    }

    public void broadcastNotification(Object notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    public void broadcastBatteryAlert(Object alert) {
        messagingTemplate.convertAndSend("/topic/alerts/battery", alert);
    }

    public void broadcastRfidScan(Object rfidData) {
        messagingTemplate.convertAndSend("/topic/rfid", rfidData);
    }

    public void broadcastObstacleWarning(Object warning) {
        messagingTemplate.convertAndSend("/topic/alerts/obstacle", warning);
    }

    public void broadcastAnalytics(Object analytics) {
        messagingTemplate.convertAndSend("/topic/analytics", analytics);
    }

    public void broadcastZoneUpdate(Object zoneData) {
        messagingTemplate.convertAndSend("/topic/zones", zoneData);
    }

    public void broadcast(String topic, Object data) {
        messagingTemplate.convertAndSend(topic, data);
    }
}
