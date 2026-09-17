export type User = {
  id: number
  username: string
  nickname: string | null
  createdAt: string
}

export type LoginPayload = { username: string; password: string }

export type RegisterPayload = { username: string; password: string; nickname?: string }

export type LoginResponse = { token: string; user: User }
