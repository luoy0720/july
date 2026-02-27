package com.example.collector.controller;

import com.example.collector.dto.TaskRequest;
import com.example.collector.model.CollectTask;
import com.example.collector.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<CollectTask> list() {
        return taskService.list();
    }

    @PostMapping
    public CollectTask create(@Valid @RequestBody TaskRequest request) {
        return taskService.create(request);
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<Void> execute(@PathVariable Long id) {
        taskService.executeNow(id);
        return ResponseEntity.ok().build();
    }
}
