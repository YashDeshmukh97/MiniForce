import type { Language } from '@/types'

/**
 * Inserts 4 spaces at current cursor position (or indents all spanned lines if multiline).
 */
export function applyTab(
  value: string,
  selectionStart: number,
  selectionEnd: number
): { value: string; selectionStart: number; selectionEnd: number } {
  const isMultiline = value.slice(selectionStart, selectionEnd).includes('\n')

  if (!isMultiline) {
    // Single line / collapsed cursor: insert 4 spaces at cursor
    const newValue = value.slice(0, selectionStart) + '    ' + value.slice(selectionEnd)
    const newPos = selectionStart + 4
    return { value: newValue, selectionStart: newPos, selectionEnd: newPos }
  }

  // Multiline selection: indent each selected line by 4 spaces
  const lineStart = value.lastIndexOf('\n', selectionStart - 1) + 1
  const effectiveEnd =
    selectionEnd > selectionStart && value[selectionEnd - 1] === '\n'
      ? selectionEnd - 1
      : selectionEnd
  let lineEnd = value.indexOf('\n', effectiveEnd)
  if (lineEnd === -1) lineEnd = value.length

  const block = value.slice(lineStart, lineEnd)
  const lines = block.split('\n')
  const indentedBlock = lines.map(line => '    ' + line).join('\n')

  const newValue = value.slice(0, lineStart) + indentedBlock + value.slice(lineEnd)
  const newStart = selectionStart + 4
  const newEnd = selectionEnd + 4 * lines.length

  return { value: newValue, selectionStart: newStart, selectionEnd: newEnd }
}

/**
 * Removes one indentation level (up to 4 spaces, or 1 tab) from each selected line or the current line.
 */
export function applyShiftTab(
  value: string,
  selectionStart: number,
  selectionEnd: number
): { value: string; selectionStart: number; selectionEnd: number } {
  const lineStart = value.lastIndexOf('\n', selectionStart - 1) + 1
  const effectiveEnd =
    selectionEnd > selectionStart && value[selectionEnd - 1] === '\n'
      ? selectionEnd - 1
      : selectionEnd
  let lineEnd = value.indexOf('\n', effectiveEnd)
  if (lineEnd === -1) lineEnd = value.length

  const block = value.slice(lineStart, lineEnd)
  const lines = block.split('\n')

  let currentPos = lineStart
  let newStart = selectionStart
  let newEnd = selectionEnd
  const newLines: string[] = []

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i]
    let removeCount = 0
    if (line.startsWith('    ')) {
      removeCount = 4
    } else if (line.startsWith('\t')) {
      removeCount = 1
    } else {
      const match = line.match(/^ {1,3}/)
      if (match) {
        removeCount = match[0].length
      }
    }

    newLines.push(line.slice(removeCount))

    if (currentPos <= selectionStart) {
      const removedBeforeStart = Math.min(Math.max(0, selectionStart - currentPos), removeCount)
      newStart -= removedBeforeStart
    }
    if (currentPos < selectionEnd) {
      const removedBeforeEnd = Math.min(Math.max(0, selectionEnd - currentPos), removeCount)
      newEnd -= removedBeforeEnd
    }

    currentPos += line.length + 1
  }

  const newBlock = newLines.join('\n')
  const newValue = value.slice(0, lineStart) + newBlock + value.slice(lineEnd)

  return {
    value: newValue,
    selectionStart: Math.max(lineStart, newStart),
    selectionEnd: Math.max(newStart, newEnd),
  }
}

/**
 * Checks whether the current line ending requires an extra level of indentation (+4 spaces).
 */
export function shouldIncreaseIndent(lineBeforeCursor: string, language?: Language | string): boolean {
  const trimmed = lineBeforeCursor.trimEnd()
  if (trimmed.endsWith(':')) return true

  // Check if colon precedes an inline comment, e.g. "if x > 0:  # comment"
  const commentIdx = trimmed.indexOf('#')
  if (commentIdx !== -1) {
    const beforeComment = trimmed.slice(0, commentIdx).trimEnd()
    if (beforeComment.endsWith(':')) return true
  }

  // Java opening brace support
  if (language === 'JAVA_17') {
    if (trimmed.endsWith('{')) return true
  }

  return false
}

/**
 * Handles Enter key press: smart indent after ':' (or '{' in Java), and preserves indentation on new lines.
 */
export function applyEnter(
  value: string,
  selectionStart: number,
  selectionEnd: number,
  language?: Language | string
): { value: string; selectionStart: number; selectionEnd: number } {
  const lineStart = value.lastIndexOf('\n', selectionStart - 1) + 1
  const lineBeforeCursor = value.slice(lineStart, selectionStart)

  const indentMatch = lineBeforeCursor.match(/^[ \t]*/)
  let currentIndent = indentMatch ? indentMatch[0].replace(/\t/g, '    ') : ''

  if (shouldIncreaseIndent(lineBeforeCursor, language)) {
    currentIndent += '    '
  }

  const textToInsert = '\n' + currentIndent
  const newValue = value.slice(0, selectionStart) + textToInsert + value.slice(selectionEnd)
  const newPos = selectionStart + textToInsert.length

  return { value: newValue, selectionStart: newPos, selectionEnd: newPos }
}

/**
 * Handles paste: converts tabs to 4 spaces, preserves multiline indentation,
 * and eliminates unwanted leading spaces.
 */
export function applyPaste(
  value: string,
  selectionStart: number,
  selectionEnd: number,
  textToPaste: string,
  _language?: Language | string
): { value: string; selectionStart: number; selectionEnd: number } {
  // 1. Normalize line endings
  let text = textToPaste.replace(/\r\n/g, '\n').replace(/\r/g, '\n')

  // 2. Expand all tabs to 4 spaces to avoid mixing tabs and spaces
  text = text.replace(/\t/g, '    ')

  const lines = text.split('\n')

  // Single-line paste
  if (lines.length === 1) {
    const lineStart = value.lastIndexOf('\n', selectionStart - 1) + 1
    let lineEnd = value.indexOf('\n', selectionEnd)
    if (lineEnd === -1) lineEnd = value.length

    const prefix = value.slice(lineStart, selectionStart)
    const suffix = value.slice(selectionEnd, lineEnd)

    let insertText = lines[0]
    // If cursor is on a blank line with spaces, and pasted line already starts with those spaces, avoid duplicating
    if (prefix.trim() === '' && suffix.trim() === '' && prefix.length > 0 && insertText.startsWith(prefix)) {
      insertText = insertText.slice(prefix.length)
    }

    const newValue = value.slice(0, selectionStart) + insertText + value.slice(selectionEnd)
    const newPos = selectionStart + insertText.length
    return { value: newValue, selectionStart: newPos, selectionEnd: newPos }
  }

  // Multiline paste
  const lineStart = value.lastIndexOf('\n', selectionStart - 1) + 1
  let lineEnd = value.indexOf('\n', selectionEnd)
  if (lineEnd === -1) lineEnd = value.length

  const prefix = value.slice(lineStart, selectionStart)
  const suffix = value.slice(selectionEnd, lineEnd)
  const isPrefixWhitespace = prefix.trim() === ''
  const isSuffixWhitespace = suffix.trim() === ''

  const nonEmptyLines = lines.filter(l => l.trim().length > 0)
  if (nonEmptyLines.length === 0) {
    const newValue = value.slice(0, selectionStart) + text + value.slice(selectionEnd)
    const newPos = selectionStart + text.length
    return { value: newValue, selectionStart: newPos, selectionEnd: newPos }
  }

  // Minimum leading spaces among non-empty lines
  const minIndent = Math.min(
    ...nonEmptyLines.map(l => {
      const m = l.match(/^ */)
      return m ? m[0].length : 0
    })
  )

  const firstNonEmpty = nonEmptyLines[0]
  const isTopLevelCode =
    /^(class\s+\w+|import\s+|from\s+|def\s+)/.test(firstNonEmpty.trimStart()) &&
    (minIndent === 0 || firstNonEmpty.search(/\S/) === minIndent)

  const isAllSelected = selectionStart === 0 && selectionEnd === value.length

  // Check if user is pasting a full class Solution solution
  const isPastingFullSolution =
    lines.some(l => /^class\s+Solution\b/.test(l)) && value.includes('class Solution')

  if (isPastingFullSolution && !isAllSelected) {
    // If the pasted text is a complete class Solution and the document already has a class Solution,
    // replace the existing document so we don't end up with nested/duplicate class Solution
    const adjustedLines = lines.map(line => {
      if (line.trim().length === 0) return ''
      return line.slice(minIndent)
    })
    const block = adjustedLines.join('\n')
    return { value: block, selectionStart: block.length, selectionEnd: block.length }
  }

  if (isPrefixWhitespace) {
    let targetIndent = ''

    if (isTopLevelCode || isAllSelected) {
      targetIndent = ''
    } else if (prefix.length > 0) {
      targetIndent = prefix
    } else {
      // selectionStart === lineStart (column 0). Check previous line:
      const prevLineEnd = lineStart - 1
      if (prevLineEnd >= 0) {
        const prevLineStart = value.lastIndexOf('\n', prevLineEnd - 1) + 1
        const prevLine = value.slice(prevLineStart, prevLineEnd)
        const prevIndent = (prevLine.match(/^[ \t]*/) || [''])[0].replace(/\t/g, '    ')
        if (prevLine.trimEnd().endsWith(':')) {
          targetIndent = prevIndent + '    '
        } else {
          targetIndent = prevIndent
        }
      } else {
        targetIndent = ''
      }
    }

    const adjustedLines = lines.map(line => {
      if (line.trim().length === 0) return ''
      return targetIndent + line.slice(minIndent)
    })
    const block = adjustedLines.join('\n')

    const replaceStart = lineStart
    const replaceEnd = isSuffixWhitespace ? lineEnd : selectionEnd

    const newValue = value.slice(0, replaceStart) + block + value.slice(replaceEnd)
    const newPos = replaceStart + block.length

    return { value: newValue, selectionStart: newPos, selectionEnd: newPos }
  } else {
    // Inline multiline paste (cursor has non-whitespace before it on the same line)
    const prefixIndent = (prefix.match(/^[ \t]*/) || [''])[0].replace(/\t/g, '    ')
    const adjustedLines = lines.map((line, idx) => {
      if (idx === 0) return line
      if (line.trim().length === 0) return ''
      return prefixIndent + line.slice(minIndent)
    })
    const block = adjustedLines.join('\n')

    const newValue = value.slice(0, selectionStart) + block + value.slice(selectionEnd)
    const newPos = selectionStart + block.length

    return { value: newValue, selectionStart: newPos, selectionEnd: newPos }
  }
}
