package com.example.dynamicscheduler.service;

import com.example.dynamicscheduler.dto.TaskRequest;
import com.example.dynamicscheduler.dto.TaskResponse;
import com.example.dynamicscheduler.entity.CollectTask;
import com.example.dynamicscheduler.repository.CollectTaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

    private final CollectTaskRepository repository;
    private final ObjectMapper objectMapper;
    private final TaskScheduler taskScheduler;
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    public TaskService(CollectTaskRepository repository, ObjectMapper objectMapper, TaskScheduler taskScheduler) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.taskScheduler = taskScheduler;
    }

    public void initializeSchedules() {
        repository.findByEnabledTrue().forEach(this::scheduleTask);
    }

    public List<TaskResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public TaskResponse get(Long id) {
        return toResponse(findById(id));
    }

    public TaskResponse create(TaskRequest request) {
        validateCron(request.getCronExpression());
        CollectTask task = new CollectTask();
        applyRequest(task, request);
        CollectTask saved = repository.save(task);
        refreshSchedule(saved);
        return toResponse(saved);
    }

    public TaskResponse update(Long id, TaskRequest request) {
        validateCron(request.getCronExpression());
        CollectTask task = findById(id);
        applyRequest(task, request);
        CollectTask saved = repository.save(task);
        refreshSchedule(saved);
        return toResponse(saved);
    }

    public void delete(Long id) {
        cancelSchedule(id);
        repository.delete(findById(id));
    }

    public TaskResponse executeNow(Long id) {
        CollectTask task = findById(id);
        executeTask(task);
        return toResponse(repository.save(task));
    }

    public TaskResponse toggle(Long id, boolean enabled) {
        CollectTask task = findById(id);
        task.setEnabled(enabled);
        CollectTask saved = repository.save(task);
        refreshSchedule(saved);
        return toResponse(saved);
    }

    private CollectTask findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "任务不存在"));
    }

    private void applyRequest(CollectTask task, TaskRequest request) {
        task.setName(request.getName());
        task.setCronExpression(request.getCronExpression());
        task.setScriptContent(request.getScriptContent());
        task.setEnabled(request.getEnabled());
        try {
            task.setParamsJson(objectMapper.writeValueAsString(request.getParams()));
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "参数JSON序列化失败");
        }
    }

    private void validateCron(String cronExpression) {
        try {
            new CronTrigger(cronExpression);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cron表达式非法");
        }
    }

    private void refreshSchedule(CollectTask task) {
        cancelSchedule(task.getId());
        if (Boolean.TRUE.equals(task.getEnabled())) {
            scheduleTask(task);
        }
    }

    private void scheduleTask(CollectTask task) {
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> executeTask(task),
                new CronTrigger(task.getCronExpression())
        );
        futures.put(task.getId(), future);
    }

    private void cancelSchedule(Long taskId) {
        ScheduledFuture<?> future = futures.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }

    private void executeTask(CollectTask task) {
        try {
            Map<String, Object> params = objectMapper.readValue(task.getParamsJson(), new TypeReference<>() {});
            Binding binding = new Binding();
            binding.setVariable("params", params);
            binding.setVariable("taskId", task.getId());
            binding.setVariable("taskName", task.getName());
            GroovyShell shell = new GroovyShell(binding);
            Object result = shell.evaluate(task.getScriptContent());
            task.setLastStatus("SUCCESS");
            task.setLastMessage(result == null ? "执行完成" : String.valueOf(result));
        } catch (Exception e) {
            task.setLastStatus("FAIL");
            task.setLastMessage(e.getMessage());
        }
        task.setLastRunTime(LocalDateTime.now());
        repository.save(task);
    }

    private TaskResponse toResponse(CollectTask task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setName(task.getName());
        response.setCronExpression(task.getCronExpression());
        response.setScriptContent(task.getScriptContent());
        response.setEnabled(task.getEnabled());
        response.setLastRunTime(task.getLastRunTime());
        response.setLastStatus(task.getLastStatus());
        response.setLastMessage(task.getLastMessage());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        try {
            response.setParams(objectMapper.readValue(task.getParamsJson(), new TypeReference<>() {}));
        } catch (JsonProcessingException e) {
            response.setParams(Map.of());
        }
        return response;
    }
}
