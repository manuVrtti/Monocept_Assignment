// ============================================
// Shared Utilities — Toasts, Modals, Pagination, Formatters
// ============================================

// ---- Toast Notifications ----

function ensureToastContainer() {
  let container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  return container;
}

function showToast(message, type = 'success') {
  const container = ensureToastContainer();
  const icons = {
    success: '✓',
    error: '✕',
    warning: '⚠',
    info: 'ℹ',
  };

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span class="toast-icon">${icons[type] || icons.info}</span>
    <span class="toast-message">${message}</span>
    <button class="toast-close" onclick="this.parentElement.classList.add('removing'); setTimeout(() => this.parentElement.remove(), 300)">✕</button>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.add('removing');
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// ---- Modal ----

function openModal(id) {
  const modal = document.getElementById(id);
  if (modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
  }
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
    // Reset form if exists
    const form = modal.querySelector('form');
    if (form) form.reset();
    // Clear error states
    modal.querySelectorAll('.form-input.error').forEach(el => el.classList.remove('error'));
    modal.querySelectorAll('.form-error.visible').forEach(el => el.classList.remove('visible'));
  }
}

// Close modal on overlay click
document.addEventListener('click', (e) => {
  if (e.target.classList.contains('modal-overlay') && e.target.classList.contains('active')) {
    e.target.classList.remove('active');
    document.body.style.overflow = '';
  }
});

// Close modal on Escape key
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    const activeModal = document.querySelector('.modal-overlay.active');
    if (activeModal) {
      activeModal.classList.remove('active');
      document.body.style.overflow = '';
    }
  }
});

// ---- Pagination ----

function renderPagination(container, currentPage, totalPages, onPageChange) {
  if (!container) return;
  if (totalPages <= 1) {
    container.innerHTML = '';
    return;
  }

  let html = '<div class="pagination">';

  // Previous
  html += `<button class="pagination-btn" ${currentPage === 0 ? 'disabled' : ''} onclick="void(0)" data-page="${currentPage - 1}">‹</button>`;

  // Page numbers
  const maxVisible = 5;
  let startPage = Math.max(0, currentPage - Math.floor(maxVisible / 2));
  let endPage = Math.min(totalPages - 1, startPage + maxVisible - 1);
  if (endPage - startPage < maxVisible - 1) {
    startPage = Math.max(0, endPage - maxVisible + 1);
  }

  if (startPage > 0) {
    html += `<button class="pagination-btn" data-page="0">1</button>`;
    if (startPage > 1) html += `<span class="pagination-info">…</span>`;
  }

  for (let i = startPage; i <= endPage; i++) {
    html += `<button class="pagination-btn ${i === currentPage ? 'active' : ''}" data-page="${i}">${i + 1}</button>`;
  }

  if (endPage < totalPages - 1) {
    if (endPage < totalPages - 2) html += `<span class="pagination-info">…</span>`;
    html += `<button class="pagination-btn" data-page="${totalPages - 1}">${totalPages}</button>`;
  }

  // Next
  html += `<button class="pagination-btn" ${currentPage === totalPages - 1 ? 'disabled' : ''} data-page="${currentPage + 1}">›</button>`;

  html += '</div>';
  container.innerHTML = html;

  // Attach event listeners
  container.querySelectorAll('.pagination-btn:not(:disabled)').forEach(btn => {
    btn.addEventListener('click', () => {
      const page = parseInt(btn.dataset.page);
      if (!isNaN(page) && page >= 0 && page < totalPages) {
        onPageChange(page);
      }
    });
  });
}

// ---- Formatters ----

function formatDate(dateString) {
  if (!dateString) return '—';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return dateString;
  return date.toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' });
}

function formatDateTime(dateString) {
  if (!dateString) return '—';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return dateString;
  return date.toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function formatCurrency(amount) {
  if (amount == null || amount === '') return '—';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(amount);
}

// ---- Status Badge ----

function createStatusBadge(status) {
  if (!status) return '<span class="badge badge-neutral">Unknown</span>';
  
  const statusMap = {
    // Policy statuses
    ACTIVE: 'success',
    INACTIVE: 'neutral',
    CANCELLED: 'danger',
    EXPIRED: 'warning',
    LAPSED: 'warning',
    
    // Claim statuses
    SUBMITTED: 'info',
    UNDER_REVIEW: 'warning',
    RECOMMENDED_APPROVAL: 'violet',
    RECOMMENDED_REJECTION: 'danger',
    APPROVED: 'success',
    REJECTED: 'danger',
    SETTLED: 'success',
    
    // Payment statuses
    SUCCESS: 'success',
    FAILED: 'danger',
    PENDING: 'warning',
    
    // User/Product statuses
    true: 'success',
    false: 'danger',
    
    // Product types
    HEALTH: 'success',
    MOTOR: 'info',
    LIFE: 'violet',
    TRAVEL: 'warning',
  };

  const badgeClass = statusMap[status] || 'neutral';
  const display = String(status).replace(/_/g, ' ');
  return `<span class="badge badge-${badgeClass}">${display}</span>`;
}

function createActiveBadge(isActive) {
  if (isActive) {
    return '<span class="badge badge-success">Active</span>';
  }
  return '<span class="badge badge-danger">Inactive</span>';
}

// ---- Loading / Empty / Error States ----

function showLoading(container) {
  if (!container) return;
  container.innerHTML = `
    <div class="loading-container">
      <div class="loading-spinner"></div>
      <div class="loading-text">Loading data...</div>
    </div>
  `;
}

function hideLoading(container) {
  const loader = container?.querySelector('.loading-container');
  if (loader) loader.remove();
}

function showEmptyState(container, message = 'No data found', icon = '📋') {
  if (!container) return;
  container.innerHTML = `
    <div class="empty-state">
      <div class="empty-state-icon">${icon}</div>
      <div class="empty-state-title">${message}</div>
      <div class="empty-state-text">Try adjusting your filters or create a new record.</div>
    </div>
  `;
}

function showErrorState(container, message = 'Failed to load data') {
  if (!container) return;
  container.innerHTML = `
    <div class="empty-state">
      <div class="empty-state-icon">⚠️</div>
      <div class="empty-state-title">${message}</div>
      <div class="empty-state-text">Please check your connection and try again.</div>
    </div>
  `;
}

// ---- Form Validation ----

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validateMobile(mobile) {
  return /^\d{10}$/.test(mobile);
}

function validateRequired(value) {
  return value !== null && value !== undefined && String(value).trim() !== '';
}

function showFieldError(inputEl, errorEl, message) {
  if (inputEl) inputEl.classList.add('error');
  if (errorEl) {
    errorEl.textContent = message;
    errorEl.classList.add('visible');
  }
}

function clearFieldError(inputEl, errorEl) {
  if (inputEl) inputEl.classList.remove('error');
  if (errorEl) errorEl.classList.remove('visible');
}

function clearAllErrors(formEl) {
  if (!formEl) return;
  formEl.querySelectorAll('.form-input.error').forEach(el => el.classList.remove('error'));
  formEl.querySelectorAll('.form-error.visible').forEach(el => el.classList.remove('visible'));
}

// ---- Button Loading State ----

function setButtonLoading(btn, loading) {
  if (!btn) return;
  if (loading) {
    btn.disabled = true;
    btn.dataset.originalText = btn.innerHTML;
    btn.innerHTML = `<span class="spinner"></span> Processing...`;
  } else {
    btn.disabled = false;
    btn.innerHTML = btn.dataset.originalText || btn.innerHTML;
  }
}

// ---- Debounce ----

function debounce(fn, delay = 300) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

// ---- Helpers ----

function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

function buildQueryParams(params) {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      searchParams.append(key, value);
    }
  });
  const qs = searchParams.toString();
  return qs ? `?${qs}` : '';
}

// ---- Simple Bar Chart ----

function renderBarChart(container, data, colorMap) {
  if (!container || !data || data.length === 0) {
    if (container) container.innerHTML = '<div class="empty-state"><div class="empty-state-text">No data for chart</div></div>';
    return;
  }
  const maxValue = Math.max(...data.map(d => d.value), 1);
  
  let html = '<div class="chart-bar-container">';
  data.forEach(item => {
    const heightPercent = (item.value / maxValue) * 100;
    const color = (colorMap && colorMap[item.label]) || 'var(--accent-indigo)';
    html += `
      <div class="chart-bar-item">
        <div class="chart-bar-value">${item.value}</div>
        <div class="chart-bar" style="height: ${heightPercent}%; background: ${color};"></div>
        <div class="chart-bar-label">${item.label}</div>
      </div>
    `;
  });
  html += '</div>';
  container.innerHTML = html;
}

// ---- Simple Donut Chart ----

function renderDonutChart(container, data, colorMap) {
  if (!container || !data || data.length === 0) {
    if (container) container.innerHTML = '<div class="empty-state"><div class="empty-state-text">No data for chart</div></div>';
    return;
  }

  const total = data.reduce((sum, d) => sum + d.value, 0);
  if (total === 0) {
    container.innerHTML = '<div class="empty-state"><div class="empty-state-text">No data</div></div>';
    return;
  }

  const radius = 54;
  const circumference = 2 * Math.PI * radius;
  let offset = 0;
  
  let circles = '';
  data.forEach(item => {
    const percent = item.value / total;
    const dashLength = percent * circumference;
    const color = (colorMap && colorMap[item.label]) || '#6366f1';
    circles += `<circle cx="70" cy="70" r="${radius}" fill="none" stroke="${color}" stroke-width="12" stroke-dasharray="${dashLength} ${circumference - dashLength}" stroke-dashoffset="${-offset}" stroke-linecap="round"/>`;
    offset += dashLength;
  });

  let legend = '<div class="chart-legend">';
  data.forEach(item => {
    const color = (colorMap && colorMap[item.label]) || '#6366f1';
    legend += `<div class="chart-legend-item"><span class="chart-legend-dot" style="background:${color}"></span>${item.label}: ${item.value}</div>`;
  });
  legend += '</div>';

  container.innerHTML = `
    <div class="donut-chart">
      <svg width="140" height="140" viewBox="0 0 140 140">
        ${circles}
      </svg>
      <div class="donut-center">
        <div class="donut-center-value">${total}</div>
        <div class="donut-center-label">Total</div>
      </div>
    </div>
    ${legend}
  `;
}
