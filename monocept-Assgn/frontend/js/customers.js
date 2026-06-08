// ============================================
// Customers Page Script
// ============================================

let currentPage = 0;
const pageSize = 10;
let customerProfileExists = false;

document.addEventListener('DOMContentLoaded', () => {
  if (!requireAuth()) return;
  renderSidebar('customers.html');

  const role = getUserRole();
  if (role === 'CUSTOMER') {
    document.getElementById('pageTitle').textContent = 'My Profile';
    document.getElementById('pageSubtitle').textContent = 'Your customer profile';
    document.getElementById('customerProfileSection').style.display = 'block';
    loadMyProfile();
    setupProfileForm();
  } else {
    document.getElementById('customerListSection').style.display = 'block';
    loadCustomers();
  }
});

// ---- Customer Profile (CUSTOMER role) ----

async function loadMyProfile() {
  const card = document.getElementById('profileCard');
  showLoading(card);

  try {
    const profile = await apiGet('/customers/me');
    customerProfileExists = true;
    renderProfile(card, profile);
  } catch (err) {
    customerProfileExists = false;
    card.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">👤</div>
        <div class="empty-state-title">Profile Not Set Up</div>
        <div class="empty-state-text">You must create a customer profile before purchasing policies or raising claims.</div>
        <button class="btn btn-primary" style="margin-top:16px;" onclick="openProfileModal()">Create Profile Now</button>
      </div>
    `;
  }
}

function renderProfile(card, p) {
  card.innerHTML = `
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:0;">
      ${detailRow('Full Name', p.fullName)}
      ${detailRow('Email Address', p.email)}
      ${detailRow('Mobile Number', p.mobileNumber)}
      ${detailRow('Date of Birth', formatDate(p.dateOfBirth))}
      ${detailRow('City', p.city)}
      ${detailRow('State', p.state)}
      ${detailRow('Residential Address', p.address)}
      ${detailRow('Pin Code', p.pinCode)}
      ${detailRow('Nominee Name', p.nomineeName)}
      ${detailRow('Nominee Relation', p.nomineeRelation)}
    </div>
  `;
}

function detailRow(label, value) {
  return `
    <div class="detail-row" style="padding:16px 20px; border-bottom:1px solid var(--border-color); display:flex; flex-direction:column; gap:4px;">
      <span class="detail-label" style="font-size:12px; color:var(--text-muted); text-transform:uppercase; font-weight:500;">${label}</span>
      <span class="detail-value" style="font-size:14px; color:var(--text-primary); font-weight:500;">${escapeHtml(value) || '—'}</span>
    </div>
  `;
}

function openProfileModal() {
  if (customerProfileExists) {
    document.getElementById('profileModalTitle').textContent = 'Update Profile';
    loadProfileIntoForm();
  } else {
    document.getElementById('profileModalTitle').textContent = 'Create Profile';
  }
  openModal('editProfileModal');
}

async function loadProfileIntoForm() {
  try {
    const p = await apiGet('/customers/me');
    document.getElementById('profDob').value = p.dateOfBirth || '';
    document.getElementById('profCity').value = p.city || '';
    document.getElementById('profState').value = p.state || '';
    document.getElementById('profAddress').value = p.address || '';
    document.getElementById('profPincode').value = p.pinCode || '';
    document.getElementById('profNominee').value = p.nomineeName || '';
    document.getElementById('profNomineeRelation').value = p.nomineeRelation || '';
  } catch (err) {
    console.error("Failed to load profile details", err);
  }
}

function setupProfileForm() {
  const form = document.getElementById('profileForm');
  const btn = document.getElementById('saveProfileBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const dob = document.getElementById('profDob').value;
    const pincode = document.getElementById('profPincode').value.trim();
    const city = document.getElementById('profCity').value.trim();
    const state = document.getElementById('profState').value.trim();
    const address = document.getElementById('profAddress').value.trim();
    const nominee = document.getElementById('profNominee').value.trim();
    const nomineeRelation = document.getElementById('profNomineeRelation').value.trim();

    let valid = true;

    if (!validateRequired(dob)) {
      showFieldError(document.getElementById('profDob'), document.getElementById('profDobError'), 'Date of birth is required');
      valid = false;
    }
    if (!validateRequired(pincode) || pincode.length < 5 || pincode.length > 10) {
      showFieldError(document.getElementById('profPincode'), document.getElementById('profPincodeError'), 'Pin code must be between 5 and 10 characters');
      valid = false;
    }
    if (!validateRequired(city)) {
      showFieldError(document.getElementById('profCity'), document.getElementById('profCityError'), 'City is required');
      valid = false;
    }
    if (!validateRequired(state)) {
      showFieldError(document.getElementById('profState'), document.getElementById('profStateError'), 'State is required');
      valid = false;
    }
    if (!validateRequired(address)) {
      showFieldError(document.getElementById('profAddress'), document.getElementById('profAddressError'), 'Address is required');
      valid = false;
    }
    if (!validateRequired(nominee)) {
      showFieldError(document.getElementById('profNominee'), document.getElementById('profNomineeError'), 'Nominee name is required');
      valid = false;
    }
    if (!validateRequired(nomineeRelation)) {
      showFieldError(document.getElementById('profNomineeRelation'), document.getElementById('profNomineeRelationError'), 'Nominee relationship is required');
      valid = false;
    }

    if (!valid) return;

    const body = {
      dateOfBirth: dob,
      address,
      city,
      state,
      pinCode: pincode,
      nomineeName: nominee,
      nomineeRelation: nomineeRelation
    };

    setButtonLoading(btn, true);

    try {
      if (customerProfileExists) {
        await apiPut('/customers', body);
        showToast('Profile updated successfully', 'success');
      } else {
        await apiPost('/customers', body);
        showToast('Profile created successfully', 'success');
        customerProfileExists = true;
      }
      closeModal('editProfileModal');
      loadMyProfile();
    } catch (err) {
      showToast(err.message || 'Failed to save profile', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}

// ---- Customer List (ADMIN/AGENT) ----

async function loadCustomers() {
  const container = document.getElementById('tableContainer');
  showLoading(container);

  const params = { page: currentPage, size: pageSize };

  try {
    const data = await apiGet('/customers' + buildQueryParams(params));
    const customers = data.records || data.content || data || [];
    const totalPages = data.totalPages || 1;

    if (customers.length === 0) {
      showEmptyState(container, 'No registered customers found', '👤');
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
            <th>City</th>
            <th>State</th>
            <th>Nominee Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
    `;

    customers.forEach(c => {
      html += `
        <tr>
          <td>${c.id}</td>
          <td style="font-weight:600; color:var(--text-primary);">${escapeHtml(c.fullName)}</td>
          <td>${escapeHtml(c.email || '—')}</td>
          <td>${escapeHtml(c.mobileNumber || '—')}</td>
          <td>${escapeHtml(c.city || '—')}</td>
          <td>${escapeHtml(c.state || '—')}</td>
          <td>${escapeHtml(c.nomineeName || '—')}</td>
          <td>
            <button class="btn btn-sm btn-secondary" onclick="viewCustomer(${c.id})">View Profile</button>
          </td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;

    renderPagination(document.getElementById('paginationContainer'), currentPage, totalPages, (page) => {
      currentPage = page;
      loadCustomers();
    });

  } catch (err) {
    showErrorState(container, 'Failed to load customers list');
    showToast(err.message, 'error');
  }
}

async function viewCustomer(id) {
  const body = document.getElementById('customerDetailBody');
  body.innerHTML = '<div style="padding:40px; text-align:center;"><div class="loading-spinner" style="margin: 0 auto 12px;"></div>Loading details...</div>';
  openModal('viewCustomerModal');

  try {
    const c = await apiGet(`/customers/${id}`);
    body.innerHTML = `
      <div style="display:grid; grid-template-columns:1fr 1fr; gap:0;">
        ${detailRow('ID', c.id)}
        ${detailRow('Full Name', c.fullName)}
        ${detailRow('Email Address', c.email)}
        ${detailRow('Mobile Number', c.mobileNumber)}
        ${detailRow('Date of Birth', formatDate(c.dateOfBirth))}
        ${detailRow('City', c.city)}
        ${detailRow('State', c.state)}
        ${detailRow('Residential Address', c.address)}
        ${detailRow('Pin Code', c.pinCode)}
        ${detailRow('Nominee Name', c.nomineeName)}
        ${detailRow('Nominee Relation', c.nomineeRelation)}
      </div>
    `;
  } catch (err) {
    body.innerHTML = `<div style="padding:40px; text-align:center; color:var(--error);">Failed to load customer details: ${escapeHtml(err.message)}</div>`;
  }
}
