<template>
  <main class="container">
    <h1>数据采集任务中心</h1>
    <form @submit.prevent="createTask" class="form">
      <input v-model="form.name" placeholder="任务名称" required />
      <select v-model="form.sourceType">
        <option>DATABASE</option>
        <option>API</option>
        <option>IOT</option>
      </select>
      <input v-model="form.cronExpr" placeholder="Cron表达式" required />
      <input v-model="form.configJson" placeholder='{"url":"..."}' />
      <button>新增任务</button>
    </form>

    <table>
      <thead>
        <tr><th>ID</th><th>名称</th><th>来源</th><th>Cron</th><th>状态</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="item in tasks" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.sourceType }}</td>
          <td>{{ item.cronExpr }}</td>
          <td>{{ item.lastStatus || '-' }}</td>
          <td><button @click="execute(item.id)">立即执行</button></td>
        </tr>
      </tbody>
    </table>
  </main>
</template>

<script setup>
import axios from 'axios'
import { onMounted, reactive, ref } from 'vue'

const tasks = ref([])
const form = reactive({
  name: '',
  sourceType: 'DATABASE',
  cronExpr: '0 */1 * * * ?',
  configJson: '{}',
  enabled: true
})

const load = async () => {
  const { data } = await axios.get('/api/tasks')
  tasks.value = data
}

const createTask = async () => {
  await axios.post('/api/tasks', form)
  await load()
}

const execute = async (id) => {
  await axios.post(`/api/tasks/${id}/execute`)
  await load()
}

onMounted(load)
</script>

<style>
.container { max-width: 960px; margin: 2rem auto; font-family: Arial, sans-serif; }
.form { display: grid; gap: .5rem; grid-template-columns: repeat(5, 1fr); margin-bottom: 1rem; }
table { width: 100%; border-collapse: collapse; }
th, td { border: 1px solid #ddd; padding: 8px; }
</style>
