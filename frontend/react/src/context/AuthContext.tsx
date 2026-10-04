import { auth as authApi, ApiError } from '@/services/api'
import { storage } from '@/utils/storage'
import type { AuthResponse, LoginRequest, RegisterRequest, User } from '@/types'
import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from 'react'

// ─── Types ────────────────────────────────────────────────────────────────────

interface AuthState {
  user: User | null
  isLoading: boolean
  isAuthenticated: boolean
}

interface AuthContextValue extends AuthState {
  login: (body: LoginRequest) => Promise<AuthResponse>
  register: (body: RegisterRequest) => Promise<AuthResponse>
  logout: () => void
  refreshUser: () => Promise<void>
}

// ─── Context ──────────────────────────────────────────────────────────────────

const AuthContext = createContext<AuthContextValue | null>(null)

// ─── Provider ─────────────────────────────────────────────────────────────────

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  // On mount: if a token exists, try to hydrate the user from /auth/me
  useEffect(() => {
    const token = storage.getToken()
    if (!token) {
      setIsLoading(false)
      return
    }
    authApi
      .me()
      .then(setUser)
      .catch((err: unknown) => {
        // 401 → token expired or invalid → clear it
        if (err instanceof ApiError && err.status === 401) {
          storage.clearToken()
        }
      })
      .finally(() => setIsLoading(false))
  }, [])

  const login = useCallback(async (body: LoginRequest): Promise<AuthResponse> => {
    const result = await authApi.login(body)
    storage.setToken(result.token)
    setUser(result.user)
    return result
  }, [])

  const register = useCallback(async (body: RegisterRequest): Promise<AuthResponse> => {
    const result = await authApi.register(body)
    storage.setToken(result.token)
    setUser(result.user)
    return result
  }, [])

  const logout = useCallback(() => {
    storage.clearToken()
    setUser(null)
  }, [])

  const refreshUser = useCallback(async () => {
    const fresh = await authApi.me()
    setUser(fresh)
  }, [])

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: user !== null,
        login,
        register,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

// ─── Hook ─────────────────────────────────────────────────────────────────────

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>')
  return ctx
}
