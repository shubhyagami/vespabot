package com.example.vespa.repository;

import com.example.vespa.entity.Robot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RobotRepository extends JpaRepository<Robot, String> {
    List<Robot> findByStatus(String status);
    List<Robot> findByOnlineTrue();
    long countByStatus(String status);

    @Query("SELECT COALESCE(AVG(r.battery), 0) FROM Robot r")
    double getAverageBattery();

    @Query("SELECT COALESCE(SUM(r.tasksCompleted), 0) FROM Robot r")
    long getTotalTasksCompleted();

    @Query("SELECT COALESCE(SUM(r.totalDistance), 0) FROM Robot r")
    double getTotalDistanceTraveled();
}
