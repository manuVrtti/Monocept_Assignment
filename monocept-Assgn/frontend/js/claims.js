// ============================================
// Claims Page Script
// ============================================

let currentPage = 0;
const pageSize = 10;
let activePoliciesList = [];
let docRowCount = 0;

document.addEventListener('DOMContentLoaded', () => {
  if (!requireAuth()) return;
  
  const role = getUserRole();
  renderSidebar('claims.html');
  
  if (role === 'CUSTOMER') {
    document.getElementById('raiseClaimBtn').style.display = 'inline-flex';
    document.getElementById('pageHeadingTitle').textContent = 'My Claims';
    document.getElementById('pageHeadingSub').textContent = 'File and track claims for your active policies';
    setupClaimForm();
    loadActivePoliciesDropdown();
  } else {
    document.getElementById('pageHeadingTitle').textContent = 'All Claims';
    document.getElementById('pageHeadingSub').textContent = 'Review and process insurance coverage claims';
    if (role === 'AGENT') {
      setupReviewForm();
    } else if (role === 'ADMIN') {
      setupDecideForm();
    }
  }

  loadClaims();
});

async function loadActivePoliciesDropdown() {
  try {
    // Get customer's policies. Filter active policies only in JS to ensure user is safe
    const data = await apiGet('/policies?size=100');
    const policies = data.records || data.content || data || [];
    
    const policySelect = document.getElementById('claimPolicy');
    
    policies.forEach(pol => {
      if (pol.policyStatus === 'ACTIVE') {
        activePoliciesList.push(pol);
        const opt = document.createElement('option');
        opt.value = pol.id;
        opt.textContent = `${pol.policyNumber} (${pol.planName}) - Coverage: ${formatCurrency(pol.coverageAmount)}`;
        policySelect.appendChild(opt);
      }
    });
  } catch (err) {
    console.error("Failed to load active policies for claim filing", err);
  }
}

async function loadClaims() {
  const container = document.getElementById('tableContainer');
  showLoading(container);

  const status = document.getElementById('filterStatus').value;
  const role = getUserRole();

  const params = { page: currentPage, size: pageSize, sortField: 'createdDate', sortDirection: 'desc' };
  if (status) params.claimStatus = status;

  try {
    const response = await apiGet('/claims' + buildQueryParams(params));
    const claims = response.records || response.content || response || [];
    const totalPages = response.totalPages || 1;

    if (claims.length === 0) {
      showEmptyState(container, 'No claims found', '📋');
      document.getElementById('paginationContainer').innerHTML = '';
      return;
    }

    let html = `
      <table class="data-table">
        <thead>
          <tr>
            <th>Claim Number</th>
            <th>Policy Number</th>
            ${role !== 'CUSTOMER' ? '<th>Customer</th>' : ''}
            <th>Claim Amount</th>
            <th>Incident Date</th>
            <th>Status</th>
            <th>File Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
    `;

    claims.forEach(claim => {
      const isFinalized = claim.claimStatus === 'APPROVED' || claim.claimStatus === 'REJECTED';
      let actionsHtml = '';

      if (role === 'AGENT' && !isFinalized) {
        actionsHtml = `
          <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); openReviewModal(${claim.id}, '${claim.claimStatus}')">
            Review
          </button>
        `;
      } else if (role === 'ADMIN' && !isFinalized) {
        actionsHtml = `
          <button class="btn btn-sm btn-success" onclick="event.stopPropagation(); openDecideModal(${claim.id}, '${claim.claimStatus}')">
            Decide
          </button>
        `;
      } else {
        actionsHtml = `
          <button class="btn btn-sm btn-secondary" onclick="event.stopPropagation(); viewClaimDetails(${claim.id})">
            Details
          </button>
        `;
      }

      html += `
        <tr onclick="viewClaimDetails(${claim.id})" style="cursor:pointer;" title="Click to view details & history">
          <td style="font-weight:600; color:var(--text-primary);">${claim.claimNumber}</td>
          <td>${claim.policyNumber}</td>
          ${role !== 'CUSTOMER' ? `<td>${escapeHtml(claim.customerName || '—')}</td>` : ''}
          <td style="font-weight:600; color:#ef4444;">${formatCurrency(claim.claimAmount)}</td>
          <td>${formatDate(claim.incidentDate)}</td>
          <td>${createStatusBadge(claim.claimStatus)}</td>
          <td>${formatDate(claim.createdDate)}</td>
          <td>
            <div style="display:flex; gap:8px; align-items:center;">
              ${actionsHtml}
            </div>
          </td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;

    renderPagination(document.getElementById('paginationContainer'), currentPage, totalPages, (page) => {
      currentPage = page;
      loadClaims();
    });

  } catch (err) {
    showErrorState(container, 'Failed to load claims');
    showToast(err.message, 'error');
  }
}

function openRaiseClaimModal() {
  document.getElementById('claimPolicy').value = '';
  document.getElementById('claimAmount').value = '';
  document.getElementById('claimIncidentDate').value = new Date().toISOString().substring(0, 10);
  document.getElementById('claimReason').value = '';
  
  const docContainer = document.getElementById('documentsListContainer');
  docContainer.innerHTML = '';
  docRowCount = 0;
  
  // Always add one default document row
  addDocumentRow();

  openModal('claimModal');
}

function addDocumentRow() {
  docRowCount++;
  const container = document.getElementById('documentsListContainer');
  
  const row = document.createElement('div');
  row.className = 'doc-row';
  row.id = `docRow_${docRowCount}`;
  row.style = 'display:grid; grid-template-columns:1.5fr 1fr 1.5fr auto; gap:8px; align-items:center; background:rgba(15,23,42,0.3); padding:8px; border-radius:8px;';
  
  row.innerHTML = `
    <div>
      <input type="text" placeholder="Doc Name (e.g. Medical Bill)" class="form-input doc-name" required>
    </div>
    <div>
      <select class="form-input doc-type" required>
        <option value="BILL">Bill</option>
        <option value="MEDICAL_REPORT">Medical Report</option>
        <option value="ID_PROOF">ID Proof</option>
        <option value="RECEIPT">Receipt</option>
        <option value="OTHER">Other</option>
      </select>
    </div>
    <div>
      <input type="text" placeholder="Ref (e.g. URL or file.pdf)" class="form-input doc-ref" required>
    </div>
    <div>
      <button type="button" class="btn btn-sm btn-danger" onclick="removeDocumentRow(${docRowCount})" style="padding:10px;">✕</button>
    </div>
  `;
  
  container.appendChild(row);
}

function removeDocumentRow(id) {
  const row = document.getElementById(`docRow_${id}`);
  if (row) {
    // Keep at least one row
    const container = document.getElementById('documentsListContainer');
    if (container.children.length > 1) {
      row.remove();
    } else {
      showToast('At least one claim document is required', 'warning');
    }
  }
}

function openReviewModal(id, currentStatus) {
  document.getElementById('reviewClaimId').value = id;
  document.getElementById('reviewStatus').value = currentStatus === 'SUBMITTED' ? 'UNDER_REVIEW' : currentStatus;
  document.getElementById('reviewRemarks').value = '';
  openModal('reviewModal');
}

function openDecideModal(id, currentStatus) {
  document.getElementById('decideClaimId').value = id;
  document.getElementById('decideStatus').value = '';
  document.getElementById('decideRemarks').value = '';
  openModal('decideModal');
}

async function viewClaimDetails(id) {
  const detailsContent = document.getElementById('detailsContent');
  detailsContent.innerHTML = '<div style="padding:40px; text-align:center;"><div class="loading-spinner" style="margin:0 auto 12px;"></div>Loading details...</div>';
  
  openModal('detailsModal');
  
  try {
    // Fetch details
    const claim = await apiGet(`/claims/${id}`);
    
    // Fetch status history
    const historyList = await apiGet(`/claims/${id}/history`);
    
    let docsHtml = '';
    if (claim.documents && claim.documents.length > 0) {
      docsHtml = claim.documents.map(doc => `
        <div style="display:flex; justify-content:space-between; align-items:center; background:rgba(255,255,255,0.03); padding:10px 14px; border-radius:8px; border:1px solid var(--glass-border);">
          <div>
            <div style="font-weight:600; color:var(--text-primary); font-size:13px;">${escapeHtml(doc.documentName)}</div>
            <div style="font-size:11px; color:var(--text-muted); text-transform:uppercase; margin-top:2px;">${doc.documentType}</div>
          </div>
          <a class="btn btn-sm btn-secondary" href="${escapeHtml(doc.documentReference)}" target="_blank" style="padding:6px 12px; font-size:11px;">
            View Link
          </a>
        </div>
      `).join('');
    } else {
      docsHtml = '<div style="color:var(--text-muted); font-size:13px;">No documents attached to this claim</div>';
    }

    let historyTimelineHtml = '';
    if (historyList && historyList.length > 0) {
      historyTimelineHtml = historyList.map(h => `
        <div style="position:relative; padding-left:24px; padding-bottom:20px; border-left:2px solid var(--border-color);">
          <div style="position:absolute; left:-7px; top:4px; width:12px; height:12px; border-radius:50%; background:var(--accent-indigo); box-shadow:0 0 0 4px rgba(99,102,241,0.2);"></div>
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
            <div style="font-weight:600; font-size:13px; color:var(--text-primary);">${createStatusBadge(h.newStatus)}</div>
            <div style="font-size:11px; color:var(--text-muted);">${formatDateTime(h.updatedDate)}</div>
          </div>
          <div style="font-size:12px; color:var(--text-secondary); margin-bottom:4px;">
            Updated by: <span style="font-weight:500; color:var(--text-primary);">${escapeHtml(h.updatedByName)}</span>
          </div>
          ${h.remarks ? `<div style="font-size:12px; color:var(--text-muted); background:rgba(15,23,42,0.2); padding:6px 10px; border-radius:6px; margin-top:6px; font-style:italic;">"${escapeHtml(h.remarks)}"</div>` : ''}
        </div>
      `).join('');
    } else {
      historyTimelineHtml = '<div style="color:var(--text-muted); font-size:13px;">No status change logs available</div>';
    }

    detailsContent.innerHTML = `
      <div style="display:flex; flex-direction:column; gap:24px;">
        <!-- Summary Cards -->
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
          <div class="glass-card" style="padding:16px;">
            <div style="font-size:12px; color:var(--text-muted); font-weight:500; text-transform:uppercase;">Claim Info</div>
            <div style="margin-top:10px; display:flex; flex-direction:column; gap:8px; font-size:13px;">
              <div>Claim No: <span style="font-weight:600; color:var(--text-primary); float:right;">${claim.claimNumber}</span></div>
              <div>Policy No: <span style="font-weight:500; color:var(--text-primary); float:right;">${claim.policyNumber}</span></div>
              <div>Customer: <span style="font-weight:500; color:var(--text-primary); float:right;">${escapeHtml(claim.customerName || '—')}</span></div>
              <div>Incident Date: <span style="color:var(--text-primary); float:right;">${formatDate(claim.incidentDate)}</span></div>
            </div>
          </div>
          <div class="glass-card" style="padding:16px;">
            <div style="font-size:12px; color:var(--text-muted); font-weight:500; text-transform:uppercase;">Financial & Decision</div>
            <div style="margin-top:10px; display:flex; flex-direction:column; gap:8px; font-size:13px;">
              <div>Claim Amount: <span style="font-weight:700; color:#ef4444; float:right;">${formatCurrency(claim.claimAmount)}</span></div>
              <div>Status: <span style="float:right;">${createStatusBadge(claim.claimStatus)}</span></div>
              <div>Agent Notes: <span style="color:var(--text-primary); float:right; max-width:140px; text-overflow:ellipsis; overflow:hidden; white-space:nowrap;" title="${escapeHtml(claim.agentRemarks || '—')}">${escapeHtml(claim.agentRemarks || '—')}</span></div>
              <div>Admin Notes: <span style="color:var(--text-primary); float:right; max-width:140px; text-overflow:ellipsis; overflow:hidden; white-space:nowrap;" title="${escapeHtml(claim.adminRemarks || '—')}">${escapeHtml(claim.adminRemarks || '—')}</span></div>
            </div>
          </div>
        </div>

        <!-- Claim Reason -->
        <div class="glass-card" style="padding:16px;">
          <div style="font-size:12px; color:var(--text-muted); font-weight:500; text-transform:uppercase; margin-bottom:8px;">Reason Description</div>
          <div style="font-size:13px; color:var(--text-secondary); line-height:1.6; word-break:break-word;">
            ${escapeHtml(claim.claimReason)}
          </div>
        </div>

        <!-- Documents & Timeline Grid -->
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:20px;">
          <!-- Documents List -->
          <div>
            <h4 style="font-size:13px; font-weight:600; text-transform:uppercase; letter-spacing:0.03em; color:var(--text-muted); margin-bottom:12px;">Documents</h4>
            <div style="display:flex; flex-direction:column; gap:8px;">
              ${docsHtml}
            </div>
          </div>

          <!-- Timeline -->
          <div>
            <h4 style="font-size:13px; font-weight:600; text-transform:uppercase; letter-spacing:0.03em; color:var(--text-muted); margin-bottom:12px;">Status Timeline</h4>
            <div style="padding-top:8px;">
              ${historyTimelineHtml}
            </div>
          </div>
        </div>
      </div>
    `;

  } catch (err) {
    detailsContent.innerHTML = `<div style="padding:40px; text-align:center; color:var(--error);">Failed to load claim details: ${escapeHtml(err.message)}</div>`;
  }
}

function setupClaimForm() {
  const form = document.getElementById('claimForm');
  const btn = document.getElementById('saveClaimBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const policyId = document.getElementById('claimPolicy').value;
    const amount = parseFloat(document.getElementById('claimAmount').value);
    const incidentDate = document.getElementById('claimIncidentDate').value;
    const reason = document.getElementById('claimReason').value.trim();

    let valid = true;
    if (!validateRequired(policyId)) {
      showFieldError(document.getElementById('claimPolicy'), document.getElementById('claimPolicyError'), 'Please select a policy');
      valid = false;
    }
    if (isNaN(amount) || amount <= 0) {
      showFieldError(document.getElementById('claimAmount'), document.getElementById('claimAmountError'), 'Claim amount must be greater than 0');
      valid = false;
    }
    if (!validateRequired(incidentDate)) {
      showFieldError(document.getElementById('claimIncidentDate'), document.getElementById('claimIncidentDateError'), 'Incident date is required');
      valid = false;
    }
    if (!validateRequired(reason)) {
      showFieldError(document.getElementById('claimReason'), document.getElementById('claimReasonError'), 'Reason for claim is required');
      valid = false;
    }

    // Build documents array
    const docRows = document.querySelectorAll('#documentsListContainer .doc-row');
    const documents = [];
    let docsValid = true;

    docRows.forEach(row => {
      const docName = row.querySelector('.doc-name').value.trim();
      const docType = row.querySelector('.doc-type').value;
      const docRef = row.querySelector('.doc-ref').value.trim();

      if (!docName || !docRef) {
        docsValid = false;
      } else {
        documents.push({
          documentName: docName,
          documentType: docType,
          documentReference: docRef
        });
      }
    });

    const docErrorEl = document.getElementById('documentsError');
    if (!docsValid || documents.length === 0) {
      docErrorEl.textContent = 'All added document fields must be completed.';
      docErrorEl.classList.add('visible');
      valid = false;
    } else {
      docErrorEl.classList.remove('visible');
    }

    if (!valid) return;

    // Check claim amount against policy coverage limit in JS for security check
    const selectedPolicy = activePoliciesList.find(p => p.id == policyId);
    if (selectedPolicy && amount > selectedPolicy.coverageAmount) {
      showFieldError(document.getElementById('claimAmount'), document.getElementById('claimAmountError'), `Claim amount cannot exceed coverage limit: ${formatCurrency(selectedPolicy.coverageAmount)}`);
      return;
    }

    setButtonLoading(btn, true);

    const body = {
      policyId: parseInt(policyId),
      claimAmount: amount,
      claimReason: reason,
      incidentDate: incidentDate,
      documents: documents
    };

    try {
      await apiPost('/claims', body);
      showToast('Claim submitted successfully', 'success');
      closeModal('claimModal');
      loadClaims();
    } catch (err) {
      showToast(err.message || 'Failed to submit claim', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}

function setupReviewForm() {
  const form = document.getElementById('reviewForm');
  const btn = document.getElementById('saveReviewBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const claimId = document.getElementById('reviewClaimId').value;
    const status = document.getElementById('reviewStatus').value;
    const remarks = document.getElementById('reviewRemarks').value.trim();

    let valid = true;
    if (!validateRequired(status)) {
      showFieldError(document.getElementById('reviewStatus'), document.getElementById('reviewStatusError'), 'Recommendation status is required');
      valid = false;
    }
    if (!validateRequired(remarks)) {
      showFieldError(document.getElementById('reviewRemarks'), document.getElementById('reviewRemarksError'), 'Remarks are required for review feedback');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    try {
      await apiPut(`/claims/${claimId}/review`, {
        recommendedStatus: status,
        remarks: remarks
      });
      showToast('Claim recommendation submitted successfully', 'success');
      closeModal('reviewModal');
      loadClaims();
    } catch (err) {
      showToast(err.message || 'Failed to submit review', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}

function setupDecideForm() {
  const form = document.getElementById('decideForm');
  const btn = document.getElementById('saveDecideBtn');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors(form);

    const claimId = document.getElementById('decideClaimId').value;
    const status = document.getElementById('decideStatus').value;
    const remarks = document.getElementById('decideRemarks').value.trim();

    let valid = true;
    if (!validateRequired(status)) {
      showFieldError(document.getElementById('decideStatus'), document.getElementById('decideStatusError'), 'Decision status is required');
      valid = false;
    }
    if (!validateRequired(remarks)) {
      showFieldError(document.getElementById('decideRemarks'), document.getElementById('decideRemarksError'), 'Explanation remarks are required');
      valid = false;
    }

    if (!valid) return;

    setButtonLoading(btn, true);

    try {
      await apiPut(`/claims/${claimId}/decide`, {
        finalStatus: status,
        remarks: remarks
      });
      showToast('Claim decision processed successfully', 'success');
      closeModal('decideModal');
      loadClaims();
    } catch (err) {
      showToast(err.message || 'Failed to submit decision', 'error');
    } finally {
      setButtonLoading(btn, false);
    }
  });
}
