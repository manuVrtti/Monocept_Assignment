// ============================================
// Products Page Script
// ============================================

let currentPage = 0;
const pageSize = 10;
let isEditMode = false;

document.addEventListener('DOMContentLoaded', () => {
  if (!requireAuth()) return;
  
  const role = getUserRole();
  renderSidebar('products.html');
  
  // Only Admin can create products
  if (role === 'ADMIN') {
    const createBtn = document.getElementById('createProductBtn');
    if (createBtn) createBtn.style.display = 'inline-flex';
  }
  
  loadProducts();
  setupProductForm();
});

async function loadProducts() {
  const container = document.getElementById('tableContainer');
  showLoading(container);

  const productType = document.getElementById('filterProductType').value;
  const active = document.getElementById('filterActive').value;

  const params = { page: currentPage, size: pageSize, sortField: 'createdDate', sortDirection: 'desc' };
  if (productType) params.productType = productType;
  if (active !== '') params.active = active;

  try {
    const response = await apiGet('/products' + buildQueryParams(params));
    // Support custom PaginatedResponse with records/content field
    const products = response.records || response.content || response || [];
    const totalPages = response.totalPages || 1;
    const role = getUserRole();

    if (products.length === 0) {
      showEmptyState(container, 'No products found', '📦');
      document.getElementById('paginationContainer').innerHTML = '';
      return;
    }

    let html = `
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Product Name</th>
            <th>Type</th>
            <th>Description</th>
            <th>Status</th>
            ${role === 'ADMIN' ? '<th>Actions</th>' : ''}
          </tr>
        </thead>
        <tbody>
    `;

    products.forEach(prod => {
      const isActive = prod.active !== false;
      let actionsHtml = '';
      
      if (role === 'ADMIN') {
        actionsHtml = `
          <td>
            <div style="display:flex; gap:8px;">
              <button class="btn btn-sm btn-secondary" onclick="openEditProductModal(${prod.id}, '${escapeHtml(prod.productName)}', '${prod.productType}', '${escapeHtml(prod.description)}', ${isActive})">
                Edit
              </button>
              ${isActive ? `
                <button class="btn btn-sm btn-danger" onclick="deactivateProduct(${prod.id})">
                  Deactivate
                </button>
              ` : ''}
            </div>
          </td>
        `;
      }

      html += `
        <tr>
          <td>${prod.id}</td>
          <td style="font-weight:600; color:var(--text-primary);">${escapeHtml(prod.productName)}</td>
          <td>${createStatusBadge(prod.productType)}</td>
          <td style="max-width:300px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;" title="${escapeHtml(prod.description)}">
            ${escapeHtml(prod.description)}
          </td>
          <td>${createActiveBadge(isActive)}</td>
          ${actionsHtml}
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;

    renderPagination(document.getElementById('paginationContainer'), currentPage, totalPages, (page) => {
      currentPage = page;
      loadProducts();
    });

  } catch (err) {
    showErrorState(container, 'Failed to load products');
    showToast(err.message, 'error');
  }
}

function openCreateProductModal() {
  isEditMode = false;
  document.getElementById('modalTitle').textContent = 'Create Product';
  document.getElementById('productId').value = '';
  document.getElementById('productName').value = '';
  document.getElementById('productType').value = '';
  document.getElementById('productDescription').value = '';
  document.getElementById('productActive').checked = true;
  document.getElementById('productActive').parentElement.style.display = 'flex';
  
  openModal('productModal');
}

function openEditProductModal(id, name, type, description, active) {
  isEditMode = true;
  document.getElementById('modalTitle').textContent = 'Edit Product';
  document.getElementById('productId').value = id;
  document.getElementById('productName').value = name;
  document.getElementById('productType').value = type;
  document.getElementById('productDescription').value = description;
  document.getElementById('productActive').checked = active;
  
  // Hide active checkbox since we have a separate deactivate endpoint, or keep it visible
  document.getElementById('productActive').parentElement.style.display = 'flex';
  
  openModal('productModal');
}

async function deactivateProduct(id) {
  if (!confirm('Are you sure you want to deactivate this product? Linked plans will also be affected.')) return;
  
  try {
    await apiPut(`/products/${id}/deactivate`);
    showToast('Product deactivated successfully', 'success');
    loadProducts();
  } catch (err) {
    showToast(err.message || 'Failed to deactivate product', 'error');
  }
}

function setupProductForm() {
  const form = document.getElementById('productForm');
  const btn = document.getElementById('saveProductBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const id = document.getElementById('productId').value;
    const name = document.getElementById('productName').value.trim();
    const type = document.getElementById('productType').value;
    const description = document.getElementById('productDescription').value.trim();
    const active = document.getElementById('productActive').checked;

    let valid = true;

    if (!validateRequired(name)) {
      showFieldError(document.getElementById('productName'), document.getElementById('productNameError'), 'Product name is required');
      valid = false;
    }
    if (!validateRequired(type)) {
      showFieldError(document.getElementById('productType'), document.getElementById('productTypeError'), 'Product type is required');
      valid = false;
    }
    if (!validateRequired(description)) {
      showFieldError(document.getElementById('productDescription'), document.getElementById('productDescriptionError'), 'Description is required');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    const body = { productName: name, productType: type, description, active };

    try {
      if (isEditMode) {
        await apiPut(`/products/${id}`, body);
        showToast('Product updated successfully', 'success');
      } else {
        await apiPost('/products', body);
        showToast('Product created successfully', 'success');
      }
      closeModal('productModal');
      loadProducts();
    } catch (err) {
      showToast(err.message || 'Failed to save product', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}
