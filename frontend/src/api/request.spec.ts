import { afterEach, expect, it, vi } from 'vitest'
import { request } from './request'
afterEach(() => vi.unstubAllGlobals())
it('unwraps a successful Result response', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(JSON.stringify({ code: 0, message: 'OK', data: { status: 'UP' } }))))
  await expect(request<{ status: string }>('/api/health')).resolves.toEqual({ status: 'UP' })
})
