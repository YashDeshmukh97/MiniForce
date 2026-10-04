(() => { 'use strict'; const api = MiniForceApi, ui = MiniForce; const klass = d => d.toLowerCase();
  document.addEventListener('DOMContentLoaded', async () => {
    const title = document.getElementById('contest-title');
    const desc = document.getElementById('contest-desc');
    const tbody = document.getElementById('contest-problems');
    const lb = document.getElementById('contest-leaderboard');
    if (!title) return;
    const cid = new URLSearchParams(location.search).get('id');
    if (!cid) { title.textContent = 'Contest not found'; return; }
    try {
      const c = await api.contests.get(cid);
      title.textContent = c.title;
      desc.textContent = c.description || (c.rated ? 'Rated' : 'Practice');
      document.title = c.title + ' - MiniForce';
      
      try {
        const probs = await api.contests.problems(cid);
        tbody.innerHTML = probs.length ? probs.map(p => `<tr><td><a class="problem-title" href="solve.html?problem=${encodeURIComponent(p.slug)}&contest=${cid}">${ui.escape(p.title)}</a></td><td><span class="tag ${klass(p.difficulty)}">${ui.escape(p.difficulty)}</span></td><td>${p.points}</td><td><a class="btn-outline btn-small" href="solve.html?problem=${encodeURIComponent(p.slug)}&contest=${cid}">Solve</a></td></tr>`).join('') : '<tr><td colspan="4" class="table-state">No problems listed for this contest.</td></tr>';
      } catch(err) {
        tbody.innerHTML = `<tr><td colspan="4" class="table-state error-state">${ui.escape(ui.errorText(err))}</td></tr>`;
      }
      
      try {
        const ranks = await api.contests.leaderboard(cid);
        lb.innerHTML = ranks.length ? '<table class="data-table rank-table"><thead><tr><th>Rank</th><th>User</th><th>Score</th></tr></thead><tbody>' + ranks.map((r, i) => `<tr><td class="rank">#${i + 1}</td><td><a class="problem-title" href="profile.html?user=${encodeURIComponent(r.user.username)}">${ui.escape(r.user.username)}</a></td><td class="rating">${r.score}</td></tr>`).join('') + '</tbody></table>' : '<p class="table-state">No participants yet.</p>';
      } catch(err) {
        lb.innerHTML = `<p class="table-state error-state">${ui.escape(ui.errorText(err))}</p>`;
      }
    } catch(err) {
      title.textContent = 'Error loading contest';
      desc.textContent = ui.errorText(err);
    }
  });
})();
