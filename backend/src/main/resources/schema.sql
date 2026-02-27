CREATE TABLE IF NOT EXISTS collect_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    cron_expression VARCHAR(50) NOT NULL,
    script_content TEXT NOT NULL,
    params_json TEXT NOT NULL,
    enabled BIT NOT NULL,
    last_run_time DATETIME NULL,
    last_status VARCHAR(20) NULL,
    last_message VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
