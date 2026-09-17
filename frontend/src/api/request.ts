export type Result<T> = { code: number; message: string; data: T }
export async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, { ...init, headers: { 'Content-Type': 'application/json', ...init?.headers } })
  const body = await response.json() as Result<T>
  if (!response.ok || body.code !== 0) throw new Error(body.message)
  return body.data
}
