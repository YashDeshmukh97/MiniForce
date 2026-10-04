// localStorage key constants — must match the existing vanilla frontend exactly
// so that drafts written by the vanilla app are readable by the React app and vice versa.

export const TOKEN_KEY = 'miniforce-auth-token'

/** Java code draft key for a given problem ID */
export const javaDraftKey = (problemId: number): string =>
  `miniforce-draft-${problemId}`

/** Python code draft key for a given problem ID */
export const pythonDraftKey = (problemId: number): string =>
  `miniforce-draft-py-${problemId}`

export const storage = {
  getToken: (): string | null => localStorage.getItem(TOKEN_KEY),
  setToken: (token: string): void => { localStorage.setItem(TOKEN_KEY, token) },
  clearToken: (): void => { localStorage.removeItem(TOKEN_KEY) },

  getJavaDraft: (problemId: number): string | null =>
    localStorage.getItem(javaDraftKey(problemId)),
  setJavaDraft: (problemId: number, code: string): void => {
    localStorage.setItem(javaDraftKey(problemId), code)
  },

  getPythonDraft: (problemId: number): string | null =>
    localStorage.getItem(pythonDraftKey(problemId)),
  setPythonDraft: (problemId: number, code: string): void => {
    localStorage.setItem(pythonDraftKey(problemId), code)
  },
}
