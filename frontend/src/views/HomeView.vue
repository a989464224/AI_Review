<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { request } from '../api/request'

const auth = useAuthStore()
const status = ref('正在检查 API 状态')

onMounted(async () => {
  try {
    status.value = (await request<{ status: string }>('/api/health')).status
  } catch {
    status.value = 'API 不可用'
  }
})
</script>

<template>
  <section>
    <h1>你好，{{ auth.user?.nickname ?? auth.user?.username }}</h1>
    <p class="muted">账户认证已就绪，笔记与知识库功能将在后续版本接入。</p>
    <p class="muted">API：{{ status }}</p>
  </section>
</template>

<style scoped>
h1 {
  margin-bottom: 8px;
}
.muted {
  color: var(--fg-muted);
  margin: 4px 0;
}
</style>
