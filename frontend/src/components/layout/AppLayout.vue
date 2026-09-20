<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const router = useRouter()

async function logout() {
  auth.logout()
  await router.replace({ name: 'login' })
}
</script>

<template>
  <div class="shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">备忘录</RouterLink>
      <nav class="nav">
        <RouterLink to="/notes">笔记</RouterLink>
        <RouterLink to="/documents">文件</RouterLink>
      </nav>
      <div class="account">
        <span class="name">{{ auth.user?.nickname ?? auth.user?.username }}</span>
        <button class="btn" type="button" @click="logout">退出</button>
      </div>
    </header>
    <div class="body">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  border-bottom: 1px solid var(--border);
  background: var(--bg-soft);
}
.brand {
  font-weight: 600;
  color: var(--fg);
}
.nav { display: flex; gap: 16px; margin-left: auto; margin-right: 24px; }
.nav a { color: var(--fg-muted); }
.nav a.router-link-active { color: var(--accent); font-weight: 600; }
.account {
  display: flex;
  align-items: center;
  gap: 12px;
}
.name {
  color: var(--fg-muted);
}
.body {
  flex: 1;
  padding: 24px;
}
</style>
