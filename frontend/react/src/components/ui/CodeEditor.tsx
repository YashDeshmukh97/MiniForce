import React, { forwardRef, useImperativeHandle, useRef } from 'react'
import type { Language } from '@/types'
import { applyEnter, applyPaste, applyShiftTab, applyTab } from '@/utils/editor'

export interface CodeEditorProps
  extends Omit<React.TextareaHTMLAttributes<HTMLTextAreaElement>, 'onChange' | 'value'> {
  value: string
  onChange: (value: string) => void
  language?: Language
}

export const CodeEditor = forwardRef<HTMLTextAreaElement, CodeEditorProps>(function CodeEditor(
  { value, onChange, language, onKeyDown, onPaste, disabled, ...props },
  ref
) {
  const textareaRef = useRef<HTMLTextAreaElement | null>(null)

  useImperativeHandle(ref, () => textareaRef.current as HTMLTextAreaElement)

  const updateEditor = (newValue: string, newStart: number, newEnd: number = newStart) => {
    onChange(newValue)
    if (textareaRef.current) {
      textareaRef.current.value = newValue
      textareaRef.current.setSelectionRange(newStart, newEnd)
    }
    requestAnimationFrame(() => {
      if (textareaRef.current) {
        textareaRef.current.setSelectionRange(newStart, newEnd)
      }
    })
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (onKeyDown) {
      onKeyDown(e)
      if (e.defaultPrevented) return
    }

    if (disabled) return

    const textarea = e.currentTarget
    const { selectionStart, selectionEnd, value: currentVal } = textarea

    if (e.key === 'Tab') {
      e.preventDefault()
      if (e.shiftKey) {
        const res = applyShiftTab(currentVal, selectionStart, selectionEnd)
        updateEditor(res.value, res.selectionStart, res.selectionEnd)
      } else {
        const res = applyTab(currentVal, selectionStart, selectionEnd)
        updateEditor(res.value, res.selectionStart, res.selectionEnd)
      }
      return
    }

    if (e.key === 'Enter') {
      e.preventDefault()
      const res = applyEnter(currentVal, selectionStart, selectionEnd, language)
      updateEditor(res.value, res.selectionStart, res.selectionEnd)
      return
    }
  }

  const handlePaste = (e: React.ClipboardEvent<HTMLTextAreaElement>) => {
    if (onPaste) {
      onPaste(e)
      if (e.defaultPrevented) return
    }

    if (disabled) return

    const text = e.clipboardData.getData('text/plain')
    if (!text) return

    e.preventDefault()
    const textarea = e.currentTarget
    const { selectionStart, selectionEnd, value: currentVal } = textarea

    const res = applyPaste(currentVal, selectionStart, selectionEnd, text, language)
    updateEditor(res.value, res.selectionStart, res.selectionEnd)
  }

  return (
    <textarea
      ref={textareaRef}
      value={value}
      onChange={e => onChange(e.target.value)}
      onKeyDown={handleKeyDown}
      onPaste={handlePaste}
      disabled={disabled}
      {...props}
    />
  )
})
