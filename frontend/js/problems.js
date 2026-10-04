(() => { 'use strict'; const api = MiniForceApi, ui = MiniForce; const difficultyClass = d => d.toLowerCase();
  document.addEventListener('DOMContentLoaded', () => { 
    const tbody = document.getElementById('problem-rows'); if (!tbody) return; 
    const search = document.getElementById('problem-search'); 
    const count = document.getElementById('problem-count'); 
    const topicContainer = document.getElementById('topic-filters');
    
    let allProblems = [];
    let topicsRendered = false;

    const renderTopics = () => {
        if (topicsRendered || !topicContainer || !allProblems.length) return;
        const topicCounts = {};
        allProblems.forEach(p => p.topics.forEach(t => { topicCounts[t] = (topicCounts[t] || 0) + 1; }));
        const sortedTopics = Object.keys(topicCounts).sort((a,b) => topicCounts[b] - topicCounts[a]);
        topicContainer.innerHTML = '<span class="filter-label">Topic</span>' + sortedTopics.map(t => 
            `<label class="check-item"><input data-topic="${ui.escape(t)}" type="checkbox"> ${ui.escape(t)} <span style="color:var(--text-muted);font-size:0.8rem">(${topicCounts[t]})</span></label>`
        ).join('');
        topicContainer.querySelectorAll('input').forEach(x => x.addEventListener('change', renderTable));
        topicsRendered = true;
    };

    const renderTable = () => {
        const selectedDiff = [...document.querySelectorAll('[data-difficulty]:checked')].map(x => x.dataset.difficulty); 
        const selectedTopics = [...document.querySelectorAll('[data-topic]:checked')].map(x => x.dataset.topic.toLowerCase()); 
        
        const filtered = allProblems.filter(p => 
            (!selectedDiff.length || selectedDiff.includes(p.difficulty)) && 
            (!selectedTopics.length || p.topics.some(t => selectedTopics.includes(t.toLowerCase()))) &&
            (!search.value.trim() || p.title.toLowerCase().includes(search.value.trim().toLowerCase()) || p.topics.some(t => t.toLowerCase().includes(search.value.trim().toLowerCase())))
        ); 
        
        count.textContent = `Showing ${filtered.length} challenge${filtered.length === 1 ? '' : 's'}`; 
        
        tbody.innerHTML = filtered.length ? filtered.map(p => 
            `<tr>
                <td>${p.solved === true ? '<span style="color:var(--green)">&#10004;</span>' : ''}</td>
                                <td><a class="problem-title" href="solve.html?problem=${encodeURIComponent(p.slug)}">${ui.escape(p.title)}</a> ${p.isReal ? '<span class="tag" style="border-color:var(--blue);color:var(--blue);font-size:10px;padding:1px 6px;margin-left:8px;">REAL</span>' : ''}</td>
                <td><div class="topic-list">${p.topics.map(t => `<span class="topic">${ui.escape(t)}</span>`).join('')}</div></td>
                <td>${Number(p.acceptanceRate).toFixed(1)}%</td>
                <td><span class="tag ${difficultyClass(p.difficulty)}">${ui.escape(p.difficulty)}</span></td>
            </tr>`
        ).join('') : '<tr><td colspan="5" class="table-state">No problems match those filters.</td></tr>'; 
    };

    const fetchProblems = async () => { 
        tbody.innerHTML = '<tr><td colspan="5" class="table-state">Loading challenges...</td></tr>'; 
        try { 
                        allProblems = await api.problems.list({}); 
            const realIds = [1, 2, 3, 8, 12, 13, 14, 20];
            allProblems.sort((a, b) => {
                const aIdx = realIds.indexOf(a.id);
                const bIdx = realIds.indexOf(b.id);
                if (aIdx !== -1 && bIdx !== -1) return aIdx - bIdx;
                if (aIdx !== -1) return -1;
                if (bIdx !== -1) return 1;
                return a.id - b.id;
            });
            allProblems.forEach(p => {
                if (realIds.includes(p.id)) p.isReal = true;
            }); 
            renderTopics();
            renderTable();
        } catch (err) { 
            count.textContent = 'API unavailable'; 
            tbody.innerHTML = `<tr><td colspan="5" class="table-state error-state">${ui.escape(ui.errorText(err))}</td></tr>`; 
        } 
    };
    
    search.addEventListener('input', () => { clearTimeout(search._timer); search._timer = setTimeout(renderTable, 250); }); 
    document.querySelectorAll('[data-difficulty]').forEach(x => x.addEventListener('change', renderTable)); 
    fetchProblems();
  });
})();

