import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from './auth'
import { getToken, setToken } from '../api/request'

const user = { id: 1, username: 'alice', nickname: '爱丽丝', createdAt: '2026-09-17 10:00:00' }

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })
}

beforeEach(() => setActivePinia(createPinia()))

afterEach(() => {
  vi.unstubAllGlobals()
  setToken(null)
})

describe('auth store', () => {
  it('keeps the token and user after login', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
      jsonResponse({ code: 0, message: 'OK', data: { token: 'token-1', user } }),
    ))

    const auth = useAuthStore()
    await auth.login({ username: 'alice', password: 'secret123' })

    expect(auth.isAuthenticated).toBe(true)
    expect(auth.user?.nickname).toBe('爱丽丝')
    expect(getToken()).toBe('token-1')
  })

  it('clears everything on logout', async () => {
    const auth = useAuthStore()
    setToken('token-1')

    auth.logout()

    expect(auth.isAuthenticated).toBe(false)
    expect(getToken()).toBeNull()
  })

  it('restores the session from a stored token', async () => {
    setToken('token-1')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse({ code: 0, message: 'OK', data: user })))

    const auth = useAuthStore()
    await auth.restoreSession()

    expect(auth.user?.username).toBe('alice')
  })

  it('drops a stored token that the server rejects', async () => {
    setToken('stale-token')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
      jsonResponse({ code: 401, message: '未登录或登录已过期', data: null }, 401),
    ))

    const auth = useAuthStore()
    await auth.restoreSession()

    expect(auth.isAuthenticated).toBe(false)
    expect(getToken()).toBeNull()
  })
})
