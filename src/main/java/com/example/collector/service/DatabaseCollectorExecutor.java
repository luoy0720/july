package com.example.collector.service;

import com.example.collector.model.CollectTask;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCollectorExecutor implements CollectorExecutor {

    @Override
    public boolean supports(String sourceType) {
        return "DATABASE".equals(sourceType);
    }

    @Override
    public String execute(CollectTask task) {
        return "执行数据库采集成功，配置=" + task.getConfigJson();
    }
}
