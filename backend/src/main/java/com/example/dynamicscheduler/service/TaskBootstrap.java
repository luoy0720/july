package com.example.dynamicscheduler.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TaskBootstrap implements ApplicationRunner {

    private final TaskService taskService;

    public TaskBootstrap(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void run(ApplicationArguments args) {
        taskService.initializeSchedules();
    }
}
