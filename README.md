# 动态 Groovy 定时采集任务平台

本项目包含两个子模块：
- `backend`: 基于 Spring Boot + MySQL + Groovy 的动态任务调度后端
- `frontend`: 基于 Vue3 + Vite 的任务管理前端

## 核心能力
- 任务新增、删除、编辑、查看
- 在线维护脚本内容（Groovy）
- 任务参数支持 JSON 动态配置
- 支持 Cron 动态调度
- 支持任务启停与“立即执行”
- 任务执行结果落库（最近执行时间、状态、消息）

## 后端启动
1. 创建 MySQL 数据库：`dynamic_scheduler`
2. 修改 `backend/src/main/resources/application.yml` 中数据库连接
3. 启动：
   ```bash
   cd backend
   mvn spring-boot:run
   ```

## 前端启动
```bash
cd frontend
npm install
npm run dev
```

默认访问：http://localhost:5173
