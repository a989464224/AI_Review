import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '../api/auth'
import { getToken, setToken } from '../api/request'
import type { LoginPayload, RegisterPayload, User } from '../types/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const isAuthenticated = computed(() => user.value !== null)

  async function login(payload: LoginPayload): Promise<void> {
    const response = await authApi.login(payload)
    setToken(response.token)
    user.value = response.user
  }

  async function register(payload: RegisterPayload): Promise<void> {
    const response = await authApi.register(payload)
    setToken(response.token)
    user.value = response.user
  }

  /** 刷新页面后用本地令牌恢复登录态；令牌失效则静默清理。 */
  async function restoreSession(): Promise<void> {
    if (user.value !== null || getToken() === null) return
    try {
      user.value = await authApi.me()
    } catch {
      setToken(null)
      user.value = null
    }
  }

  function logout(): void {
    setToken(null)
    user.value = null
  }

  return { user, isAuthenticated, login, register, restoreSession, logout }
})
