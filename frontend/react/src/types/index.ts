// ─── Auth ──────────────────────────────────────────────────────────────────────

export type Role = 'USER' | 'ADMIN'

export interface User {
  id: number
  username: string
  email: string
  fullName: string
  role: Role
  avatarKey: string | null
  bio: string | null
  rating: number
  solvedCount: number
  streak: number
  createdAt: string
}

export interface UserProfileResponse {
  user: User
  contestsJoined: number
  recentSubmissions: Submission[]
  activity: Record<string, number>
}

// ─── Problems ─────────────────────────────────────────────────────────────────

export type Difficulty = 'EASY' | 'MEDIUM' | 'HARD'

export interface ProblemSummary {
  id: number
  slug: string
  title: string
  difficulty: Difficulty
  topics: string[]
  acceptanceRate: number
  isPublic?: boolean
  solved?: boolean
}

export interface ProblemDetail extends ProblemSummary {
  description: string
  constraintsText: string | null
  starterCode: string
  starterCodePython: string | null
  supportedLanguages?: string[]
}

export interface TestCase {
  id: number
  problemId: number
  inputData: string
  expectedOutput: string
}

export interface ProblemHint {
  id: number
  problemId: number
  hintLevel: number
  hintText: string
}

// ─── Submissions ──────────────────────────────────────────────────────────────

export type Language = 'JAVA_17' | 'PYTHON_3'

export type SubmissionStatus =
  | 'QUEUED'
  | 'COMPILING'
  | 'RUNNING'
  | 'ACCEPTED'
  | 'WRONG_ANSWER'
  | 'COMPILATION_ERROR'
  | 'RUNTIME_ERROR'
  | 'TIME_LIMIT_EXCEEDED'
  | 'MEMORY_LIMIT_EXCEEDED'
  | 'SYSTEM_ERROR'

export interface Submission {
  id: number
  problemId: number
  problemTitle: string
  userId: number
  username: string
  contestId: number | null
  language: Language
  status: SubmissionStatus
  runtimeMs: number | null
  submittedAt: string
  compilerOutput: string | null
  runtimeErrorOutput: string | null
  systemErrorMessage: string | null
}

export interface SubmissionRequest {
  problemId: number
  contestId?: number | null
  language: Language
  code: string
}

// ─── Auth DTOs ────────────────────────────────────────────────────────────────

export interface LoginRequest {
  identity: string
  password: string
}

export interface RegisterRequest {
  fullName: string
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  user: User
}

// ─── Contests ─────────────────────────────────────────────────────────────────

export type ContestStatus = 'UPCOMING' | 'LIVE' | 'ENDED'

export interface Contest {
  id: number
  title: string
  description: string
  status: ContestStatus
  startsAt: string
  endsAt: string
  rated: boolean
}

export interface ContestProblem {
  id: number
  slug: string
  title: string
  difficulty: Difficulty
  points: number
}

export interface ContestLeaderboardEntry {
  rank: number
  user: {
    id: number
    username: string
    fullName: string
    avatarKey: string | null
    rating: number
    solvedCount: number
    streak: number
  }
  score: number
}

// ─── Leaderboard ──────────────────────────────────────────────────────────────

export interface LeaderboardEntry {
  rank: number
  user: {
    id: number
    username: string
    fullName: string
    avatarKey: string | null
    rating: number
    solvedCount: number
    streak: number
  }
}

// ─── Discussions ──────────────────────────────────────────────────────────────

export interface DiscussionSummary {
  id: number
  title: string
  body: string
  authorId: number
  authorUsername: string
  authorAvatarKey: string | null
  replyCount: number
  createdAt: string
}

export interface Reply {
  id: number
  authorId: number
  authorUsername: string
  authorAvatarKey: string | null
  body: string
  createdAt: string
}

export interface DiscussionDetail extends DiscussionSummary {
  replies: Reply[]
}

// ─── Admin ────────────────────────────────────────────────────────────────────

export interface AdminDashboard {
  users: number
  problems: number
  submissions: number
  discussions: number
  contests: number
}

export interface AdminUser {
  id: number
  username: string
  email: string
  fullName: string
  role: Role
  createdAt: string
}

// ─── API Errors ───────────────────────────────────────────────────────────────

export interface ApiErrorBody {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  fields?: Record<string, string>
}
