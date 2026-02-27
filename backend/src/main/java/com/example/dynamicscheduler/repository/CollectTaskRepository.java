package com.example.dynamicscheduler.repository;

import com.example.dynamicscheduler.entity.CollectTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectTaskRepository extends JpaRepository<CollectTask, Long> {
    List<CollectTask> findByEnabledTrue();
}
