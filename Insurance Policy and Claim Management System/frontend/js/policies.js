// ============================================
// Policies Page Script
// ============================================

let currentPage = 0;
const pageSize = 10;
let plansList = [];
let customersList = [];

document.addEventListener('DOMContentLoaded', () => {
  if (!requireAuth()) return;
  
  const role = getUserRole();
  renderSidebar('policies.html');
  
  if (role === 'CUSTOMER') {
    document.getElementById('purchasePolicyBtn').style.display = 'inline-flex';
    document.getElementById('pageHeadingTitle').textContent = 'My Policies';
    document.getElementById('pageHeadingSub').textContent = 'Review and manage your purchased insurance coverage';
    setupPurchaseForm();
  } else {
    document.getElementById('issuePolicyBtn').style.display = 'inline-flex';
    document.getElementById('pageHeadingTitle').textContent = 'All Policies';
    document.getElementById('pageHeadingSub').textContent = 'Review and manage all customer policies';
    setupIssueForm();
    loadCustomersDropdown();
  }
  
  loadPlansDropdown();
  loadPolicies();
});

async function loadPlansDropdown() {
  try {
    const data = await apiGet('/plans?active=true&size=100');
    plansList = data.records || data.content || data || [];
    
    const purchasePlanSelect = document.getElementById('purchasePlan');
    const issuePlanSelect = document.getElementById('issuePlan');
    
    plansList.forEach(plan => {
      const opt = document.createElement('option');
      opt.value = plan.id;
      opt.textContent = `${plan.planName} (${plan.productName}) - Premium: ${formatCurrency(plan.premiumAmount)}`;
      
      if (purchasePlanSelect) purchasePlanSelect.appendChild(opt.cloneNode(true));
      if (issuePlanSelect) issuePlanSelect.appendChild(opt);
    });
  } catch (err) {
    console.error("Failed to load plans for dropdown", err);
  }
}

async function loadCustomersDropdown() {
  try {
    const data = await apiGet('/customers?size=100');
    customersList = data.records || data.content || data || [];
    
    const issueCustSelect = document.getElementById('issueCustomer');
    
    customersList.forEach(cust => {
      const opt = document.createElement('option');
      opt.value = cust.id;
      opt.textContent = `${cust.fullName || cust.name || 'ID: ' + cust.id} (${cust.email || ''})`;
      if (issueCustSelect) issueCustSelect.appendChild(opt);
    });
  } catch (err) {
    console.error("Failed to load customers for dropdown", err);
  }
}

async function loadPolicies() {
  const container = document.getElementById('tableContainer');
  showLoading(container);

  const status = document.getElementById('filterStatus').value;
  const role = getUserRole();

  const params = { page: currentPage, size: pageSize, sortField: 'createdDate', sortDirection: 'desc' };
  if (status) params.policyStatus = status;

  try {
    const response = await apiGet('/policies' + buildQueryParams(params));
    const policies = response.records || response.content || response || [];
    const totalPages = response.totalPages || 1;

    if (policies.length === 0) {
      showEmptyState(container, 'No policies found', '🛡️');
      document.getElementById('paginationContainer').innerHTML = '';
      return;
    }

    let html = `
      <table class="data-table">
        <thead>
          <tr>
            <th>Policy Number</th>
            ${role !== 'CUSTOMER' ? '<th>Customer</th>' : ''}
            <th>Plan Name</th>
            <th>Type</th>
            <th>Premium</th>
            <th>Duration</th>
            <th>Start Date</th>
            <th>End Date</th>
            <th>Status</th>
            <th>Paid Premium</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
    `;

    policies.forEach(policy => {
      let actionsHtml = '';
      const canCancel = (policy.policyStatus === 'ACTIVE' || policy.policyStatus === 'PENDING_PAYMENT');
      
      if (role === 'CUSTOMER' && policy.policyStatus === 'PENDING_PAYMENT') {
        actionsHtml = `
          <td>
            <a class="btn btn-sm btn-primary" href="payments.html?policyId=${policy.id}&amount=${policy.premiumAmount}">
              Pay Now
            </a>
          </td>
        `;
      } else if (role !== 'CUSTOMER' && canCancel) {
        actionsHtml = `
          <td>
            <button class="btn btn-sm btn-danger" onclick="cancelPolicy(${policy.id})">
              Cancel
            </button>
          </td>
        `;
      } else {
        actionsHtml = `<td>—</td>`;
      }

      html += `
        <tr>
          <td style="font-weight:600; color:var(--text-primary);">${policy.policyNumber}</td>
          ${role !== 'CUSTOMER' ? `<td>${escapeHtml(policy.customerName || '—')}</td>` : ''}
          <td>${escapeHtml(policy.planName)}</td>
          <td>${createStatusBadge(policy.productType)}</td>
          <td style="font-weight:500;">${formatCurrency(policy.premiumAmount)} (${createStatusBadge(policy.premiumType)})</td>
          <td>${policy.endDate ? new Date(policy.endDate).getFullYear() - new Date(policy.startDate).getFullYear() : '—'} Yrs</td>
          <td>${formatDate(policy.startDate)}</td>
          <td>${formatDate(policy.endDate)}</td>
          <td>${createStatusBadge(policy.policyStatus)}</td>
          <td style="font-weight:600; color:#10b981;">${formatCurrency(policy.totalPremiumPaid)}</td>
          ${actionsHtml}
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;

    renderPagination(document.getElementById('paginationContainer'), currentPage, totalPages, (page) => {
      currentPage = page;
      loadPolicies();
    });

  } catch (err) {
    showErrorState(container, 'Failed to load policies');
    showToast(err.message, 'error');
  }
}

function openPurchaseModal() {
  document.getElementById('purchasePlan').value = '';
  // Default start date is today
  document.getElementById('purchaseStartDate').value = new Date().toISOString().substring(0, 10);
  openModal('purchaseModal');
}

function openIssueModal() {
  document.getElementById('issueCustomer').value = '';
  document.getElementById('issuePlan').value = '';
  document.getElementById('issueStartDate').value = new Date().toISOString().substring(0, 10);
  openModal('issueModal');
}

async function cancelPolicy(id) {
  if (!confirm('Are you sure you want to cancel this policy? This action cannot be undone.')) return;
  
  try {
    await apiPut(`/policies/${id}/cancel`);
    showToast('Policy cancelled successfully', 'success');
    loadPolicies();
  } catch (err) {
    showToast(err.message || 'Failed to cancel policy', 'error');
  }
}

function setupPurchaseForm() {
  const form = document.getElementById('purchaseForm');
  const btn = document.getElementById('submitPurchaseBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const planId = document.getElementById('purchasePlan').value;
    const startDate = document.getElementById('purchaseStartDate').value;

    let valid = true;
    if (!validateRequired(planId)) {
      showFieldError(document.getElementById('purchasePlan'), document.getElementById('purchasePlanError'), 'Please select a plan');
      valid = false;
    }
    if (!validateRequired(startDate)) {
      showFieldError(document.getElementById('purchaseStartDate'), document.getElementById('purchaseStartDateError'), 'Start date is required');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    try {
      await apiPost('/policies/purchase', {
        planId: parseInt(planId),
        startDate: startDate
      });
      showToast('Policy purchased successfully! Please make a payment to activate it.', 'success');
      closeModal('purchaseModal');
      loadPolicies();
    } catch (err) {
      showToast(err.message || 'Failed to purchase policy', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}

function setupIssueForm() {
  const form = document.getElementById('issueForm');
  const btn = document.getElementById('submitIssueBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const customerId = document.getElementById('issueCustomer').value;
    const planId = document.getElementById('issuePlan').value;
    const startDate = document.getElementById('issueStartDate').value;

    let valid = true;
    if (!validateRequired(customerId)) {
      showFieldError(document.getElementById('issueCustomer'), document.getElementById('issueCustomerError'), 'Please select a customer');
      valid = false;
    }
    if (!validateRequired(planId)) {
      showFieldError(document.getElementById('issuePlan'), document.getElementById('issuePlanError'), 'Please select a plan');
      valid = false;
    }
    if (!validateRequired(startDate)) {
      showFieldError(document.getElementById('issueStartDate'), document.getElementById('issueStartDateError'), 'Start date is required');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    try {
      await apiPost('/policies/issue', {
        customerId: parseInt(customerId),
        planId: parseInt(planId),
        startDate: startDate
      });
      showToast('Policy issued successfully to customer', 'success');
      closeModal('issueModal');
      loadPolicies();
    } catch (err) {
      showToast(err.message || 'Failed to issue policy', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}
