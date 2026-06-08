// ============================================
// Authentication Utilities
// ============================================

function isLoggedIn() {
  return !!localStorage.getItem('token');
}

function getUserRole() {
  return localStorage.getItem('role') || '';
}

function getUserEmail() {
  return localStorage.getItem('email') || '';
}

function getUserName() {
  return localStorage.getItem('fullName') || localStorage.getItem('email') || 'User';
}

function logout() {
  localStorage.clear();
  window.location.href = 'index.html';
}

function requireAuth() {
  if (!isLoggedIn()) {
    window.location.href = 'index.html';
    return false;
  }
  return true;
}

function requireRole(allowedRoles) {
  if (!requireAuth()) return false;
  const role = getUserRole();
  if (!allowedRoles.includes(role)) {
    window.location.href = 'dashboard.html';
    return false;
  }
  return true;
}

// ============================================
// Sidebar Renderer
// ============================================

function getNavItems() {
  const role = getUserRole();
  
  const adminNav = [
    { label: 'Dashboard', href: 'dashboard.html', icon: 'dashboard' },
    { label: 'Users', href: 'users.html', icon: 'users' },
    { label: 'Customers', href: 'customers.html', icon: 'customers' },
    { label: 'Products', href: 'products.html', icon: 'products' },
    { label: 'Plans', href: 'plans.html', icon: 'plans' },
    { label: 'Policies', href: 'policies.html', icon: 'policies' },
    { label: 'Payments', href: 'payments.html', icon: 'payments' },
    { label: 'Claims', href: 'claims.html', icon: 'claims' },
  ];

  const agentNav = [
    { label: 'Dashboard', href: 'dashboard.html', icon: 'dashboard' },
    { label: 'Customers', href: 'customers.html', icon: 'customers' },
    { label: 'Products', href: 'products.html', icon: 'products' },
    { label: 'Plans', href: 'plans.html', icon: 'plans' },
    { label: 'Policies', href: 'policies.html', icon: 'policies' },
    { label: 'Payments', href: 'payments.html', icon: 'payments' },
    { label: 'Claims', href: 'claims.html', icon: 'claims' },
  ];

  const customerNav = [
    { label: 'Dashboard', href: 'dashboard.html', icon: 'dashboard' },
    { label: 'My Profile', href: 'customers.html', icon: 'customers' },
    { label: 'Products', href: 'products.html', icon: 'products' },
    { label: 'Plans', href: 'plans.html', icon: 'plans' },
    { label: 'My Policies', href: 'policies.html', icon: 'policies' },
    { label: 'My Payments', href: 'payments.html', icon: 'payments' },
    { label: 'My Claims', href: 'claims.html', icon: 'claims' },
  ];

  if (role === 'ADMIN') return adminNav;
  if (role === 'AGENT') return agentNav;
  return customerNav;
}

function getNavIcon(iconName) {
  const icons = {
    dashboard: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"/></svg>`,
    users: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"/></svg>`,
    customers: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/></svg>`,
    products: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 2L3 7v11h14V7l-7-5zm0 2.236L15 8v8H5V8l5-3.764z" clip-rule="evenodd"/><path d="M6 12h8v2H6z"/></svg>`,
    plans: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/><path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/></svg>`,
    policies: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 1.944A11.954 11.954 0 012.166 5C2.056 5.649 2 6.319 2 7c0 5.225 3.34 9.67 8 11.317C14.66 16.67 18 12.225 18 7c0-.682-.057-1.35-.166-2.001A11.954 11.954 0 0110 1.944zM11 14a1 1 0 11-2 0 1 1 0 012 0zm0-7a1 1 0 10-2 0v3a1 1 0 102 0V7z" clip-rule="evenodd"/></svg>`,
    payments: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path d="M4 4a2 2 0 00-2 2v1h16V6a2 2 0 00-2-2H4z"/><path fill-rule="evenodd" d="M18 9H2v5a2 2 0 002 2h12a2 2 0 002-2V9zM4 13a1 1 0 011-1h1a1 1 0 110 2H5a1 1 0 01-1-1zm5-1a1 1 0 100 2h1a1 1 0 100-2H9z" clip-rule="evenodd"/></svg>`,
    claims: `<svg class="nav-icon" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/></svg>`,
  };
  return icons[iconName] || icons.dashboard;
}

function renderSidebar(activePage) {
  const navItems = getNavItems();
  const currentPage = activePage || window.location.pathname.split('/').pop();
  const role = getUserRole();
  const email = getUserEmail();
  const name = getUserName();
  const initials = name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);

  const sidebarHTML = `
    <div class="sidebar-header">
      <div class="sidebar-brand">
        <div class="sidebar-brand-icon">IP</div>
        <div class="sidebar-brand-text">
          <h2>InsurePolicy</h2>
          <span>Management System</span>
        </div>
      </div>
    </div>
    <nav class="sidebar-nav">
      <div class="sidebar-nav-section">
        <div class="sidebar-nav-title">Navigation</div>
        ${navItems.map(item => `
          <a href="${item.href}" class="sidebar-nav-item ${currentPage === item.href ? 'active' : ''}">
            ${getNavIcon(item.icon)}
            <span>${item.label}</span>
          </a>
        `).join('')}
      </div>
    </nav>
    <div class="sidebar-footer">
      <div class="sidebar-user">
        <div class="sidebar-avatar">${initials}</div>
        <div class="sidebar-user-info">
          <div class="sidebar-user-name">${name}</div>
          <div class="sidebar-user-role">${role}</div>
        </div>
        <button class="sidebar-logout" onclick="logout()" title="Logout">
          <svg width="18" height="18" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 102 0V4a1 1 0 00-1-1zm10.293 9.293a1 1 0 001.414 1.414l3-3a1 1 0 000-1.414l-3-3a1 1 0 10-1.414 1.414L14.586 9H7a1 1 0 100 2h7.586l-1.293 1.293z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>
    </div>
  `;

  const sidebar = document.getElementById('sidebar');
  if (sidebar) {
    sidebar.innerHTML = sidebarHTML;
  }

  // Setup hamburger toggle
  const hamburger = document.getElementById('hamburgerBtn');
  const overlay = document.getElementById('sidebarOverlay');
  
  if (hamburger) {
    hamburger.addEventListener('click', () => {
      sidebar.classList.toggle('open');
      overlay.classList.toggle('active');
    });
  }

  if (overlay) {
    overlay.addEventListener('click', () => {
      sidebar.classList.remove('open');
      overlay.classList.remove('active');
    });
  }
}
