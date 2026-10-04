import { Loader2 } from 'lucide-react'

interface LoadingProps {
  message?: string
  className?: string
}

export function Loading({ message = 'Loading...', className = '' }: LoadingProps) {
  return (
    <div className={`flex flex-col items-center justify-center gap-3 py-12 ${className}`}>
      <Loader2 className="h-8 w-8 animate-spin text-indigo-400" />
      <p className="text-sm text-slate-400">{message}</p>
    </div>
  )
}

/** Full-page loading state */
export function PageLoading() {
  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <Loading />
    </div>
  )
}
