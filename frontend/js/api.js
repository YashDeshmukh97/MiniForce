(() => {
  'use strict';
  const base = window.MINIFORCE_API_BASE || 'http://127.0.0.1:8080/api';
  const TOKEN_KEY = 'miniforce-auth-token';
  class ApiError extends Error { constructor(message, status, details) { super(message); this.status = status; this.details = details; } }
  const request = async (path, options = {}) => {
    const headers = { Accept: 'application/json', ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...(options.headers || {}) };
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) headers.Authorization = `Bearer ${token}`;
    let response;
    try { response = await fetch(`${base}${path}`, { ...options, headers }); }
    catch { throw new ApiError('Cannot reach MiniForce API. Start the backend and check your network connection.', 0); }
    if (response.status === 204) return null;
    const data = await response.json().catch(() => ({}));
    if (!response.ok) throw new ApiError(data.message || `Request failed (${response.status})`, response.status, data.fields);
    return data;
  };
  const query = (params) => { const p = new URLSearchParams(); Object.entries(params).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') p.set(k, v); }); const text = p.toString(); return text ? `?${text}` : ''; };
  window.MiniForceApi = {
    base, ApiError, token: { get: () => localStorage.getItem(TOKEN_KEY), set: (v) => localStorage.setItem(TOKEN_KEY, v), clear: () => localStorage.removeItem(TOKEN_KEY) },
    auth: { register: (body) => request('/auth/register', { method: 'POST', body: JSON.stringify(body) }), login: (body) => request('/auth/login', { method: 'POST', body: JSON.stringify(body) }), me: () => request('/auth/me') },
    problems: { list: (filters = {}) => request(`/problems${query(filters)}`), get: (id) => request(`/problems/${id}`), getBySlug: (slug) => request(`/problems/slug/${encodeURIComponent(slug)}`), create: (body) => request('/problems', { method: 'POST', body: JSON.stringify(body) }), update: (id, body) => request(`/problems/${id}`, { method: 'PUT', body: JSON.stringify(body) }), remove: (id) => request(`/problems/${id}`, { method: 'DELETE' }), tests: (id) => request(`/problems/${id}/test-cases`), addTest: (id, body) => request(`/problems/${id}/test-cases`, { method: 'POST', body: JSON.stringify(body) }) },
    submissions: { submit: (body) => request('/submissions', { method: 'POST', body: JSON.stringify(body) }), submitAsync: (body) => request('/submissions/async', { method: 'POST', body: JSON.stringify(body) }), get: (id) => request(`/submissions/${id}`), run: (body) => request('/submissions/run', { method: 'POST', body: JSON.stringify(body) }), list: () => request('/submissions'), forProblem: (id) => request(`/problems/${id}/submissions`) },
    users: { profile: () => request('/users/me'), update: (body) => request('/users/me', { method: 'PUT', body: JSON.stringify(body) }), updateAvatar: (body) => request('/users/me/avatar', { method: 'PUT', body: JSON.stringify(body) }), leaderboard: () => request('/leaderboard') },
    contests: { list: () => request('/contests'), get: (id) => request(`/contests/${id}`), problems: (id) => request(`/contests/${id}/problems`), join: (id) => request(`/contests/${id}/join`, { method: 'POST' }), leaderboard: (id) => request(`/contests/${id}/leaderboard`), create: (body) => request('/contests', { method: 'POST', body: JSON.stringify(body) }), update: (id, body) => request(`/contests/${id}`, { method: 'PUT', body: JSON.stringify(body) }), remove: (id) => request(`/contests/${id}`, { method: 'DELETE' }) },
    discussions: { list: () => request('/discussions'), get: (id) => request(`/discussions/${id}`), create: (body) => request('/discussions', { method: 'POST', body: JSON.stringify(body) }), reply: (id, body) => request(`/discussions/${id}/replies`, { method: 'POST', body: JSON.stringify(body) }) },
    admin: { dashboard: () => request('/admin/dashboard'), users: (search = '') => request(`/admin/users${query({ search })}`), role: (id, role) => request(`/admin/users/${id}/role`, { method: 'PUT', body: JSON.stringify({ role }) }), removeDiscussion: (id) => request(`/admin/discussions/${id}`, { method: 'DELETE' }) }
  };
})();
