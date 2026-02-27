# 数据采集定时任务平台（Spring Boot + JDK17 + Quartz + MySQL + Vue + Vite）

## 功能
- 支持三类采集任务：`DATABASE`、`API`、`IOT`
- 使用 Quartz 按 Cron 表达式调度任务
- MySQL 持久化任务配置与执行状态
- 提供 REST API + Vue 管理页面

## 后端启动
```bash
mvn spring-boot:run
```

默认数据库配置：
- url: `jdbc:mysql://localhost:3306/collector`
- username/password: `root/root`

## 前端启动
```bash
cd web
npm install
npm run dev
```

## 核心接口
- `GET /api/tasks`：查询任务
- `POST /api/tasks`：创建任务
- `POST /api/tasks/{id}/execute`：立即执行

## 建表（若关闭 JPA 自动建表）
```sql
CREATE TABLE collect_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  source_type VARCHAR(32) NOT NULL,
  cron_expr VARCHAR(255) NOT NULL,
  enabled BIT NOT NULL,
  config_json TEXT,
  last_run_at DATETIME,
  last_status VARCHAR(32),
  last_message VARCHAR(1024)
);
```
