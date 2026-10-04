import { ApiError } from '@/services/api'
import { useCallback, useEffect, useState } from 'react'

interface UseApiState<T> {
  data: T | null
  isLoading: boolean
  error: string | null
  refetch: () => void
}

/**
 * Generic hook for data fetching from the API.
 * Usage: const { data, isLoading, error, refetch } = useApi(() => api.problems.list())
 */
export function useApi<T>(
  fetcher: () => Promise<T>,
  deps: unknown[] = [],
): UseApiState<T> {
  const [data, setData] = useState<T | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [tick, setTick] = useState(0)

  const refetch = useCallback(() => setTick(t => t + 1), [])

  useEffect(() => {
    let cancelled = false
    setIsLoading(true)
    setError(null)
    fetcher()
      .then(result => { if (!cancelled) setData(result) })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(
            err instanceof ApiError
              ? err.message
              : 'Something went wrong. Please try again.',
          )
        }
      })
      .finally(() => { if (!cancelled) setIsLoading(false) })
    return () => { cancelled = true }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tick, ...deps])

  return { data, isLoading, error, refetch }
}
