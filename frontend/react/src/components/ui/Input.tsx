import type { InputHTMLAttributes } from 'react'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
}

export function Input({ label, error, className = '', id, ...rest }: InputProps) {
  const inputId = id ?? label?.toLowerCase().replace(/\s+/g, '-')
  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label htmlFor={inputId} className="text-sm font-medium text-slate-300">
          {label}
        </label>
      )}
      <input
        id={inputId}
        {...rest}
        className={[
          'rounded-md border bg-slate-900 px-3 py-2 text-sm text-slate-100',
          'placeholder:text-slate-500 focus:outline-none focus:ring-2',
          error
            ? 'border-red-500 focus:ring-red-500'
            : 'border-slate-700 focus:ring-indigo-500',
          className,
        ].join(' ')}
      />
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  )
}
