import { useAuth } from '@/context/AuthContext'
import { Navigate, useLocation } from 'react-router-dom'
import type { ReactNode } from 'react'

/**
 * Wraps a route and redirects unauthenticated users to /login,
 * preserving the intended destination in location state (same pattern
 * as vanilla requireLogin() + ?next= query param).
 */
export function RequireAuth({ children }: { children: ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth()
  const location = useLocation()

  // While hydrating token → render nothing to avoid flash
  if (isLoading) return null

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return <>{children}</>
}

/**
 * Wraps a route and redirects non-admin users to the home page.
 */
export function RequireAdmin({ children }: { children: ReactNode }) {
  const { user, isLoading } = useAuth()
  const location = useLocation()

  if (isLoading) return null

  if (!user || user.role !== 'ADMIN') {
    return <Navigate to="/" state={{ from: location }} replace />
  }

  return <>{children}</>
}
