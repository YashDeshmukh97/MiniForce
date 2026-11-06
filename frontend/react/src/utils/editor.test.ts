import { describe, it } from 'node:test'
import assert from 'node:assert/strict'
import {
  applyTab,
  applyShiftTab,
  applyEnter,
  applyPaste,
  shouldIncreaseIndent,
} from './editor.ts'

describe('shouldIncreaseIndent helper', () => {
  it('returns true for colons and false for non-colons', () => {
    assert.equal(shouldIncreaseIndent('def foo():'), true)
    assert.equal(shouldIncreaseIndent('if x > 0:  # comment'), true)
    assert.equal(shouldIncreaseIndent('x = 1'), false)
    assert.equal(shouldIncreaseIndent('public class Solution {', 'JAVA_17'), true)
  })
})

describe('CodeEditor Tab Support', () => {
  it('inserts exactly 4 spaces at cursor on collapsed selection', () => {
    const initial = 'def solve():'
    // Cursor at end of 'def solve():'
    const res = applyTab(initial, initial.length, initial.length)
    assert.equal(res.value, 'def solve():    ')
    assert.equal(res.selectionStart, initial.length + 4)
    assert.equal(res.selectionEnd, initial.length + 4)
  })

  it('inserts 4 spaces at cursor inside existing line', () => {
    const initial = 'x = 1'
    // Cursor at index 0
    const res = applyTab(initial, 0, 0)
    assert.equal(res.value, '    x = 1')
    assert.equal(res.selectionStart, 4)
    assert.equal(res.selectionEnd, 4)
  })

  it('indents all spanned lines when multiline selection is present', () => {
    const initial = 'line1\nline2\nline3'
    // Select from index 0 to line2
    const res = applyTab(initial, 0, 11)
    assert.equal(res.value, '    line1\n    line2\nline3')
    assert.equal(res.selectionStart, 4)
    assert.equal(res.selectionEnd, 11 + 4 * 2)
  })
})

describe('CodeEditor Shift + Tab Support', () => {
  it('removes 4 spaces from an 8-space indented line', () => {
    const initial = '        d = {}'
    // Cursor at column 8 (before 'd')
    const res = applyShiftTab(initial, 8, 8)
    assert.equal(res.value, '    d = {}')
    assert.equal(res.selectionStart, 4)
    assert.equal(res.selectionEnd, 4)
  })

  it('removes 4 spaces from a 4-space indented line to 0 spaces', () => {
    const initial = '    d = {}'
    const res = applyShiftTab(initial, 4, 4)
    assert.equal(res.value, 'd = {}')
    assert.equal(res.selectionStart, 0)
    assert.equal(res.selectionEnd, 0)
  })

  it('does nothing to a 0-indent line', () => {
    const initial = 'class Solution:'
    const res = applyShiftTab(initial, 0, 0)
    assert.equal(res.value, 'class Solution:')
    assert.equal(res.selectionStart, 0)
    assert.equal(res.selectionEnd, 0)
  })

  it('removes 1-3 spaces if line starts with fewer than 4 spaces', () => {
    const initial = '  x = 1'
    const res = applyShiftTab(initial, 2, 2)
    assert.equal(res.value, 'x = 1')
    assert.equal(res.selectionStart, 0)
    assert.equal(res.selectionEnd, 0)
  })

  it('removes a tab character if present', () => {
    const initial = '\tx = 1'
    const res = applyShiftTab(initial, 1, 1)
    assert.equal(res.value, 'x = 1')
    assert.equal(res.selectionStart, 0)
    assert.equal(res.selectionEnd, 0)
  })

  it('unindents all selected lines across a multiline selection', () => {
    const initial = '        a = 1\n        b = 2\n        c = 3'
    const res = applyShiftTab(initial, 0, initial.length)
    assert.equal(res.value, '    a = 1\n    b = 2\n    c = 3')
  })
})

describe('CodeEditor Smart Indentation (Enter)', () => {
  it('indents by 4 additional spaces after class definition colon', () => {
    const initial = 'class Solution:'
    const res = applyEnter(initial, initial.length, initial.length, 'PYTHON_3')
    assert.equal(res.value, 'class Solution:\n    ')
    assert.equal(res.selectionStart, 'class Solution:\n    '.length)
  })

  it('indents by 4 additional spaces after method definition colon', () => {
    const initial = 'class Solution:\n    def twoSum(self, nums, target):'
    const res = applyEnter(initial, initial.length, initial.length, 'PYTHON_3')
    assert.equal(res.value, 'class Solution:\n    def twoSum(self, nums, target):\n        ')
  })

  it('indents by 4 additional spaces after loop/condition colon', () => {
    const initial = '        for i, n in enumerate(nums):'
    const res = applyEnter(initial, initial.length, initial.length, 'PYTHON_3')
    assert.equal(res.value, '        for i, n in enumerate(nums):\n            ')
  })

  it('indents by 4 additional spaces after colon with trailing spaces', () => {
    const initial = '        if target - n in d:   '
    const res = applyEnter(initial, initial.length, initial.length, 'PYTHON_3')
    assert.equal(res.value, '        if target - n in d:   \n            ')
  })

  it('indents by 4 additional spaces after colon with inline comment', () => {
    const initial = '        if target - n in d:  # check existence'
    const res = applyEnter(initial, initial.length, initial.length, 'PYTHON_3')
    assert.equal(res.value, '        if target - n in d:  # check existence\n            ')
  })

  it('preserves existing indentation on lines without colon', () => {
    const initial = '        d = {}'
    const res = applyEnter(initial, initial.length, initial.length, 'PYTHON_3')
    assert.equal(res.value, '        d = {}\n        ')
    assert.equal(res.selectionStart, '        d = {}\n        '.length)
  })

  it('preserves indentation in Java code on non-brace line', () => {
    const initial = '        int n = nums.length;'
    const res = applyEnter(initial, initial.length, initial.length, 'JAVA_17')
    assert.equal(res.value, '        int n = nums.length;\n        ')
  })

  it('indents by 4 spaces in Java code after opening brace {', () => {
    const initial = 'public class Solution {'
    const res = applyEnter(initial, initial.length, initial.length, 'JAVA_17')
    assert.equal(res.value, 'public class Solution {\n    ')
  })
})

describe('CodeEditor Paste Handling', () => {
  it('converts tab characters to 4 spaces', () => {
    const textToPaste = 'def solve():\n\treturn 42'
    const res = applyPaste('', 0, 0, textToPaste, 'PYTHON_3')
    assert.equal(res.value, 'def solve():\n    return 42')
  })

  it('pastes unindented multiline body into an 8-space indented block correctly', () => {
    // Starter code state: cursor at line 3, indented with 8 spaces
    const initial = 'class Solution:\n    def twoSum(self, nums, target):\n        '
    const cursor = initial.length
    const bodySnippet = [
      'd = {}',
      'for i, n in enumerate(nums):',
      '    if target - n in d:',
      '        return [d[target - n], i]',
      '    d[n] = i',
    ].join('\n')

    const res = applyPaste(initial, cursor, cursor, bodySnippet, 'PYTHON_3')
    const expected = [
      'class Solution:',
      '    def twoSum(self, nums, target):',
      '        d = {}',
      '        for i, n in enumerate(nums):',
      '            if target - n in d:',
      '                return [d[target - n], i]',
      '            d[n] = i',
    ].join('\n')

    assert.equal(res.value, expected)
  })

  it('pastes already-indented multiline body into 8-space indented block without duplicate indent', () => {
    const initial = 'class Solution:\n    def twoSum(self, nums, target):\n        '
    const cursor = initial.length
    const alreadyIndentedBody = [
      '        d = {}',
      '        for i, n in enumerate(nums):',
      '            if target - n in d:',
      '                return [d[target - n], i]',
      '            d[n] = i',
    ].join('\n')

    const res = applyPaste(initial, cursor, cursor, alreadyIndentedBody, 'PYTHON_3')
    const expected = [
      'class Solution:',
      '    def twoSum(self, nums, target):',
      '        d = {}',
      '        for i, n in enumerate(nums):',
      '            if target - n in d:',
      '                return [d[target - n], i]',
      '            d[n] = i',
    ].join('\n')

    assert.equal(res.value, expected)
  })

  it('pastes a complete class Solution solution at column 0 without unwanted leading spaces', () => {
    const initial = 'class Solution:\n    def twoSum(self, nums, target):\n        # Starter\n        pass\n'
    // User clicks inside without selecting all
    const cursor = initial.indexOf('pass')
    const fullSolution = [
      'class Solution:',
      '    def twoSum(self, nums, target):',
      '        d = {}',
      '        for i, n in enumerate(nums):',
      '            if target - n in d:',
      '                return [d[target - n], i]',
      '            d[n] = i',
    ].join('\n')

    const res = applyPaste(initial, cursor, cursor, fullSolution, 'PYTHON_3')
    assert.equal(res.value, fullSolution)
  })

  it('strips common indentation when copying code with extra indentation (e.g. from markdown)', () => {
    const indentedMarkdownCode = [
      '    class Solution:',
      '        def twoSum(self, nums, target):',
      '            return []',
    ].join('\n')

    const res = applyPaste('', 0, 0, indentedMarkdownCode, 'PYTHON_3')
    const expected = [
      'class Solution:',
      '    def twoSum(self, nums, target):',
      '        return []',
    ].join('\n')

    assert.equal(res.value, expected)
  })

  it('handles single-line paste on indented blank line without duplicating prefix spaces', () => {
    const initial = 'class Solution:\n    def twoSum(self, nums, target):\n        '
    const cursor = initial.length
    const singleLine = '        return []'
    const res = applyPaste(initial, cursor, cursor, singleLine, 'PYTHON_3')
    assert.equal(
      res.value,
      'class Solution:\n    def twoSum(self, nums, target):\n        return []'
    )
  })
})

describe('CodeEditor Full Typing Simulation (Nested Python Two Sum)', () => {
  it('builds a complete nested Two Sum solution with perfect indentation', () => {
    let code = ''
    let cursor = 0

    const type = (str: string) => {
      code = code.slice(0, cursor) + str + code.slice(cursor)
      cursor += str.length
    }
    const pressEnter = () => {
      const res = applyEnter(code, cursor, cursor, 'PYTHON_3')
      code = res.value
      cursor = res.selectionStart
    }
    const pressShiftTab = () => {
      const res = applyShiftTab(code, cursor, cursor)
      code = res.value
      cursor = res.selectionStart
    }

    // 1. class Solution:
    type('class Solution:')
    pressEnter()
    assert.equal(cursor, 'class Solution:\n    '.length)

    // 2. def twoSum(self, nums, target):
    type('def twoSum(self, nums, target):')
    pressEnter()
    assert.equal(cursor, 'class Solution:\n    def twoSum(self, nums, target):\n        '.length)

    // 3. d = {}
    type('d = {}')
    pressEnter()
    assert.equal(cursor, 'class Solution:\n    def twoSum(self, nums, target):\n        d = {}\n        '.length)

    // 4. for i, n in enumerate(nums):
    type('for i, n in enumerate(nums):')
    pressEnter()
    assert.equal(cursor, 'class Solution:\n    def twoSum(self, nums, target):\n        d = {}\n        for i, n in enumerate(nums):\n            '.length)

    // 5. if target - n in d:
    type('if target - n in d:')
    pressEnter()
    assert.equal(cursor, 'class Solution:\n    def twoSum(self, nums, target):\n        d = {}\n        for i, n in enumerate(nums):\n            if target - n in d:\n                '.length)

    // 6. return [d[target - n], i]
    type('return [d[target - n], i]')
    pressEnter()

    // 7. Unindent back to loop body (Shift + Tab) and write d[n] = i
    pressShiftTab()
    type('d[n] = i')

    const expected = [
      'class Solution:',
      '    def twoSum(self, nums, target):',
      '        d = {}',
      '        for i, n in enumerate(nums):',
      '            if target - n in d:',
      '                return [d[target - n], i]',
      '            d[n] = i',
    ].join('\n')

    assert.equal(code, expected)
  })
})

describe('CodeEditor Java Editing Compatibility', () => {
  it('builds a Java Two Sum skeleton without indentation errors', () => {
    let code = ''
    let cursor = 0

    const type = (str: string) => {
      code = code.slice(0, cursor) + str + code.slice(cursor)
      cursor += str.length
    }
    const pressEnter = () => {
      const res = applyEnter(code, cursor, cursor, 'JAVA_17')
      code = res.value
      cursor = res.selectionStart
    }

    type('public class Solution {')
    pressEnter()
    assert.equal(code, 'public class Solution {\n    ')

    type('public int[] twoSum(int[] nums, int target) {')
    pressEnter()
    assert.equal(code, 'public class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        ')

    type('int n = nums.length;')
    pressEnter()
    assert.equal(code, 'public class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        int n = nums.length;\n        ')
  })
})
