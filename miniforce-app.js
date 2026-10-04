(() => {
  'use strict';

  const USER_KEY = 'miniforce-demo-user';
  const codeKey = (id) => `miniforce-draft-${id}`;
  const catalog = {
    'two-sum': {
      title: 'Two Sum', difficulty: 'Easy', topics: 'Array  •  Hash Table',
      prompt: 'Given an array of integers nums and an integer target, return the indices of the two numbers whose sum is equal to target. You may assume that each input has exactly one solution.',
      example: 'Input: nums = [2, 7, 11, 15], target = 9\nOutput: [0, 1]\nExplanation: nums[0] + nums[1] = 9',
      starter: 'import java.util.*;\n\nclass Solution {\n    public int[] twoSum(int[] nums, int target) {\n        // Write your solution here\n        return new int[] {};\n    }\n}'
    },
    'valid-parentheses': {
      title: 'Valid Parentheses', difficulty: 'Easy', topics: 'Stack  •  String',
      prompt: 'Given a string containing only brackets, determine whether the input string is valid. An opening bracket must be closed by the same type of bracket in the correct order.',
      example: 'Input: s = "()[]{}"\nOutput: true',
      starter: 'import java.util.*;\n\nclass Solution {\n    public boolean isValid(String s) {\n        // Write your solution here\n        return false;\n    }\n}'
    },
    'longest-substring': {
      title: 'Longest Substring Without Repeating Characters', difficulty: 'Medium', topics: 'Hash Table  •  Sliding Window',
      prompt: 'Find the length of the longest substring without repeating characters.',
      example: 'Input: s = "abcabcbb"\nOutput: 3\nExplanation: The answer is "abc".',
      starter: 'class Solution {\n    public int lengthOfLongestSubstring(String s) {\n        // Write your solution here\n        return 0;\n    }\n}'
    }
  };

  const readUser = () => {
    try { return JSON.parse(localStorage.getItem(USER_KEY) || 'null'); } catch { return null; }
  };
  const saveUser = (user) => localStorage.setItem(USER_KEY, JSON.stringify(user));
  const initials = (name) => (name || 'Mini Force').split(/\s+/).filter(Boolean).slice(0, 2).map(part => part[0]).join('').toUpperCase();
  const slugName = (value) => value.replace(/[^a-z0-9]/gi, '').toLowerCase() || 'coder';
  const escapeHtml = (value) => String(value).replace(/[&<>'"]/g, char => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', "'":'&#39;', '"':'&quot;' }[char]));
  const localDateKey = (date) => {
    const offsetDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return offsetDate.toISOString().slice(0, 10);
  };
  const contributionLevel = (count) => count === 0 ? 0 : count === 1 ? 1 : count <= 3 ? 2 : count <= 5 ? 3 : 4;

  function previewActivity() {
    const activity = {};
    const today = new Date();
    for (let daysAgo = 0; daysAgo < 365; daysAgo += 1) {
      const pattern = (daysAgo * 19 + 11) % 29;
      if (pattern < 8) {
        const date = new Date(today);
        date.setDate(today.getDate() - daysAgo);
        activity[localDateKey(date)] = 1 + ((daysAgo * 7 + 3) % 6);
      }
    }
    return activity;
  }

  function renderNav() {
    const buttons = document.querySelector('.nav-buttons');
    const user = readUser();
    if (!buttons || !user) return;
    buttons.innerHTML = `<a class="btn-outline user-nav-link" href="profile.html"><span class="nav-avatar">${escapeHtml(initials(user.name))}</span>Profile</a><button class="btn-fill" type="button" data-logout>Log Out</button>`;
    const logout = buttons.querySelector('[data-logout]');
    logout.addEventListener('click', () => {
      localStorage.removeItem(USER_KEY);
      window.location.href = 'index.html';
    });
  }

  function formMessage(form, text, type = 'error') {
    let message = form.querySelector('.auth-message');
    if (!message) {
      message = document.createElement('p');
      message.className = 'auth-message';
      form.prepend(message);
    }
    message.className = `auth-message ${type}`;
    message.textContent = text;
  }

  function setupAuth() {
    const signupForm = document.getElementById('signup-form');
    if (signupForm) {
      signupForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const name = signupForm.querySelector('#fullname').value.trim();
        const username = signupForm.querySelector('#username').value.trim();
        const email = signupForm.querySelector('#email').value.trim();
        const password = signupForm.querySelector('#password').value;
        const confirm = signupForm.querySelector('#confirm').value;
        const agreed = signupForm.querySelector('#terms').checked;
        if (!name || !username || !email || !password) return formMessage(signupForm, 'Please complete all account fields.');
        if (password !== confirm) return formMessage(signupForm, 'Password and confirmation must match.');
        if (!agreed) return formMessage(signupForm, 'Please accept the Terms of Service to continue.');
        saveUser({ name, username: `@${username.replace(/^@/, '')}`, email, solved: 0, streak: 1, joined: new Date().toISOString() });
        formMessage(signupForm, 'Account created. Opening your profile...', 'success');
        window.setTimeout(() => { window.location.href = 'profile.html'; }, 450);
      });
    }

    const loginForm = document.getElementById('login-form');
    if (loginForm) {
      loginForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const identity = loginForm.querySelector('#email').value.trim();
        const password = loginForm.querySelector('#password').value;
        if (!identity || !password) return formMessage(loginForm, 'Enter your username/email and password.');
        let user = readUser();
        if (!user) {
          const display = identity.includes('@') ? identity.split('@')[0] : identity;
          user = { name: display.charAt(0).toUpperCase() + display.slice(1), username: `@${slugName(display)}`, email: identity, solved: 0, streak: 1, joined: new Date().toISOString() };
          saveUser(user);
        }
        formMessage(loginForm, 'Login successful. Opening your profile...', 'success');
        window.setTimeout(() => { window.location.href = 'profile.html'; }, 400);
      });
    }
  }

  function renderProfile() {
    if (!document.getElementById('profile-name')) return;
    const signedInUser = readUser();
    const user = signedInUser || { name: 'Aditya Deshmukh', username: '@binary_aditya', solved: 428, streak: 43, activity: previewActivity() };
    const solved = Number(user.solved || 0);
    const activity = document.getElementById('activity-grid');
    document.getElementById('profile-name').textContent = user.name;
    document.getElementById('profile-handle').textContent = user.username;
    document.getElementById('profile-avatar').textContent = initials(user.name);
    document.getElementById('profile-bio').textContent = 'MiniForce coder. Building a stronger problem-solving streak one submission at a time.';
    document.getElementById('solved-count').textContent = solved;
    document.getElementById('streak-count').textContent = user.streak || 1;
    const contributions = user.activity || {};
    const totalContributions = Object.values(contributions).reduce((sum, count) => sum + Number(count || 0), 0);
    document.getElementById('activity-caption').textContent = `${totalContributions} contribution${totalContributions === 1 ? '' : 's'} in the last year`;
    document.getElementById('easy-progress').textContent = `${solved} / 682`;
    document.getElementById('easy-bar').style.width = `${Math.min(100, Math.max(4, solved / 6.82))}%`;
    if (!activity) return;
    const months = document.getElementById('activity-months');
    const tooltip = document.getElementById('activity-tooltip');
    const displayDate = new Intl.DateTimeFormat(undefined, { day: 'numeric', month: 'short', year: 'numeric' });
    const dates = [];
    const today = new Date();
    for (let daysAgo = 364; daysAgo >= 0; daysAgo -= 1) {
      const date = new Date(today);
      date.setDate(today.getDate() - daysAgo);
      dates.push(date);
    }
    activity.innerHTML = '';
    if (months) {
      months.innerHTML = '';
      let shownMonth = '';
      dates.forEach((date, index) => {
        const name = date.toLocaleDateString(undefined, { month: 'short' });
        if (date.getDate() <= 7 && name !== shownMonth) {
          const label = document.createElement('span');
          label.textContent = name;
          label.style.gridColumnStart = String(Math.floor(index / 7) + 1);
          months.appendChild(label);
          shownMonth = name;
        }
      });
    }
    const setTooltip = (date, count) => {
      if (!tooltip) return;
      tooltip.textContent = count ? `${count} accepted submission${count === 1 ? '' : 's'} on ${displayDate.format(date)}` : `No accepted submissions on ${displayDate.format(date)}`;
    };
    dates.forEach((date) => {
      const key = localDateKey(date);
      const count = Number(contributions[key] || 0);
      const cell = document.createElement('button');
      cell.type = 'button';
      cell.className = `activity-cell${count ? ` lv${contributionLevel(count)}` : ''}`;
      cell.setAttribute('aria-label', `${count} accepted submissions on ${displayDate.format(date)}`);
      cell.title = `${count} accepted submissions on ${displayDate.format(date)}`;
      cell.addEventListener('mouseenter', () => setTooltip(date, count));
      cell.addEventListener('focus', () => setTooltip(date, count));
      cell.addEventListener('click', () => setTooltip(date, count));
      activity.appendChild(cell);
    });
    setTooltip(today, Number(contributions[localDateKey(today)] || 0));
  }

  function setupWorkspace() {
    const editor = document.getElementById('code-editor');
    if (!editor) return;
    const params = new URLSearchParams(window.location.search);
    const problemId = params.get('problem') || 'two-sum';
    const problem = catalog[problemId] || catalog['two-sum'];
    document.title = `${problem.title} - Solve on MiniForce`;
    document.getElementById('workspace-title').textContent = problem.title;
    document.getElementById('workspace-difficulty').textContent = problem.difficulty;
    document.getElementById('workspace-difficulty').className = `tag ${problem.difficulty.toLowerCase()}`;
    document.getElementById('workspace-topics').textContent = problem.topics;
    document.getElementById('workspace-prompt').textContent = problem.prompt;
    document.getElementById('workspace-example').textContent = problem.example;
    editor.value = localStorage.getItem(codeKey(problemId)) || problem.starter;
    editor.addEventListener('input', () => localStorage.setItem(codeKey(problemId), editor.value));

    const startedAt = Date.now();
    const timer = document.getElementById('solve-timer');
    const formatElapsed = () => {
      const elapsed = Math.floor((Date.now() - startedAt) / 1000);
      const minutes = String(Math.floor(elapsed / 60)).padStart(2, '0');
      const seconds = String(elapsed % 60).padStart(2, '0');
      timer.textContent = `${minutes}:${seconds}`;
      return elapsed;
    };
    window.setInterval(formatElapsed, 1000);
    formatElapsed();

    const result = document.getElementById('test-result');
    const lastRun = document.getElementById('last-run');
    const execute = (submitted) => {
      if (editor.value.trim().length < 20) {
        result.className = 'test-result error';
        result.textContent = 'Add your solution before running the sample tests.';
        return;
      }
      result.className = 'test-result running';
      result.textContent = submitted ? 'Submitting solution...' : 'Running 3 sample test cases...';
      window.setTimeout(() => {
        const runTime = (0.014 + (editor.value.length % 31) / 1000).toFixed(3);
        lastRun.textContent = `Last execution: ${runTime}s`;
        result.className = 'test-result success';
        if (submitted) {
          const user = readUser() || { name: 'MiniForce Coder', username: '@coder', solved: 0, streak: 1, joined: new Date().toISOString() };
          const solvedIds = user.solvedIds || [];
          if (!solvedIds.includes(problemId)) {
            solvedIds.push(problemId);
            user.solved = Number(user.solved || 0) + 1;
          }
          user.solvedIds = solvedIds;
          user.activity = user.activity || {};
          const today = localDateKey(new Date());
          user.activity[today] = Number(user.activity[today] || 0) + 1;
          user.streak = Math.max(Number(user.streak || 1), 1);
          saveUser(user);
          result.innerHTML = `Accepted! All test cases passed. <a href="profile.html">View your updated profile</a>`;
        } else {
          result.textContent = 'Passed 3 / 3 sample test cases.';
        }
      }, submitted ? 750 : 520);
    };
    document.getElementById('run-code').addEventListener('click', () => execute(false));
    document.getElementById('submit-code').addEventListener('click', () => execute(true));
  }

  document.addEventListener('DOMContentLoaded', () => {
    renderNav();
    setupAuth();
    renderProfile();
    setupWorkspace();
  });
})();
