import { Button, Card, CardHeader, CardTitle, Input } from '@/components/ui'
import { useAuth } from '@/context/AuthContext'
import { ApiError } from '@/services/api'
import { motion } from 'framer-motion'
import { UserPlus } from 'lucide-react'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    fullName: '',
    username: '',
    email: '',
    password: '',
  })
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({ ...prev, [e.target.id]: e.target.value }))
    // Clear specific field error when typing
    if (fieldErrors[e.target.id]) {
      setFieldErrors((prev) => ({ ...prev, [e.target.id]: '' }))
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)
    setFieldErrors({})

    try {
      await register(formData)
      navigate('/problems', { replace: true })
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message)
        if (err.fields) {
          setFieldErrors(err.fields)
        }
      } else {
        setError('An unexpected error occurred.')
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-[70vh] items-center justify-center">
      <motion.div
        initial={{ opacity: 0, y: 15 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="w-full max-w-md py-8"
      >
        <Card className="p-6 sm:p-8">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-indigo-500/10">
              <UserPlus className="h-6 w-6 text-indigo-400" />
            </div>
            <CardTitle className="text-2xl">Create Account</CardTitle>
            <p className="mt-2 text-sm text-slate-400">
              Join MiniForce and start solving
            </p>
          </CardHeader>

          {error && !Object.keys(fieldErrors).length && (
            <div className="mb-6 rounded-md bg-red-950/50 p-3 text-sm text-red-400 border border-red-900/50">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <Input
              label="Full Name"
              id="fullName"
              type="text"
              placeholder="Ada Lovelace"
              value={formData.fullName}
              onChange={handleChange}
              disabled={isLoading}
              error={fieldErrors.fullName}
              required
            />
            
            <Input
              label="Username"
              id="username"
              type="text"
              placeholder="adalovelace"
              value={formData.username}
              onChange={handleChange}
              disabled={isLoading}
              error={fieldErrors.username}
              required
            />

            <Input
              label="Email Address"
              id="email"
              type="email"
              placeholder="ada@example.com"
              value={formData.email}
              onChange={handleChange}
              disabled={isLoading}
              error={fieldErrors.email}
              required
            />

            <Input
              label="Password"
              id="password"
              type="password"
              placeholder="••••••••"
              value={formData.password}
              onChange={handleChange}
              disabled={isLoading}
              error={fieldErrors.password}
              required
              minLength={6}
            />

            <Button type="submit" className="mt-4 w-full" isLoading={isLoading}>
              Create Account
            </Button>
          </form>

          <p className="mt-6 text-center text-sm text-slate-400">
            Already have an account?{' '}
            <Link to="/login" className="text-indigo-400 hover:text-indigo-300 font-medium">
              Sign in
            </Link>
          </p>
        </Card>
      </motion.div>
    </div>
  )
}
