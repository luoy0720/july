package com.example.collector.config;

import com.example.collector.repo.CollectTaskRepository;
import com.example.collector.service.SchedulerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupScheduler implements CommandLineRunner {

    private final CollectTaskRepository repository;
    private final SchedulerService schedulerService;

    public StartupScheduler(CollectTaskRepository repository, SchedulerService schedulerService) {
        this.repository = repository;
        this.schedulerService = schedulerService;
    }

    @Override
    public void run(String... args) {
        schedulerService.scheduleAll(repository.findByEnabledTrue());
    }
}
