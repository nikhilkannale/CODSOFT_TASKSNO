/**
 * Thin fetch wrapper for the Quiz Application REST API.
 * Authentication is carried by the HttpOnly "quizapp_token" cookie set by
 * /api/auth/login and /api/auth/register, so every request just needs
 * credentials: 'same-origin' (the default) -- no manual header wiring needed.
 */
const Api = (() => {
  async function request(path, options = {}) {
    const res = await fetch(path, {
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
      ...options,
    });

    if (res.status === 401) {
      // Session missing/expired -- send the user back to login.
      if (!location.pathname.startsWith('/login')) {
        location.href = '/login';
      }
      throw new Error('Unauthorized');
    }

    let body = null;
    const text = await res.text();
    if (text) {
      try { body = JSON.parse(text); } catch (e) { body = text; }
    }

    if (!res.ok) {
      const message = (body && body.message) ? body.message : `Request failed (${res.status})`;
      const error = new Error(message);
      error.status = res.status;
      error.details = body && body.details;
      throw error;
    }
    return body;
  }

  return {
    get: (path) => request(path, { method: 'GET' }),
    post: (path, data) => request(path, { method: 'POST', body: JSON.stringify(data) }),
    put: (path, data) => request(path, { method: 'PUT', body: JSON.stringify(data) }),
    patch: (path, data) => request(path, { method: 'PATCH', body: JSON.stringify(data) }),
    del: (path) => request(path, { method: 'DELETE' }),
  };
})();

/** Renders a dismissible Bootstrap alert into a container element. */
function showAlert(containerId, message, type = 'danger') {
  const container = document.getElementById(containerId);
  if (!container) { alert(message); return; }
  container.innerHTML = `
    <div class="alert alert-${type} alert-dismissible fade show" role="alert">
      ${escapeHtml(message)}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>`;
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}

function formatSeconds(totalSeconds) {
  const s = Math.max(0, Math.floor(totalSeconds));
  const mm = Math.floor(s / 60).toString().padStart(2, '0');
  const ss = (s % 60).toString().padStart(2, '0');
  return `${mm}:${ss}`;
}

function formatDate(isoString) {
  if (!isoString) return '—';
  const d = new Date(isoString);
  return d.toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
}

/** Populates the shared navbar with the current user's name/role, or shows guest links. */
async function hydrateNav() {
  const authArea = document.getElementById('navAuthArea');
  if (!authArea) return;
  try {
    const me = await Api.get('/api/users/me');
    const isAdmin = me.role === 'ADMIN';
    authArea.innerHTML = `
      <span class="badge badge-role me-2 align-self-center">${me.role}</span>
      <span class="nav-link disabled text-white-50 d-none d-md-inline">${escapeHtml(me.fullName)}</span>
      <button class="btn btn-sm btn-outline-light ms-2" id="logoutBtn">Log out</button>
    `;
    document.getElementById('logoutBtn').addEventListener('click', async () => {
      await Api.post('/api/auth/logout', {});
      location.href = '/login';
    });

    const adminLinks = document.getElementById('adminNavLinks');
    const studentLinks = document.getElementById('studentNavLinks');
    if (adminLinks) adminLinks.classList.toggle('d-none', !isAdmin);
    if (studentLinks) studentLinks.classList.toggle('d-none', isAdmin);
  } catch (e) {
    authArea.innerHTML = `
      <a class="nav-link" href="/login">Log in</a>
      <a class="btn btn-sm btn-amber ms-2" href="/register">Sign up</a>
    `;
  }
}

document.addEventListener('DOMContentLoaded', hydrateNav);
