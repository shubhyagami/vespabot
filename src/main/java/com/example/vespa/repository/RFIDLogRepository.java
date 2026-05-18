package com.example.vespa.repository;

import com.example.vespa.entity.RFIDLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RFIDLogRepository extends JpaRepository<RFIDLog, Long> {
    List<RFIDLog> findByRobotIdOrderByRecordedAtDesc(String robotId);
    List<RFIDLog> findTop50ByOrderByRecordedAtDesc();
    List<RFIDLog> findByRfidTag(String rfidTag);
}
