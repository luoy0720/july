package com.example.collector.job;

import com.example.collector.service.TaskService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerContext;

public class CollectJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            Long taskId = dataMap.getLong("taskId");
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            TaskService taskService = (TaskService) schedulerContext.get("taskService");
            taskService.executeNow(taskId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
