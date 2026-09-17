<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { request } from '../api/request'
const status = ref('正在检查 API 状态')
onMounted(async () => { try { status.value = (await request<{ status: string }>('/api/health')).status } catch { status.value = 'API 不可用' } })
</script>
<template><main><h1>备忘录</h1><p>基础框架已启动</p><p>API：{{ status }}</p></main></template>
<style scoped>main{max-width:680px;margin:72px auto;font-family:system-ui,sans-serif}h1{margin-bottom:8px}</style>
