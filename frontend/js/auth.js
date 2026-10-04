(() => { 'use strict'; const api = MiniForceApi, ui = MiniForce; const next = new URLSearchParams(location.search).get('next') || 'profile.html'; const redirect = () => { location.href = next.startsWith('/') ? next.slice(1) : next; };
  document.addEventListener('DOMContentLoaded', () => {
    const signup = document.getElementById('signup-form'); const login = document.getElementById('login-form');
    
    if (signup) signup.addEventListener('submit', async (event) => { 
      event.preventDefault(); 
      const msg = signup.querySelector('.auth-message') || document.createElement('p'); 
      if (!msg.parentElement) signup.prepend(msg); 
      
      const submitBtn = signup.querySelector('button[type="submit"]');
      
      const fullName = signup.querySelector('#fullname').value.trim(), 
            username = signup.querySelector('#username').value.trim(), 
            email = signup.querySelector('#email').value.trim(), 
            password = signup.querySelector('#password').value, 
            confirm = signup.querySelector('#confirm').value; 
            
      if (!signup.querySelector('#terms').checked) return ui.message(msg, 'Please accept the Terms of Service.'); 
      if (password !== confirm) return ui.message(msg, 'Password and confirmation must match.'); 
      
      if (submitBtn) submitBtn.disabled = true;
      try { 
        const result = await api.auth.register({ fullName, username, email, password }); 
        api.token.set(result.token); 
        ui.message(msg, 'Account created. Opening your profile...', 'success'); 
        setTimeout(redirect, 350); 
      } catch (err) { 
        ui.message(msg, ui.errorText(err)); 
        if (submitBtn) submitBtn.disabled = false;
      } 
    });
    
    if (login) login.addEventListener('submit', async (event) => { 
      event.preventDefault(); 
      const msg = login.querySelector('.auth-message') || document.createElement('p'); 
      if (!msg.parentElement) login.prepend(msg); 
      
      const submitBtn = login.querySelector('button[type="submit"]');
      if (submitBtn) submitBtn.disabled = true;
      
      try { 
        const identityInput = login.querySelector('#email') || login.querySelector('#username');
        const result = await api.auth.login({ 
          identity: identityInput.value.trim(), 
          password: login.querySelector('#password').value 
        }); 
        api.token.set(result.token); 
        ui.message(msg, 'Login successful. Opening your account...', 'success'); 
        setTimeout(redirect, 300); 
      } catch (err) { 
        ui.message(msg, ui.errorText(err)); 
        if (submitBtn) submitBtn.disabled = false;
      } 
    });
  });
})();
