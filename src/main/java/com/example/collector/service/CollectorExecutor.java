package com.example.collector.service;

import com.example.collector.model.CollectTask;

public interface CollectorExecutor {
    boolean supports(String sourceType);

    String execute(CollectTask task);
}
