package com.example.dynamicscheduler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "collect_task")
public class CollectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String cronExpression;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String scriptContent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String paramsJson;

    @Column(nullable = false)
    private Boolean enabled = true;

    private LocalDateTime lastRunTime;

    @Column(length = 20)
    private String lastStatus;

    @Column(length = 1000)
    private String lastMessage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public String getScriptContent() { return scriptContent; }
    public void setScriptContent(String scriptContent) { this.scriptContent = scriptContent; }
    public String getParamsJson() { return paramsJson; }
    public void setParamsJson(String paramsJson) { this.paramsJson = paramsJson; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getLastRunTime() { return lastRunTime; }
    public void setLastRunTime(LocalDateTime lastRunTime) { this.lastRunTime = lastRunTime; }
    public String getLastStatus() { return lastStatus; }
    public void setLastStatus(String lastStatus) { this.lastStatus = lastStatus; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
