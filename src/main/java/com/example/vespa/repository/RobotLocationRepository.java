package com.example.vespa.repository;

import com.example.vespa.entity.RobotLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RobotLocationRepository extends JpaRepository<RobotLocation, Long> {
    List<RobotLocation> findByRobotIdOrderByRecordedAtAsc(String robotId);
    List<RobotLocation> findByRobotIdAndRecordedAtAfterOrderByRecordedAtAsc(String robotId, LocalDateTime after);
    List<RobotLocation> findByRecordedAtAfterOrderByRecordedAtAsc(LocalDateTime after);
    void deleteByRecordedAtBefore(LocalDateTime before);
}
