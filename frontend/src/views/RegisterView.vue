<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const username = ref('')
const nickname = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const submitting = ref(false)

async function submit() {
  error.value = ''
  if (password.value !== confirmPassword.value) {
    error.value = '两次输入的密码不一致'
    return
  }
  submitting.value = true
  try {
    await auth.register({
      username: username.value.trim(),
      password: password.value,
      nickname: nickname.value.trim() === '' ? undefined : nickname.value.trim(),
    })
    await router.replace('/')
  } catch (exception) {
    error.value = exception instanceof Error ? exception.message : '注册失败，请稍后重试'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="auth">
    <h1>注册</h1>
    <p class="hint">用户名 3-32 位，仅限字母、数字和下划线。</p>
    <p v-if="error" class="form-error">{{ error }}</p>
    <form @submit.prevent="submit">
      <label class="field">
        用户名
        <input v-model="username" name="username" autocomplete="username" required />
      </label>
      <label class="field">
        昵称（可选）
        <input v-model="nickname" name="nickname" autocomplete="nickname" />
      </label>
      <label class="field">
        密码
        <input v-model="password" name="password" type="password" autocomplete="new-password" required />
      </label>
      <label class="field">
        确认密码
        <input v-model="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password" required />
      </label>
      <button class="btn btn-primary" type="submit" :disabled="submitting">
        {{ submitting ? '注册中…' : '注册' }}
      </button>
    </form>
    <p class="alt">已有账号？<RouterLink to="/login">登录</RouterLink></p>
  </main>
</template>

<style scoped>
.auth {
  max-width: 360px;
  margin: 72px auto;
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
