(() => {
  'use strict';
  const api = () => window.MiniForceApi;
  const escape = (value = '') => String(value).replace(/[&<>'"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[c]));
  const initials = (name = 'Mini Force') => name.split(/\s+/).filter(Boolean).slice(0, 2).map(x => x[0]).join('').toUpperCase();
  const message = (target, text, type = 'error') => { if (!target) return; target.className = `auth-message ${type === 'success' ? 'success' : ''}`; target.textContent = text; target.hidden = false; };
  const errorText = (err) => err?.status === 401 ? 'Your session has expired. Please log in again.' : (err?.message || 'Something went wrong. Please try again.');
  const initMobileToggle = () => {
    const toggle = document.querySelector('.mobile-toggle');
    const links = document.querySelector('.nav-links');
    if (!toggle || !links) return;
    toggle.addEventListener('click', () => {
      const open = links.classList.toggle('open');
      toggle.setAttribute('aria-expanded', String(open));
    });
  };
  const loadNav = async () => {
    const toggle = document.querySelector('.mobile-toggle');
    const navLinks = document.querySelector('.nav-links');
    if (toggle && navLinks) {
      toggle.addEventListener('click', () => {
        const isOpen = navLinks.classList.toggle('open');
        toggle.setAttribute('aria-expanded', isOpen);
      });
    }
    
    const buttons = document.querySelector('.nav-buttons'); if (!buttons || !api().token.get()) return;
    try { 
      const user = await api().auth.me(); 
      const isToggleInside = toggle && buttons.contains(toggle);
      buttons.innerHTML = `<a class="btn-outline user-nav-link" href="profile.html"><span class="nav-avatar" style="padding:0;overflow:hidden;"><img src="frontend/assets/avatars/${user.avatarKey || 'avatar_default'}.svg" style="width:100%;height:100%;border-radius:50%;"></span>Profile</a>${user.role === 'ADMIN' ? '<a class="btn-outline" href="admin.html">Admin</a>' : ''}<button class="btn-fill" type="button" data-logout>Log Out</button>${isToggleInside ? toggle.outerHTML : ''}`; 
      buttons.querySelector('[data-logout]').onclick = () => { api().token.clear(); window.location.href = 'index.html'; }; 
      // re-bind toggle if we overwrote it
      if (isToggleInside) {
        const newToggle = buttons.querySelector('.mobile-toggle');
        if (newToggle) newToggle.addEventListener('click', () => {
          const isOpen = navLinks.classList.toggle('open');
          newToggle.setAttribute('aria-expanded', isOpen);
        });
      }
    }
    catch (err) { if (err.status === 401) api().token.clear(); }
  };
  window.MiniForce = { escape, initials, message, errorText, requireLogin: () => { if (!api().token.get()) { window.location.href = 'login.html?next=' + encodeURIComponent(location.pathname.startsWith('/') ? location.pathname.slice(1) : location.pathname) + encodeURIComponent(location.search); return false; } return true; } };
  document.addEventListener('DOMContentLoaded', loadNav);
})();

