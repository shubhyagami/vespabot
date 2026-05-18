CREATE DATABASE IF NOT EXISTS vespa_robots;
USE vespa_robots;

CREATE TABLE IF NOT EXISTS robots (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'IDLE',
    battery INT DEFAULT 100,
    ultrasonic_distance DOUBLE DEFAULT 0,
    rfid_tag VARCHAR(50),
    speed DOUBLE DEFAULT 0,
    current_destination VARCHAR(100),
    online BOOLEAN DEFAULT TRUE,
    latitude DOUBLE DEFAULT 8.52,
    longitude DOUBLE DEFAULT 76.934,
    last_updated DATETIME,
    total_distance DOUBLE DEFAULT 0,
    tasks_completed INT DEFAULT 0,
    uptime_seconds BIGINT DEFAULT 0,
    is_real BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS robot_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    robot_id VARCHAR(20) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (robot_id) REFERENCES robots(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sensor_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    robot_id VARCHAR(20) NOT NULL,
    ultrasonic_distance DOUBLE,
    battery_level INT,
    status VARCHAR(20),
    speed DOUBLE DEFAULT 0,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (robot_id) REFERENCES robots(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rfid_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    robot_id VARCHAR(20) NOT NULL,
    rfid_tag VARCHAR(50),
    action VARCHAR(20) DEFAULT 'SCAN',
    description VARCHAR(100),
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (robot_id) REFERENCES robots(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS delivery_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(50),
    robot_id VARCHAR(20),
    pickup_location VARCHAR(200),
    drop_location VARCHAR(200),
    status VARCHAR(50),
    package_id VARCHAR(100),
    priority INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,
    FOREIGN KEY (robot_id) REFERENCES robots(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50),
    message VARCHAR(200),
    robot_id VARCHAR(20),
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_robot_locations_robot ON robot_locations(robot_id, recorded_at);
CREATE INDEX idx_sensor_logs_robot ON sensor_logs(robot_id, recorded_at);
CREATE INDEX idx_rfid_logs_robot ON rfid_logs(robot_id, recorded_at);
CREATE INDEX idx_delivery_tasks_robot ON delivery_tasks(robot_id);
CREATE INDEX idx_notifications_read ON notifications(is_read, created_at);
