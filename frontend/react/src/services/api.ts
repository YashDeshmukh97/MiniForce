import type {
  AdminDashboard,
  AdminUser,
  AuthResponse,
  Contest,
  ContestLeaderboardEntry,
  ContestProblem,
  Difficulty,
  DiscussionDetail,
  DiscussionSummary,
  LeaderboardEntry,
  LoginRequest,
  ProblemDetail,
  ProblemSummary,
  RegisterRequest,
  Reply,
  Role,
  Submission,
  SubmissionRequest,
  TestCase,
  User,
  UserProfileResponse,
} from '@/types'
import { storage } from '@/utils/storage'

// ─── Error class ──────────────────────────────────────────────────────────────

export class ApiError extends Error {
  public readonly status: number
  public readonly fields?: Record<string, string>

  constructor(
    message: string,
    status: number,
    fields?: Record<string, string>,
  ) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fields = fields
  }
}

// ─── Base fetch ───────────────────────────────────────────────────────────────

const BASE = import.meta.env.VITE_API_BASE ?? '/api'

interface RequestOptions {
  method?: string
  body?: unknown
  headers?: Record<string, string>
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {
    Accept: 'application/json',
    ...(options.body !== undefined ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers ?? {}),
  }

  const token = storage.getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`

  let response: Response
  try {
    response = await fetch(`${BASE}${path}`, {
      method: options.method ?? 'GET',
      headers,
      body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    })
  } catch {
    throw new ApiError(
      'Cannot reach MiniForce API. Start the backend and check your network connection.',
      0,
    )
  }

  if (response.status === 204) return null as T

  let data: Record<string, unknown> = {}
  try {
    data = await response.json()
  } catch {
    // ignore parse failure
  }

  if (!response.ok) {
    throw new ApiError(
      (data['message'] as string) ?? `Request failed (${response.status})`,
      response.status,
      data['fields'] as Record<string, string> | undefined,
    )
  }

  return data as T
}

function buildQuery(params: Record<string, string | number | boolean | undefined | null>): string {
  const p = new URLSearchParams()
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== null && v !== '') p.set(k, String(v))
  }
  const text = p.toString()
  return text ? `?${text}` : ''
}

// ─── Auth ─────────────────────────────────────────────────────────────────────

export const auth = {
  register: (body: RegisterRequest) =>
    request<AuthResponse>('/auth/register', { method: 'POST', body }),

  login: (body: LoginRequest) =>
    request<AuthResponse>('/auth/login', { method: 'POST', body }),

  me: () => request<User>('/auth/me'),
}

// ─── Problems ─────────────────────────────────────────────────────────────────

export const problems = {
  list: (filters: {
    search?: string
    difficulty?: Difficulty
    topic?: string
    page?: number
  } = {}) => request<ProblemSummary[]>(`/problems${buildQuery(filters)}`),

  get: (id: number) => request<ProblemDetail>(`/problems/${id}`),

  getBySlug: (slug: string) =>
    request<ProblemDetail>(`/problems/slug/${encodeURIComponent(slug)}`),

  create: (body: Partial<ProblemDetail>) =>
    request<ProblemDetail>('/problems', { method: 'POST', body }),

  update: (id: number, body: Partial<ProblemDetail>) =>
    request<ProblemDetail>(`/problems/${id}`, { method: 'PUT', body }),

  remove: (id: number) => request<null>(`/problems/${id}`, { method: 'DELETE' }),

  tests: (id: number) => request<TestCase[]>(`/problems/${id}/test-cases`),

  addTest: (id: number, body: Pick<TestCase, 'inputData' | 'expectedOutput'>) =>
    request<TestCase>(`/problems/${id}/test-cases`, { method: 'POST', body }),
}

// ─── Submissions ──────────────────────────────────────────────────────────────

export const submissions = {
  submit: (body: SubmissionRequest) =>
    request<Submission>('/submissions', { method: 'POST', body }),

  submitAsync: (body: SubmissionRequest) =>
    request<Submission>('/submissions/async', { method: 'POST', body }),

  run: (body: SubmissionRequest) =>
    request<Submission>('/submissions/run', { method: 'POST', body }),

  get: (id: number) => request<Submission>(`/submissions/${id}`),

  list: () => request<Submission[]>('/submissions'),
}

// ─── Users ────────────────────────────────────────────────────────────────────

export const users = {
  profile: () => request<UserProfileResponse>('/users/me'),

  update: (body: Partial<Pick<User, 'fullName' | 'email'>>) =>
    request<User>('/users/me', { method: 'PUT', body }),

  updateAvatar: (avatarKey: string) =>
    request<User>('/users/me/avatar', { method: 'PUT', body: { avatarKey } }),

  leaderboard: () => request<LeaderboardEntry[]>('/leaderboard'),
}

// ─── Contests ─────────────────────────────────────────────────────────────────

export const contests = {
  list: () => request<Contest[]>('/contests'),

  get: (id: number) => request<Contest>(`/contests/${id}`),

  problems: (id: number) => request<ContestProblem[]>(`/contests/${id}/problems`),

  join: (id: number) => request<null>(`/contests/${id}/join`, { method: 'POST' }),

  leaderboard: (id: number) =>
    request<ContestLeaderboardEntry[]>(`/contests/${id}/leaderboard`),

  create: (body: Partial<Contest>) =>
    request<Contest>('/contests', { method: 'POST', body }),

  update: (id: number, body: Partial<Contest>) =>
    request<Contest>(`/contests/${id}`, { method: 'PUT', body }),

  remove: (id: number) => request<null>(`/contests/${id}`, { method: 'DELETE' }),
}

// ─── Discussions ──────────────────────────────────────────────────────────────

export const discussions = {
  list: () => request<DiscussionSummary[]>('/discussions'),

  get: (id: number) => request<DiscussionDetail>(`/discussions/${id}`),

  create: (body: { title: string; body: string }) =>
    request<DiscussionDetail>('/discussions', { method: 'POST', body }),

  reply: (id: number, body: { body: string }) =>
    request<Reply>(`/discussions/${id}/replies`, { method: 'POST', body }),
}

// ─── Admin ────────────────────────────────────────────────────────────────────

export const admin = {
  dashboard: () => request<AdminDashboard>('/admin/dashboard'),

  users: (search = '') =>
    request<AdminUser[]>(`/admin/users${buildQuery({ search })}`),

  setRole: (id: number, role: Role) =>
    request<AdminUser>(`/admin/users/${id}/role`, { method: 'PUT', body: { role } }),

  removeDiscussion: (id: number) =>
    request<null>(`/admin/discussions/${id}`, { method: 'DELETE' }),
}

// ─── Named export grouping (mirrors vanilla MiniForceApi shape) ───────────────

export const api = { auth, problems, submissions, users, contests, discussions, admin }
export default api
