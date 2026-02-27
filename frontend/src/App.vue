<template>
  <div class="container">
    <h2>Groovy 动态采集任务管理</h2>
    <button @click="startCreate">新增任务</button>
    <button class="secondary" @click="loadTasks">刷新</button>

    <div v-if="editing" style="margin: 20px 0; border: 1px solid #ddd; padding: 16px; border-radius: 10px">
      <h3>{{ form.id ? '编辑任务' : '新增任务' }}</h3>
      <div class="form-grid">
        <div>
          <label>任务名称</label>
          <input v-model="form.name" />
          <label>Cron表达式</label>
          <input v-model="form.cronExpression" placeholder="0 */5 * * * *" />
          <label>是否启用</label>
          <input type="checkbox" v-model="form.enabled" />
        </div>
        <div>
          <label>参数(JSON)</label>
          <textarea v-model="paramsText"></textarea>
        </div>
      </div>
      <label>Groovy脚本</label>
      <textarea v-model="form.scriptContent" style="min-height: 180px"></textarea>
      <button @click="saveTask">保存</button>
      <button class="secondary" @click="editing = false">取消</button>
    </div>

    <table>
      <thead>
      <tr>
        <th>ID</th><th>名称</th><th>Cron</th><th>启用</th><th>最近状态</th><th>最近消息</th><th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="task in tasks" :key="task.id">
        <td>{{ task.id }}</td>
        <td>{{ task.name }}</td>
        <td>{{ task.cronExpression }}</td>
        <td>{{ task.enabled ? '是' : '否' }}</td>
        <td>
          <span class="badge" :class="statusClass(task.lastStatus)">{{ task.lastStatus || 'NONE' }}</span>
        </td>
        <td>{{ task.lastMessage }}</td>
        <td>
          <button @click="editTask(task)">编辑</button>
          <button @click="execute(task.id)">立即执行</button>
          <button class="secondary" @click="toggle(task)">{{ task.enabled ? '停用' : '启用' }}</button>
          <button class="danger" @click="remove(task.id)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios'

const tasks = ref([])
const editing = ref(false)
const paramsText = ref('{\n  "url": "https://api.example.com"\n}')
const form = reactive({
  id: null,
  name: '',
  cronExpression: '0 */5 * * * *',
  scriptContent: 'return "采集完成: ${params.url}"',
  enabled: true
})

const loadTasks = async () => {
  const { data } = await axios.get('/api/tasks')
  tasks.value = data
}

const startCreate = () => {
  form.id = null
  form.name = ''
  form.cronExpression = '0 */5 * * * *'
  form.scriptContent = 'return "采集完成: ${params.url}"'
  form.enabled = true
  paramsText.value = '{\n  "url": "https://api.example.com"\n}'
  editing.value = true
}

const editTask = (task) => {
  form.id = task.id
  form.name = task.name
  form.cronExpression = task.cronExpression
  form.scriptContent = task.scriptContent
  form.enabled = task.enabled
  paramsText.value = JSON.stringify(task.params || {}, null, 2)
  editing.value = true
}

const saveTask = async () => {
  const payload = {
    name: form.name,
    cronExpression: form.cronExpression,
    scriptContent: form.scriptContent,
    enabled: form.enabled,
    params: JSON.parse(paramsText.value || '{}')
  }
  if (form.id) {
    await axios.put(`/api/tasks/${form.id}`, payload)
  } else {
    await axios.post('/api/tasks', payload)
  }
  editing.value = false
  await loadTasks()
}

const remove = async (id) => {
  await axios.delete(`/api/tasks/${id}`)
  await loadTasks()
}

const execute = async (id) => {
  await axios.post(`/api/tasks/${id}/execute`)
  await loadTasks()
}

const toggle = async (task) => {
  await axios.post(`/api/tasks/${task.id}/enable`, null, { params: { enabled: !task.enabled } })
  await loadTasks()
}

const statusClass = (status) => {
  if (status === 'SUCCESS') return 'success'
  if (status === 'FAIL') return 'fail'
  return 'none'
}

loadTasks()
</script>
