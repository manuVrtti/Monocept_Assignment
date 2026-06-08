// ============================================
// Users Page — Admin User Management
// ============================================

let currentPage = 0;
const pageSize = 10;

document.addEventListener('DOMContentLoaded', () => {
  if (!requireRole(['ADMIN'])) return;
  renderSidebar('users.html');
  loadUsers();
  setupCreateAgentForm();
});

async function loadUsers() {
  const container = document.getElementById('tableContainer');
  showLoading(container);

  const role = document.getElementById('filterRole').value;
  const active = document.getElementById('filterActive').value;

  const params = { page: currentPage, size: pageSize };
  if (role) params.role = role;
  if (active !== '') params.active = active;

  try {
    const data = await apiGet('/users' + buildQueryParams(params));
    const users = data.records || data.content || data || [];
    const totalPages = data.totalPages || 1;

    if (users.length === 0) {
      showEmptyState(container, 'No users found', '👥');
      document.getElementById('paginationContainer').innerHTML = '';
      return;
    }

    let html = `
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Mobile</th>
            <th>Role</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
    `;

    users.forEach(user => {
      const isActive = user.active !== false;
      html += `
        <tr>
          <td>${user.id || '—'}</td>
          <td>${escapeHtml(user.fullName || user.name || '—')}</td>
          <td>${escapeHtml(user.email || '—')}</td>
          <td>${escapeHtml(user.mobileNumber || user.mobile || '—')}</td>
          <td>${createStatusBadge(user.role || '—')}</td>
          <td>${createActiveBadge(isActive)}</td>
          <td>
            <button class="btn btn-sm ${isActive ? 'btn-danger' : 'btn-success'}" onclick="toggleUserStatus(${user.id}, ${isActive})">
              ${isActive ? 'Deactivate' : 'Activate'}
            </button>
          </td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;

    renderPagination(document.getElementById('paginationContainer'), currentPage, totalPages, (page) => {
      currentPage = page;
      loadUsers();
    });

  } catch (err) {
    showErrorState(container, 'Failed to load users');
    showToast(err.message, 'error');
  }
}

async function toggleUserStatus(userId, currentActive) {
  try {
    await apiPut(`/users/${userId}/status`, { active: !currentActive });
    showToast(`User ${currentActive ? 'deactivated' : 'activated'} successfully`, 'success');
    loadUsers();
  } catch (err) {
    showToast(err.message || 'Failed to update user status', 'error');
  }
}

function setupCreateAgentForm() {
  const form = document.getElementById('createAgentForm');
  const btn = document.getElementById('createAgentBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const name = document.getElementById('agentName').value.trim();
    const email = document.getElementById('agentEmail').value.trim();
    const password = document.getElementById('agentPassword').value;
    const mobile = document.getElementById('agentMobile').value.trim();

    let valid = true;

    if (!validateRequired(name)) {
      showFieldError(document.getElementById('agentName'), document.getElementById('agentNameError'), 'Name is required');
      valid = false;
    }
    if (!validateEmail(email)) {
      showFieldError(document.getElementById('agentEmail'), document.getElementById('agentEmailError'), 'Valid email required');
      valid = false;
    }
    if (!password || password.length < 6) {
      showFieldError(document.getElementById('agentPassword'), document.getElementById('agentPasswordError'), 'Min 6 characters');
      valid = false;
    }
    if (!validateMobile(mobile)) {
      showFieldError(document.getElementById('agentMobile'), document.getElementById('agentMobileError'), 'Valid 10-digit number required');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    try {
      await apiPost('/users/agent', { fullName: name, email, password, mobileNumber: mobile });
      showToast('Agent created successfully', 'success');
      closeModal('createAgentModal');
      currentPage = 0;
      loadUsers();
    } catch (err) {
      showToast(err.message || 'Failed to create agent', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}
