package com.example.collector.repo;

import com.example.collector.model.CollectTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectTaskRepository extends JpaRepository<CollectTask, Long> {
    List<CollectTask> findByEnabledTrue();
}
