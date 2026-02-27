package com.example.collector.service;

import com.example.collector.job.CollectJob;
import com.example.collector.model.CollectTask;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.quartz.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    private final Scheduler scheduler;
    private final TaskService taskService;

    public SchedulerService(Scheduler scheduler, @Lazy TaskService taskService) {
        this.scheduler = scheduler;
        this.taskService = taskService;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.getContext().put("taskService", taskService);
    }

    public void scheduleAll(List<CollectTask> tasks) {
        tasks.forEach(this::refreshSchedule);
    }

    public void refreshSchedule(CollectTask task) {
        JobKey jobKey = JobKey.jobKey("collect-job-" + task.getId());
        TriggerKey triggerKey = TriggerKey.triggerKey("collect-trigger-" + task.getId());
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            if (!task.isEnabled()) {
                return;
            }

            JobDetail jobDetail = JobBuilder.newJob(CollectJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("taskId", task.getId())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpr()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("更新任务调度失败: " + task.getName(), e);
        }
    }
}
