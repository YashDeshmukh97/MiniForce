import type { HTMLAttributes, ReactNode } from 'react'

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode
}

export function Card({ children, className = '', ...rest }: CardProps) {
  return (
    <div
      {...rest}
      className={`rounded-lg border border-slate-800 bg-slate-900 p-4 ${className}`}
    >
      {children}
    </div>
  )
}

export function CardHeader({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <div className={`mb-3 ${className}`}>{children}</div>
}

export function CardTitle({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <h3 className={`text-lg font-semibold text-slate-100 ${className}`}>{children}</h3>
}
