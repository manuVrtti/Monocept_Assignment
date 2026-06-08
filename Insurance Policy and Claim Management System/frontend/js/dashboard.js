// ============================================
// Dashboard — Role-based dashboard content
// ============================================

document.addEventListener('DOMContentLoaded', () => {
  if (!requireAuth()) return;
  renderSidebar('dashboard.html');

  const role = getUserRole();
  const name = getUserName();
  document.getElementById('headerSubtitle').textContent = `Welcome back, ${name}`;
  document.getElementById('headerRole').textContent = role;

  if (role === 'ADMIN') {
    loadAdminDashboard();
  } else if (role === 'AGENT') {
    loadAgentDashboard();
  } else {
    loadCustomerDashboard();
  }
});

const statusColors = {
  ACTIVE: '#22c55e',
  INACTIVE: '#64748b',
  CANCELLED: '#ef4444',
  EXPIRED: '#eab308',
  LAPSED: '#f59e0b',
  SUBMITTED: '#3b82f6',
  UNDER_REVIEW: '#eab308',
  RECOMMENDED_APPROVAL: '#8b5cf6',
  RECOMMENDED_REJECTION: '#f97316',
  APPROVED: '#22c55e',
  REJECTED: '#ef4444',
  SETTLED: '#14b8a6',
  PENDING: '#eab308',
  SUCCESS: '#22c55e',
  FAILED: '#ef4444',
};

// ---- Admin Dashboard ----
async function loadAdminDashboard() {
  document.getElementById('chart1Title').textContent = 'Policies by Status';
  document.getElementById('chart2Title').textContent = 'Claims by Status';
  document.getElementById('recentTableTitle').textContent = 'Recent Claims';

  const statsGrid = document.getElementById('statsGrid');

  try {
    // Fetch data in parallel
    const [users, products, plans, policies, claims] = await Promise.allSettled([
      apiGet('/users?page=0&size=1'),
      apiGet('/products?page=0&size=1'),
      apiGet('/plans?page=0&size=1'),
      apiGet('/policies?page=0&size=1'),
      apiGet('/claims?page=0&size=1'),
    ]);

    const totalUsers = extractTotal(users);
    const totalProducts = extractTotal(products);
    const totalPlans = extractTotal(plans);
    const totalPolicies = extractTotal(policies);
    const totalClaims = extractTotal(claims);

    statsGrid.innerHTML = `
      ${statCard('Users', totalUsers, 'indigo', '👥')}
      ${statCard('Products', totalProducts, 'violet', '📦')}
      ${statCard('Plans', totalPlans, 'blue', '📋')}
      ${statCard('Policies', totalPolicies, 'green', '🛡️')}
      ${statCard('Claims', totalClaims, 'yellow', '📄')}
    `;

    // Load policies chart
    loadStatusChart('chart1', '/policies', 'status');
    // Load claims chart
    loadStatusChart('chart2', '/claims', 'claimStatus');
    // Load recent claims table
    loadRecentClaims();

  } catch (err) {
    statsGrid.innerHTML = '<p style="color:var(--text-muted);">Failed to load dashboard data.</p>';
    showToast('Failed to load dashboard data', 'error');
  }
}

// ---- Agent Dashboard ----
async function loadAgentDashboard() {
  document.getElementById('chart1Title').textContent = 'Claims by Status';
  document.getElementById('chart2Title').textContent = 'Policies by Status';
  document.getElementById('recentTableTitle').textContent = 'Recent Claims';

  const statsGrid = document.getElementById('statsGrid');

  try {
    const [policies, claims] = await Promise.allSettled([
      apiGet('/policies?page=0&size=1'),
      apiGet('/claims?page=0&size=1'),
    ]);

    const totalPolicies = extractTotal(policies);
    const totalClaims = extractTotal(claims);

    statsGrid.innerHTML = `
      ${statCard('Total Policies', totalPolicies, 'green', '🛡️')}
      ${statCard('Total Claims', totalClaims, 'yellow', '📄')}
      ${statCard('Pending Reviews', '—', 'violet', '⏳')}
    `;

    loadStatusChart('chart1', '/claims', 'claimStatus');
    loadStatusChart('chart2', '/policies', 'status');
    loadRecentClaims();
  } catch (err) {
    showToast('Failed to load dashboard', 'error');
  }
}

// ---- Customer Dashboard ----
async function loadCustomerDashboard() {
  document.getElementById('chart1Title').textContent = 'My Policies by Status';
  document.getElementById('chart2Title').textContent = 'My Claims by Status';
  document.getElementById('recentTableTitle').textContent = 'Recent Activity';

  const statsGrid = document.getElementById('statsGrid');

  try {
    const [policies, claims, payments] = await Promise.allSettled([
      apiGet('/policies?page=0&size=1'),
      apiGet('/claims?page=0&size=1'),
      apiGet('/payments?page=0&size=1'),
    ]);

    const totalPolicies = extractTotal(policies);
    const totalClaims = extractTotal(claims);
    const totalPayments = extractTotal(payments);

    statsGrid.innerHTML = `
      ${statCard('My Policies', totalPolicies, 'green', '🛡️')}
      ${statCard('My Claims', totalClaims, 'yellow', '📄')}
      ${statCard('My Payments', totalPayments, 'violet', '💳')}
    `;

    loadStatusChart('chart1', '/policies', 'status');
    loadStatusChart('chart2', '/claims', 'claimStatus');
    loadRecentClaims();
  } catch (err) {
    showToast('Failed to load dashboard', 'error');
  }
}

// ---- Helpers ----

function extractTotal(result) {
  if (result.status === 'fulfilled') {
    const data = result.value;
    if (data) {
      if (typeof data.totalRecords !== 'undefined') return data.totalRecords;
      if (typeof data.totalElements !== 'undefined') return data.totalElements;
      if (typeof data.total !== 'undefined') return data.total;
      if (Array.isArray(data.records)) return data.totalRecords || data.records.length;
      if (Array.isArray(data.content)) return data.totalElements || data.content.length;
      if (Array.isArray(data)) return data.length;
    }
  }
  return 0;
}

function statCard(label, value, color, icon) {
  return `
    <div class="stat-card">
      <div class="stat-card-header">
        <div class="stat-card-label">${label}</div>
        <div class="stat-card-icon ${color}">${icon}</div>
      </div>
      <div class="stat-card-value">${value}</div>
    </div>
  `;
}

async function loadStatusChart(containerId, endpoint, statusField) {
  const container = document.getElementById(containerId);
  try {
    const data = await apiGet(`${endpoint}?page=0&size=100`);
    const items = data.records || data.content || data || [];
    
    const statusCounts = {};
    items.forEach(item => {
      const status = item[statusField] || item.status || item.claimStatus || 'UNKNOWN';
      statusCounts[status] = (statusCounts[status] || 0) + 1;
    });

    const chartData = Object.entries(statusCounts).map(([label, value]) => ({ label: label.replace(/_/g, ' '), value }));
    
    if (chartData.length === 0) {
      container.innerHTML = '<div class="empty-state"><div class="empty-state-text">No data available</div></div>';
      return;
    }

    const colorMap = {};
    chartData.forEach(d => {
      const key = d.label.replace(/ /g, '_');
      colorMap[d.label] = statusColors[key] || '#6366f1';
    });

    renderDonutChart(container, chartData, colorMap);
  } catch (err) {
    container.innerHTML = '<div class="empty-state"><div class="empty-state-text">Unable to load chart</div></div>';
  }
}

async function loadRecentClaims() {
  const container = document.getElementById('recentTableContainer');
  showLoading(container);

  try {
    const data = await apiGet('/claims?page=0&size=5&sortField=createdDate&sortDirection=desc');
    const claims = data.records || data.content || data || [];

    if (claims.length === 0) {
      showEmptyState(container, 'No recent claims');
      return;
    }

    let html = `
      <table class="data-table">
        <thead>
          <tr>
            <th>Claim #</th>
            <th>Policy</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
    `;

    claims.forEach(claim => {
      html += `
        <tr>
          <td>${claim.claimNumber || claim.id || '—'}</td>
          <td>${claim.policyNumber || '—'}</td>
          <td style="font-weight:600; color:#ef4444;">${formatCurrency(claim.claimAmount)}</td>
          <td>${createStatusBadge(claim.claimStatus || claim.status)}</td>
          <td>${formatDate(claim.createdDate)}</td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;
  } catch (err) {
    showEmptyState(container, 'Unable to load recent claims');
  }
}

