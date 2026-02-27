package com.example.collector.service;

import com.example.collector.dto.TaskRequest;
import com.example.collector.model.CollectTask;
import com.example.collector.repo.CollectTaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final CollectTaskRepository repository;
    private final SchedulerService schedulerService;
    private final List<CollectorExecutor> executors;

    public TaskService(CollectTaskRepository repository, SchedulerService schedulerService, List<CollectorExecutor> executors) {
        this.repository = repository;
        this.schedulerService = schedulerService;
        this.executors = executors;
    }

    public List<CollectTask> list() {
        return repository.findAll();
    }

    @Transactional
    public CollectTask create(TaskRequest request) {
        CollectTask task = new CollectTask();
        task.setName(request.name());
        task.setSourceType(request.sourceType());
        task.setCronExpr(request.cronExpr());
        task.setEnabled(request.enabled());
        task.setConfigJson(request.configJson());
        CollectTask saved = repository.save(task);
        schedulerService.refreshSchedule(saved);
        return saved;
    }

    @Transactional
    public void executeNow(Long taskId) {
        CollectTask task = repository.findById(taskId).orElseThrow();
        CollectorExecutor executor = executors.stream()
                .filter(item -> item.supports(task.getSourceType().name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No executor for " + task.getSourceType()));

        try {
            String message = executor.execute(task);
            task.setLastStatus("SUCCESS");
            task.setLastMessage(message);
        } catch (Exception ex) {
            task.setLastStatus("FAILED");
            task.setLastMessage(ex.getMessage());
            throw ex;
        } finally {
            task.setLastRunAt(LocalDateTime.now());
            repository.save(task);
        }
    }
}
