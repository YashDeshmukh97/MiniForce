import { useAuth } from '@/context/AuthContext'
import { LogOut, Menu, Shield, User, X, Zap } from 'lucide-react'
import { useState } from 'react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'

const NAV_LINKS = [
  { to: '/problems', label: 'Problems' },
  { to: '/contests', label: 'Contests' },
  { to: '/leaderboard', label: 'Leaderboard' },
  { to: '/discuss', label: 'Discuss' },
  { to: '/about', label: 'About' },
]

export default function AppShell() {
  const { user, isAuthenticated, logout } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/')
    setMenuOpen(false)
  }

  return (
    <div className="min-h-screen flex flex-col">
      {/* ── Navbar ─────────────────────────────────────────────────────────── */}
      <header className="sticky top-0 z-50 border-b border-slate-800 bg-slate-950/90 backdrop-blur">
        <nav className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 font-bold text-xl text-white">
            <Zap className="h-5 w-5 text-indigo-400" />
            MiniForce
          </Link>

          {/* Desktop nav links */}
          <ul className="hidden md:flex items-center gap-6">
            {NAV_LINKS.map(({ to, label }) => (
              <li key={to}>
                <NavLink
                  to={to}
                  className={({ isActive }) =>
                    `text-sm font-medium transition-colors ${
                      isActive ? 'text-indigo-400' : 'text-slate-400 hover:text-slate-100'
                    }`
                  }
                >
                  {label}
                </NavLink>
              </li>
            ))}
          </ul>

          {/* Desktop auth buttons */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <>
                {user?.role === 'ADMIN' && (
                  <Link
                    to="/admin"
                    className="flex items-center gap-1.5 text-sm text-amber-400 hover:text-amber-300"
                  >
                    <Shield className="h-4 w-4" />
                    Admin
                  </Link>
                )}
                <Link
                  to="/profile"
                  className="flex items-center gap-1.5 text-sm text-slate-300 hover:text-white"
                >
                  <User className="h-4 w-4" />
                  {user?.username}
                </Link>
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-1.5 text-sm text-slate-400 hover:text-red-400 transition-colors"
                >
                  <LogOut className="h-4 w-4" />
                  Log out
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="text-sm text-slate-300 hover:text-white transition-colors"
                >
                  Log in
                </Link>
                <Link
                  to="/register"
                  className="rounded-md bg-indigo-600 px-4 py-1.5 text-sm font-medium text-white hover:bg-indigo-500 transition-colors"
                >
                  Sign up
                </Link>
              </>
            )}
          </div>

          {/* Mobile hamburger */}
          <button
            className="md:hidden text-slate-400 hover:text-white"
            onClick={() => setMenuOpen(!menuOpen)}
            aria-label="Toggle menu"
            aria-expanded={menuOpen}
          >
            {menuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </nav>

        {/* Mobile menu */}
        {menuOpen && (
          <div className="md:hidden border-t border-slate-800 bg-slate-950 px-4 py-4 flex flex-col gap-4">
            {NAV_LINKS.map(({ to, label }) => (
              <NavLink
                key={to}
                to={to}
                className={({ isActive }) =>
                  `text-sm font-medium ${isActive ? 'text-indigo-400' : 'text-slate-300'}`
                }
                onClick={() => setMenuOpen(false)}
              >
                {label}
              </NavLink>
            ))}
            <hr className="border-slate-800" />
            {isAuthenticated ? (
              <>
                <Link to="/profile" className="text-sm text-slate-300" onClick={() => setMenuOpen(false)}>
                  Profile ({user?.username})
                </Link>
                {user?.role === 'ADMIN' && (
                  <Link to="/admin" className="text-sm text-amber-400" onClick={() => setMenuOpen(false)}>
                    Admin
                  </Link>
                )}
                <button onClick={handleLogout} className="text-sm text-red-400 text-left">
                  Log out
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-sm text-slate-300" onClick={() => setMenuOpen(false)}>
                  Log in
                </Link>
                <Link to="/register" className="text-sm text-indigo-400" onClick={() => setMenuOpen(false)}>
                  Sign up
                </Link>
              </>
            )}
          </div>
        )}
      </header>

      {/* ── Main content ────────────────────────────────────────────────────── */}
      <main className="flex-1 mx-auto w-full max-w-7xl px-4 py-8">
        <Outlet />
      </main>

      {/* ── Footer ─────────────────────────────────────────────────────────── */}
      <footer className="border-t border-slate-800 py-6 text-center text-sm text-slate-500">
        © {new Date().getFullYear()} MiniForce · Built with React & Spring Boot
      </footer>
    </div>
  )
}
