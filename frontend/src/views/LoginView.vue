<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const username = ref('')
const password = ref('')
const error = ref('')
const submitting = ref(false)

async function submit() {
  error.value = ''
  submitting.value = true
  try {
    await auth.login({ username: username.value.trim(), password: password.value })
    const redirect = route.query.redirect
    await router.replace(typeof redirect === 'string' && redirect !== '' ? redirect : '/')
  } catch (exception) {
    error.value = exception instanceof Error ? exception.message : '登录失败，请稍后重试'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="auth">
    <h1>登录</h1>
    <p class="hint">登录后即可管理你的笔记与知识库。</p>
    <p v-if="error" class="form-error">{{ error }}</p>
    <form @submit.prevent="submit">
      <label class="field">
        用户名
        <input v-model="username" name="username" autocomplete="username" required />
      </label>
      <label class="field">
        密码
        <input v-model="password" name="password" type="password" autocomplete="current-password" required />
      </label>
      <button class="btn btn-primary" type="submit" :disabled="submitting">
        {{ submitting ? '登录中…' : '登录' }}
      </button>
    </form>
    <p class="alt">还没有账号？<RouterLink to="/register">注册</RouterLink></p>
  </main>
</template>

<style scoped>
.auth {
  max-width: 360px;
  margin: 96px auto;
  padding: 0 20px;
}
h1 {
  margin-bottom: 4px;
}
.hint {
  margin-top: 0;
  color: var(--fg-muted);
}
.alt {
  margin-top: 20px;
  color: var(--fg-muted);
}
</style>
