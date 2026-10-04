import { RequireAdmin, RequireAuth } from './guards'
import AboutPage from '@/pages/About'
import AdminPage from '@/pages/Admin'
import ContestPage from '@/pages/Contest'
import ContestsPage from '@/pages/Contests'
import DiscussPage from '@/pages/Discuss'
import HomePage from '@/pages/Home'
import LeaderboardPage from '@/pages/Leaderboard'
import LoginPage from '@/pages/Login'
import NotFoundPage from '@/pages/NotFound'
import ProblemDetailPage from '@/pages/ProblemDetail'
import ProblemsPage from '@/pages/Problems'
import ProfilePage from '@/pages/Profile'
import RegisterPage from '@/pages/Register'
import SolvePage from '@/pages/Solve'
import { Route, Routes } from 'react-router-dom'
import AppShell from '@/layouts/AppShell'

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppShell />}>
        {/* Public routes */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/problems" element={<ProblemsPage />} />
        <Route path="/problems/:id" element={<ProblemDetailPage />} />
        <Route path="/contests" element={<ContestsPage />} />
        <Route path="/contests/:id" element={<ContestPage />} />
        <Route path="/leaderboard" element={<LeaderboardPage />} />
        <Route path="/discuss" element={<DiscussPage />} />
        <Route path="/about" element={<AboutPage />} />

        {/* Authenticated routes */}
        <Route
          path="/solve/:slug"
          element={
            <RequireAuth>
              <SolvePage />
            </RequireAuth>
          }
        />
        <Route
          path="/profile"
          element={
            <RequireAuth>
              <ProfilePage />
            </RequireAuth>
          }
        />

        {/* Admin-only routes */}
        <Route
          path="/admin"
          element={
            <RequireAdmin>
              <AdminPage />
            </RequireAdmin>
          }
        />

        {/* 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}
