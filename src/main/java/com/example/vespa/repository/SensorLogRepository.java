package com.example.vespa.repository;

import com.example.vespa.entity.SensorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorLogRepository extends JpaRepository<SensorLog, Long> {
    List<SensorLog> findByRobotIdOrderByRecordedAtDesc(String robotId);
    List<SensorLog> findByRobotIdAndRecordedAtAfterOrderByRecordedAtAsc(String robotId, LocalDateTime after);
    List<SensorLog> findByRecordedAtAfterOrderByRecordedAtAsc(LocalDateTime after);
    List<SensorLog> findTop50ByOrderByRecordedAtDesc();
}
