(() => {
  'use strict';
  const api = MiniForceApi, ui = MiniForce;
  const klass = d => d.toLowerCase();

  document.addEventListener('DOMContentLoaded', async () => {
    const editor = document.getElementById('code-editor');
    if (!editor) return;

    const result = document.getElementById('test-result');
    const lastRun = document.getElementById('last-run');
    const langSelect = document.getElementById('language');
    const filenameLabel = document.getElementById('editor-filename');
    const runBtn = document.getElementById('run-code');
    const submitBtn = document.getElementById('submit-code');
    const disclaimer = document.getElementById('judge-disclaimer');
    
    const slug = new URLSearchParams(location.search).get('problem') || 'two-sum';
    let problem;

    const updateEditorForLanguage = () => {
      const lang = langSelect.value;
      
      if (lang === 'PYTHON_3') {
        filenameLabel.textContent = 'solution.py';
        const draft = localStorage.getItem(`miniforce-draft-py-${problem.id}`);
        if (!problem.starterCodePython) {
            disclaimer.style.display = 'block';
            runBtn.disabled = true;
            submitBtn.disabled = true;
            editor.value = draft || '# Python support is not available for this problem yet.';
            editor.disabled = true;
        } else {
            disclaimer.style.display = 'none';
            runBtn.disabled = false;
            submitBtn.disabled = false;
            editor.value = draft || problem.starterCodePython || '';
            editor.disabled = false;
        }
      } else {
        filenameLabel.textContent = 'Solution.java';
        const draft = localStorage.getItem(`miniforce-draft-${problem.id}`);
        disclaimer.style.display = 'none';
        runBtn.disabled = false;
        submitBtn.disabled = false;
        editor.value = draft || problem.starterCode || '';
        editor.disabled = false;
      }
    };

    try {
      problem = await api.problems.getBySlug(slug);
      
      document.title = `${problem.title} - Solve on MiniForce`;
      document.getElementById('workspace-title').textContent = problem.title;
      
      const difficulty = document.getElementById('workspace-difficulty');
      difficulty.textContent = problem.difficulty;
      difficulty.className = `tag ${klass(problem.difficulty)}`;
      
      document.getElementById('workspace-topics').textContent = problem.topics.join(' \u2022 ');
      document.getElementById('workspace-prompt').textContent = problem.description;
      document.getElementById('workspace-constraints').textContent = problem.constraintsText || 'See the problem statement for constraints.';
      
      langSelect.addEventListener('change', () => {
        updateEditorForLanguage();
      });
      
      // Initialize editor with Java
      updateEditorForLanguage();

      const tests = await api.problems.tests(problem.id);
      const tcDiv = document.getElementById('workspace-testcases');
      if (tests && tests.length) {
        tcDiv.innerHTML = tests.map((t, i) => `<div class="example-box"><strong>Input:</strong>\n${ui.escape(t.inputData)}\n\n<strong>Expected Output:</strong>\n${ui.escape(t.expectedOutput)}</div>`).join('');
      } else {
        tcDiv.innerHTML = '<p class="caption">No sample test cases available.</p>';
      }
    } catch (err) {
      result.className = 'test-result error';
      result.textContent = err?.status === 404 ? 'Problem not found.' : ui.errorText(err);
      return;
    }

    editor.addEventListener('input', () => {
        const lang = langSelect.value;
        const key = lang === 'PYTHON_3' ? `miniforce-draft-py-${problem.id}` : `miniforce-draft-${problem.id}`;
        localStorage.setItem(key, editor.value);
    });

    const started = Date.now();
    const timer = document.getElementById('solve-timer');
    setInterval(() => {
      const seconds = Math.floor((Date.now() - started) / 1000);
      timer.textContent = `${String(Math.floor(seconds / 60)).padStart(2, '0')}:${String(seconds % 60).padStart(2, '0')}`;
    }, 1000);

    const contestId = new URLSearchParams(location.search).get('contest');

    const submit = async (isRun) => {
      if (!ui.requireLogin()) return;

      runBtn.disabled = true;
      submitBtn.disabled = true;

      result.className = 'test-result running';
      result.textContent = `${isRun ? 'Running' : 'Submitting'} safely...`;
      
      try {
        const req = {
            problemId: problem.id,
            language: langSelect.value,
            code: editor.value
        };
        
        if (contestId) req.contestId = Number(contestId);

        let outcome;
        if (isRun) {
          outcome = await api.submissions.run(req);
        } else {
          outcome = await api.submissions.submitAsync(req);
          let attempts = 0;
          while (attempts < 15 && ['QUEUED', 'COMPILING', 'RUNNING'].includes(outcome.status)) {
            await new Promise(r => setTimeout(r, 1000));
            outcome = await api.submissions.get(outcome.id);
            result.textContent = `Judge pending / processing (${outcome.status})...`;
            attempts++;
          }
        }

        let extraDetails = '';
        if (['QUEUED', 'COMPILING', 'RUNNING'].includes(outcome.status)) {
          extraDetails = '<br><br><strong>Timeout:</strong> The submission timed out waiting for the judge. Please check your profile later.';
        } else if (outcome.status === 'COMPILATION_ERROR' && outcome.compilerOutput) {
          extraDetails = `<br><br><strong>Compiler Output:</strong><pre class="example-box">${ui.escape(outcome.compilerOutput)}</pre>`;
        } else if (outcome.status === 'RUNTIME_ERROR' && outcome.runtimeErrorOutput) {
          extraDetails = `<br><br><strong>Runtime Error:</strong><pre class="example-box">${ui.escape(outcome.runtimeErrorOutput)}</pre>`;
        } else if (outcome.status === 'SYSTEM_ERROR' && outcome.systemErrorMessage) {
          extraDetails = `<br><br><strong>System Error:</strong><pre class="example-box">${ui.escape(outcome.systemErrorMessage)}</pre>`;
        } else if (outcome.status === 'TIME_LIMIT_EXCEEDED') {
          extraDetails = `<br><br><strong>Time Limit Exceeded:</strong> Your code took too long to execute.`;
        }

        lastRun.textContent = `Last result: ${outcome.status}${outcome.runtimeMs ? ` \u2022 ${outcome.runtimeMs}ms` : ''}`;
        result.className = `test-result ${outcome.status === 'ACCEPTED' ? 'success' : 'error'}`;
        result.innerHTML = `${ui.escape(outcome.status.replaceAll('_', ' '))}. ${isRun ? 'Tested against sample cases only.' : 'This submission was recorded. <a href="profile.html">View your profile</a>'}${extraDetails}`;
      } catch (err) {
        result.className = 'test-result error';
        result.textContent = ui.errorText(err);
      } finally {
        runBtn.disabled = false;
        submitBtn.disabled = false;
      }
    };

    runBtn.onclick = () => submit(true);
    submitBtn.onclick = () => submit(false);
  });
})();
