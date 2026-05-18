package com.example.vespa.repository;

import com.example.vespa.entity.DeliveryTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {
    List<DeliveryTask> findByRobotIdOrderByCreatedAtDesc(String robotId);
    List<DeliveryTask> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);

    @Query("SELECT FUNCTION('DATE', d.createdAt) as date, " +
           "COUNT(CASE WHEN d.status = 'COMPLETED' THEN 1 END) as completed, " +
           "COUNT(CASE WHEN d.status != 'COMPLETED' THEN 1 END) as pending " +
           "FROM DeliveryTask d GROUP BY FUNCTION('DATE', d.createdAt) ORDER BY date")
    List<Object[]> getTaskStatsByDate();
}
