// ============================================
// Plans Page Script
// ============================================

let currentPage = 0;
const pageSize = 10;
let isEditMode = false;
let productsList = [];

document.addEventListener('DOMContentLoaded', () => {
  if (!requireAuth()) return;
  
  const role = getUserRole();
  renderSidebar('plans.html');
  
  if (role === 'ADMIN') {
    const createBtn = document.getElementById('createPlanBtn');
    if (createBtn) createBtn.style.display = 'inline-flex';
  }
  
  loadProductsDropdown();
  loadPlans();
  setupPlanForm();
});

async function loadProductsDropdown() {
  try {
    // Load active products for dropdown selection
    const data = await apiGet('/products?active=true&size=100');
    productsList = data.records || data.content || data || [];
    
    const filterSelect = document.getElementById('filterProduct');
    const formSelect = document.getElementById('planProduct');
    
    productsList.forEach(prod => {
      // Populate filter dropdown
      const optFilter = document.createElement('option');
      optFilter.value = prod.id;
      optFilter.textContent = prod.productName;
      filterSelect.appendChild(optFilter);

      // Populate form dropdown
      const optForm = document.createElement('option');
      optForm.value = prod.id;
      optForm.textContent = prod.productName;
      formSelect.appendChild(optForm);
    });
  } catch (err) {
    log.error("Failed to load products for dropdown selection", err);
  }
}

async function loadPlans() {
  const container = document.getElementById('tableContainer');
  showLoading(container);

  const productId = document.getElementById('filterProduct').value;
  const active = document.getElementById('filterActive').value;

  const params = { page: currentPage, size: pageSize, sortField: 'createdDate', sortDirection: 'desc' };
  if (productId) params.productId = productId;
  if (active !== '') params.active = active;

  try {
    const response = await apiGet('/plans' + buildQueryParams(params));
    const plans = response.records || response.content || response || [];
    const totalPages = response.totalPages || 1;
    const role = getUserRole();

    if (plans.length === 0) {
      showEmptyState(container, 'No plans found', '📋');
      document.getElementById('paginationContainer').innerHTML = '';
      return;
    }

    let html = `
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Plan Name</th>
            <th>Product</th>
            <th>Coverage Amount</th>
            <th>Premium Amount</th>
            <th>Type</th>
            <th>Duration</th>
            <th>Status</th>
            ${role === 'ADMIN' ? '<th>Actions</th>' : ''}
          </tr>
        </thead>
        <tbody>
    `;

    plans.forEach(plan => {
      const isActive = plan.active !== false;
      let actionsHtml = '';
      
      if (role === 'ADMIN') {
        actionsHtml = `
          <td>
            <div style="display:flex; gap:8px;">
              <button class="btn btn-sm btn-secondary" onclick="openEditPlanModal(${plan.id}, ${plan.productId || 0}, '${escapeHtml(plan.planName)}', ${plan.coverageAmount}, ${plan.premiumAmount}, '${plan.premiumType}', ${plan.duration}, '${escapeHtml(plan.termsAndConditions)}', ${isActive})">
                Edit
              </button>
              ${isActive ? `
                <button class="btn btn-sm btn-danger" onclick="deactivatePlan(${plan.id})">
                  Deactivate
                </button>
              ` : ''}
            </div>
          </td>
        `;
      }

      html += `
        <tr>
          <td>${plan.id}</td>
          <td style="font-weight:600; color:var(--text-primary);">${escapeHtml(plan.planName)}</td>
          <td>${escapeHtml(plan.productName || '—')}</td>
          <td style="font-weight:500; color:#10b981;">${formatCurrency(plan.coverageAmount)}</td>
          <td style="font-weight:500; color:#f59e0b;">${formatCurrency(plan.premiumAmount)}</td>
          <td>${createStatusBadge(plan.premiumType)}</td>
          <td>${plan.duration} ${plan.duration === 1 ? 'Year' : 'Years'}</td>
          <td>${createActiveBadge(isActive)}</td>
          ${actionsHtml}
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;

    renderPagination(document.getElementById('paginationContainer'), currentPage, totalPages, (page) => {
      currentPage = page;
      loadPlans();
    });

  } catch (err) {
    showErrorState(container, 'Failed to load plans');
    showToast(err.message, 'error');
  }
}

function openCreatePlanModal() {
  isEditMode = false;
  document.getElementById('modalTitle').textContent = 'Create Plan';
  document.getElementById('planId').value = '';
  document.getElementById('planProduct').value = '';
  document.getElementById('planProduct').disabled = false;
  document.getElementById('planName').value = '';
  document.getElementById('coverageAmount').value = '';
  document.getElementById('premiumAmount').value = '';
  document.getElementById('premiumType').value = '';
  document.getElementById('planDuration').value = '';
  document.getElementById('termsAndConditions').value = '';
  document.getElementById('planActive').checked = true;
  document.getElementById('planActive').parentElement.style.display = 'flex';
  
  openModal('planModal');
}

function openEditPlanModal(id, productId, name, coverage, premium, type, duration, terms, active) {
  isEditMode = true;
  document.getElementById('modalTitle').textContent = 'Edit Plan';
  document.getElementById('planId').value = id;
  
  // Prefill fields
  const prodSelect = document.getElementById('planProduct');
  prodSelect.value = productId;
  prodSelect.disabled = true; // Product cannot be changed during edit
  
  document.getElementById('planName').value = name;
  document.getElementById('coverageAmount').value = coverage;
  document.getElementById('premiumAmount').value = premium;
  document.getElementById('premiumType').value = type;
  document.getElementById('planDuration').value = duration;
  document.getElementById('termsAndConditions').value = terms;
  document.getElementById('planActive').checked = active;
  
  document.getElementById('planActive').parentElement.style.display = 'flex';
  openModal('planModal');
}

async function deactivatePlan(id) {
  if (!confirm('Are you sure you want to deactivate this plan?')) return;
  
  try {
    await apiPut(`/plans/${id}/deactivate`);
    showToast('Plan deactivated successfully', 'success');
    loadPlans();
  } catch (err) {
    showToast(err.message || 'Failed to deactivate plan', 'error');
  }
}

function setupPlanForm() {
  const form = document.getElementById('planForm');
  const btn = document.getElementById('savePlanBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const id = document.getElementById('planId').value;
    const productId = document.getElementById('planProduct').value;
    const name = document.getElementById('planName').value.trim();
    const coverage = parseFloat(document.getElementById('coverageAmount').value);
    const premium = parseFloat(document.getElementById('premiumAmount').value);
    const type = document.getElementById('premiumType').value;
    const duration = parseInt(document.getElementById('planDuration').value);
    const terms = document.getElementById('termsAndConditions').value.trim();
    const active = document.getElementById('planActive').checked;

    let valid = true;

    if (!isEditMode && !validateRequired(productId)) {
      showFieldError(document.getElementById('planProduct'), document.getElementById('planProductError'), 'Product selection is required');
      valid = false;
    }
    if (!validateRequired(name)) {
      showFieldError(document.getElementById('planName'), document.getElementById('planNameError'), 'Plan name is required');
      valid = false;
    }
    if (isNaN(coverage) || coverage <= 0) {
      showFieldError(document.getElementById('coverageAmount'), document.getElementById('coverageAmountError'), 'Coverage amount must be greater than 0');
      valid = false;
    }
    if (isNaN(premium) || premium <= 0) {
      showFieldError(document.getElementById('premiumAmount'), document.getElementById('premiumAmountError'), 'Premium amount must be greater than 0');
      valid = false;
    }
    if (valid && coverage <= premium) {
      showFieldError(document.getElementById('coverageAmount'), document.getElementById('coverageAmountError'), 'Coverage must be greater than Premium');
      valid = false;
    }
    if (!validateRequired(type)) {
      showFieldError(document.getElementById('premiumType'), document.getElementById('premiumTypeError'), 'Premium type is required');
      valid = false;
    }
    if (isNaN(duration) || duration <= 0) {
      showFieldError(document.getElementById('planDuration'), document.getElementById('planDurationError'), 'Duration must be at least 1 year');
      valid = false;
    }
    if (!validateRequired(terms)) {
      showFieldError(document.getElementById('termsAndConditions'), document.getElementById('termsAndConditionsError'), 'Terms & conditions are required');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    const body = {
      productId: parseInt(productId),
      planName: name,
      coverageAmount: coverage,
      premiumAmount: premium,
      premiumType: type,
      duration: duration,
      termsAndConditions: terms,
      active: active
    };

    try {
      if (isEditMode) {
        await apiPut(`/plans/${id}`, body);
        showToast('Plan updated successfully', 'success');
      } else {
        await apiPost('/plans', body);
        showToast('Plan created successfully', 'success');
      }
      closeModal('planModal');
      loadPlans();
    } catch (err) {
      showToast(err.message || 'Failed to save plan', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}
