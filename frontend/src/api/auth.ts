import { post, request } from './request'
import type { LoginPayload, LoginResponse, RegisterPayload, User } from '../types/auth'

export const authApi = {
  register: (payload: RegisterPayload) => post<LoginResponse>('/api/auth/register', payload),
  login: (payload: LoginPayload) => post<LoginResponse>('/api/auth/login', payload),
  me: () => request<User>('/api/auth/me'),
}
