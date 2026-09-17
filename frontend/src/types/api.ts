export type Result<T> = { code: number; message: string; data: T }

export type PageResult<T> = { records: T[]; total: number; page: number; size: number }
