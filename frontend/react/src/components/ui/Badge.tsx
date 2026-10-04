import type { HTMLAttributes, ReactNode } from 'react'

type BadgeVariant = 'default' | 'easy' | 'medium' | 'hard' | 'success' | 'error' | 'warning'

const variants: Record<BadgeVariant, string> = {
  default:  'bg-slate-700 text-slate-300',
  easy:     'bg-emerald-900/60 text-emerald-400',
  medium:   'bg-amber-900/60 text-amber-400',
  hard:     'bg-red-900/60 text-red-400',
  success:  'bg-emerald-900/60 text-emerald-400',
  error:    'bg-red-900/60 text-red-400',
  warning:  'bg-amber-900/60 text-amber-400',
}

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant
  children: ReactNode
}

export function Badge({ variant = 'default', children, className = '', ...rest }: BadgeProps) {
  return (
    <span
      {...rest}
      className={[
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium',
        variants[variant],
        className,
      ].join(' ')}
    >
      {children}
    </span>
  )
}
