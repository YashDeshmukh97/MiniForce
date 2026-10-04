(() => {
  'use strict';
  const api = MiniForceApi, ui = MiniForce;
  
  document.addEventListener('DOMContentLoaded', async () => {
    if (!document.getElementById('stat-users')) return;
    if (!ui.requireLogin()) return;
    
    const errorEl = document.getElementById('admin-error');
    const uList = document.getElementById('admin-user-list');
    const dList = document.getElementById('admin-discussion-list');
    
    try {
      const [stats, users, discussions] = await Promise.all([
        api.admin.dashboard(),
        api.admin.users(),
        api.discussions.list()
      ]);
      
      document.getElementById('stat-users').textContent = stats.users || 0;
      document.getElementById('stat-subs').textContent = stats.submissions || 0;
      document.getElementById('stat-problems').textContent = stats.problems || 0;
      document.getElementById('stat-discussions').textContent = stats.contests || 0; // Using contests stat in discussion placeholder if not present, fallback
      
      uList.innerHTML = users.length === 0 ? '<tr><td colspan="5" class="table-state">No users found.</td></tr>' : users.map(u => `
        <tr>
          <td class="caption">${u.id}</td>
          <td><b>${ui.escape(u.username)}</b></td>
          <td class="muted">${ui.escape(u.email)}</td>
          <td>
            <select data-user="${u.id}" style="padding: 4px; border-radius: 4px; background: var(--bg); color: var(--text); border: 1px solid var(--line);">
              <option ${u.role === 'USER' ? 'selected' : ''}>USER</option>
              <option ${u.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
            </select>
          </td>
          <td>-</td>
        </tr>
      `).join('');
      
      dList.innerHTML = discussions.length === 0 ? '<tr><td colspan="4" class="table-state">No discussions found.</td></tr>' : discussions.map(d => `
        <tr data-discussion-row="${d.id}">
          <td class="caption">${d.id}</td>
          <td><b>${ui.escape(d.title)}</b></td>
          <td class="muted">${ui.escape(d.authorUsername || 'Unknown')}</td>
          <td>
            <button class="btn-danger btn-small" data-delete-discussion="${d.id}">Delete</button>
          </td>
        </tr>
      `).join('');
      
      document.querySelectorAll('[data-user]').forEach(select => select.onchange = async () => {
        try {
          await api.admin.role(select.dataset.user, select.value);
              const f = document.getElementById('problem-form');
      if (f) {
        f.onsubmit = async e => {
          e.preventDefault();
          const form = new FormData(f);
          const msg = f.querySelector('p');
          try {
            await api.problems.create({
              slug: form.get('slug'),
              title: form.get('title'),
              difficulty: form.get('difficulty'),
              description: form.get('description'),
              starterCode: 'class Solution { }',
              constraintsText: 'See problem statement.',
              acceptanceRate: 0,
              topics: form.get('topics').split(',').map(x => x.trim()).filter(Boolean)
            });
            ui.message(msg, 'Problem created successfully.', 'success');
            f.reset();
          } catch (err) {
            ui.message(msg, ui.errorText(err));
          }
        };
      }
    } catch (err) {
          alert(ui.errorText(err));
        }
      });
      
      document.querySelectorAll('[data-delete-discussion]').forEach(btn => btn.onclick = async () => {
        if (!confirm('Are you sure you want to delete this discussion?')) return;
        btn.disabled = true;
        btn.textContent = 'Deleting...';
        try {
          await api.admin.removeDiscussion(btn.dataset.deleteDiscussion);
          const row = document.querySelector(`[data-discussion-row="${btn.dataset.deleteDiscussion}"]`);
          if (row) {
            row.style.opacity = '0.5';
            setTimeout(() => row.remove(), 300);
          }
              const f = document.getElementById('problem-form');
      if (f) {
        f.onsubmit = async e => {
          e.preventDefault();
          const form = new FormData(f);
          const msg = f.querySelector('p');
          try {
            await api.problems.create({
              slug: form.get('slug'),
              title: form.get('title'),
              difficulty: form.get('difficulty'),
              description: form.get('description'),
              starterCode: 'class Solution { }',
              constraintsText: 'See problem statement.',
              acceptanceRate: 0,
              topics: form.get('topics').split(',').map(x => x.trim()).filter(Boolean)
            });
            ui.message(msg, 'Problem created successfully.', 'success');
            f.reset();
          } catch (err) {
            ui.message(msg, ui.errorText(err));
          }
        };
      }
    } catch (err) {
          alert(ui.errorText(err));
          btn.disabled = false;
          btn.textContent = 'Delete';
        }
      });

          const f = document.getElementById('problem-form');
      if (f) {
        f.onsubmit = async e => {
          e.preventDefault();
          const form = new FormData(f);
          const msg = f.querySelector('p');
          try {
            await api.problems.create({
              slug: form.get('slug'),
              title: form.get('title'),
              difficulty: form.get('difficulty'),
              description: form.get('description'),
              starterCode: 'class Solution { }',
              constraintsText: 'See problem statement.',
              acceptanceRate: 0,
              topics: form.get('topics').split(',').map(x => x.trim()).filter(Boolean)
            });
            ui.message(msg, 'Problem created successfully.', 'success');
            f.reset();
          } catch (err) {
            ui.message(msg, ui.errorText(err));
          }
        };
      }
    } catch (err) {
      if (errorEl) {
        errorEl.style.display = 'block';
        errorEl.textContent = ui.errorText(err) + " Admin access required.";
      }
      uList.innerHTML = '<tr><td colspan="5" class="table-state error-state">Access Denied</td></tr>';
      dList.innerHTML = '<tr><td colspan="4" class="table-state error-state">Access Denied</td></tr>';
    }
  });
})();

