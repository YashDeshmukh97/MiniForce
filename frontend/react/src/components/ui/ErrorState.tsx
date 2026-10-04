import { AlertCircle, RefreshCw } from 'lucide-react'
import { Button } from './Button'

interface ErrorStateProps {
  message?: string
  onRetry?: () => void
  className?: string
}

export function ErrorState({
  message = 'Something went wrong. Please try again.',
  onRetry,
  className = '',
}: ErrorStateProps) {
  return (
    <div
      className={`flex flex-col items-center justify-center gap-4 rounded-lg border border-red-900/50 bg-red-950/30 px-6 py-10 ${className}`}
    >
      <AlertCircle className="h-10 w-10 text-red-400" />
      <p className="text-sm text-red-300 text-center max-w-sm">{message}</p>
      {onRetry && (
        <Button variant="secondary" size="sm" onClick={onRetry}>
          <RefreshCw className="h-4 w-4" />
          Retry
        </Button>
      )}
    </div>
  )
}
