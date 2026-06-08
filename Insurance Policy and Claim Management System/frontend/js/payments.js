// ============================================
// Payments Page Script
// ============================================

let currentPage = 0;
const pageSize = 10;
let policiesList = [];

document.addEventListener('DOMContentLoaded', () => {
  if (!requireAuth()) return;
  
  const role = getUserRole();
  renderSidebar('payments.html');
  
  if (role === 'CUSTOMER') {
    document.getElementById('pageHeadingTitle').textContent = 'My Payments';
    document.getElementById('pageHeadingSub').textContent = 'Review your premium transaction history';
  } else {
    document.getElementById('pageHeadingTitle').textContent = 'All Payments';
    document.getElementById('pageHeadingSub').textContent = 'Review premium transactions across all users';
  }
  
  loadPoliciesDropdown().then(() => {
    // Check for query parameters to pre-fill and auto-open payment modal
    const urlParams = new URLSearchParams(window.location.search);
    const policyId = urlParams.get('policyId');
    const amount = urlParams.get('amount');
    
    if (policyId) {
      openRecordPaymentModal();
      document.getElementById('paymentPolicy').value = policyId;
      if (amount) {
        document.getElementById('paymentAmount').value = amount;
      }
      generateRandomTxnRef();
    }
  });

  loadPayments();
  setupPaymentForm();
});

async function loadPoliciesDropdown() {
  try {
    // For dropdown, get customer policies (or all policies if ADMIN/AGENT)
    const response = await apiGet('/policies?size=100');
    policiesList = response.records || response.content || response || [];
    
    const policySelect = document.getElementById('paymentPolicy');
    policiesList.forEach(pol => {
      // Allow paying only if not cancelled or expired
      if (pol.policyStatus === 'PENDING_PAYMENT' || pol.policyStatus === 'ACTIVE') {
        const opt = document.createElement('option');
        opt.value = pol.id;
        opt.textContent = `${pol.policyNumber} (${pol.planName}) - Premium: ${formatCurrency(pol.premiumAmount)}`;
        policySelect.appendChild(opt);
      }
    });
  } catch (err) {
    console.error("Failed to load policies for payment dropdown", err);
  }
}

async function loadPayments() {
  const container = document.getElementById('tableContainer');
  showLoading(container);

  const status = document.getElementById('filterStatus').value;

  const params = { page: currentPage, size: pageSize, sortField: 'createdDate', sortDirection: 'desc' };
  if (status) params.paymentStatus = status;

  try {
    const response = await apiGet('/payments' + buildQueryParams(params));
    const payments = response.records || response.content || response || [];
    const totalPages = response.totalPages || 1;

    if (payments.length === 0) {
      showEmptyState(container, 'No payment records found', '💸');
      document.getElementById('paginationContainer').innerHTML = '';
      return;
    }

    let html = `
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Policy Number</th>
            <th>Amount</th>
            <th>Payment Date</th>
            <th>Mode</th>
            <th>Transaction Ref</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
    `;

    payments.forEach(pay => {
      html += `
        <tr>
          <td>${pay.id}</td>
          <td style="font-weight:600; color:var(--text-primary);">${pay.policyNumber}</td>
          <td style="font-weight:600; color:#10b981;">${formatCurrency(pay.amount)}</td>
          <td>${formatDateTime(pay.paymentDate || pay.createdDate)}</td>
          <td>${pay.paymentMode}</td>
          <td style="font-family:monospace; font-size:12px;">${escapeHtml(pay.transactionReference)}</td>
          <td>${createStatusBadge(pay.paymentStatus)}</td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;

    renderPagination(document.getElementById('paginationContainer'), currentPage, totalPages, (page) => {
      currentPage = page;
      loadPayments();
    });

  } catch (err) {
    showErrorState(container, 'Failed to load payments');
    showToast(err.message, 'error');
  }
}

function openRecordPaymentModal() {
  document.getElementById('paymentPolicy').value = '';
  document.getElementById('paymentAmount').value = '';
  document.getElementById('paymentMode').value = '';
  document.getElementById('paymentStatus').value = '';
  document.getElementById('transactionRef').value = '';
  
  openModal('paymentModal');
}

function generateRandomTxnRef() {
  const rand = 'TXN' + Math.floor(Math.random() * 100000000).toString().padStart(8, '0');
  document.getElementById('transactionRef').value = rand;
}

function setupPaymentForm() {
  const form = document.getElementById('paymentForm');
  const btn = document.getElementById('savePaymentBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const policyId = document.getElementById('paymentPolicy').value;
    const amount = parseFloat(document.getElementById('paymentAmount').value);
    const mode = document.getElementById('paymentMode').value;
    const status = document.getElementById('paymentStatus').value;
    const txnRef = document.getElementById('transactionRef').value.trim();

    let valid = true;
    if (!validateRequired(policyId)) {
      showFieldError(document.getElementById('paymentPolicy'), document.getElementById('paymentPolicyError'), 'Please select a policy');
      valid = false;
    }
    if (isNaN(amount) || amount <= 0) {
      showFieldError(document.getElementById('paymentAmount'), document.getElementById('paymentAmountError'), 'Amount must be greater than 0');
      valid = false;
    }
    if (!validateRequired(mode)) {
      showFieldError(document.getElementById('paymentMode'), document.getElementById('paymentModeError'), 'Please select a payment mode');
      valid = false;
    }
    if (!validateRequired(status)) {
      showFieldError(document.getElementById('paymentStatus'), document.getElementById('paymentStatusError'), 'Please select a status');
      valid = false;
    }
    if (!validateRequired(txnRef)) {
      showFieldError(document.getElementById('transactionRef'), document.getElementById('transactionRefError'), 'Transaction reference is required');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    const body = {
      policyId: parseInt(policyId),
      amount: amount,
      paymentMode: mode,
      transactionReference: txnRef,
      paymentStatus: status
    };

    try {
      await apiPost('/payments', body);
      showToast('Payment recorded successfully', 'success');
      closeModal('paymentModal');
      loadPayments();
    } catch (err) {
      showToast(err.message || 'Failed to record payment', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}
