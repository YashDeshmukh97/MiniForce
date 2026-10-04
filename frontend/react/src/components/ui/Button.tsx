import type { ButtonHTMLAttributes, ReactNode } from 'react'

type Variant = 'primary' | 'secondary' | 'ghost' | 'danger'
type Size = 'sm' | 'md' | 'lg'

const variants: Record<Variant, string> = {
  primary:   'bg-indigo-600 text-white hover:bg-indigo-500 disabled:bg-indigo-800',
  secondary: 'bg-slate-700 text-slate-100 hover:bg-slate-600 disabled:bg-slate-800',
  ghost:     'text-slate-300 hover:text-white hover:bg-slate-800 disabled:opacity-50',
  danger:    'bg-red-600 text-white hover:bg-red-500 disabled:bg-red-900',
}

const sizes: Record<Size, string> = {
  sm: 'px-3 py-1.5 text-xs',
  md: 'px-4 py-2 text-sm',
  lg: 'px-6 py-2.5 text-base',
}

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant
  size?: Size
  isLoading?: boolean
  children: ReactNode
}

export function Button({
  variant = 'primary',
  size = 'md',
  isLoading = false,
  className = '',
  disabled,
  children,
  ...rest
}: ButtonProps) {
  return (
    <button
      {...rest}
      disabled={disabled || isLoading}
      className={[
        'inline-flex items-center justify-center gap-2 rounded-md font-medium',
        'transition-colors focus-visible:outline focus-visible:outline-2',
        'focus-visible:outline-indigo-500 disabled:cursor-not-allowed',
        variants[variant],
        sizes[size],
        className,
      ].join(' ')}
    >
      {isLoading ? (
        <span className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" />
      ) : null}
      {children}
    </button>
  )
}
