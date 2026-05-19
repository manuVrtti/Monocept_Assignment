/**
 * Student Course Management System - Common JS
 * Toast notifications & Modal dialogs
 */

/* ==================== Toast System ==================== */
function showToast(message, type) {
    type = type || 'success';
    var container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    var icons = {
        success: '\u2705',
        error: '\u274C',
        info: '\u2139\uFE0F'
    };

    var toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.innerHTML =
        '<span class="toast-icon">' + (icons[type] || '\u2705') + '</span>' +
        '<span>' + message + '</span>' +
        '<button class="toast-close" onclick="dismissToast(this)">\u2715</button>';

    container.appendChild(toast);

    // Auto dismiss after 4 seconds
    setTimeout(function() {
        dismissToast(toast.querySelector('.toast-close'));
    }, 4000);
}

function dismissToast(btn) {
    var toast = btn.closest ? btn.closest('.toast') : btn.parentElement;
    if (toast && !toast.classList.contains('toast-exit')) {
        toast.classList.add('toast-exit');
        setTimeout(function() {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
        }, 300);
    }
}

/* ==================== Modal System ==================== */
var pendingModalAction = null;

function showModal(options) {
    // options: { title, message, type('danger'|'warning'), confirmText, cancelText, onConfirm }
    var overlay = document.getElementById('confirmModal');
    if (!overlay) return;

    var iconEmojis = {
        danger: '\u26A0\uFE0F',
        warning: '\u2753'
    };

    document.getElementById('modalIcon').className = 'modal-icon ' + (options.type || 'danger');
    document.getElementById('modalIconEmoji').textContent = iconEmojis[options.type] || '\u26A0\uFE0F';
    document.getElementById('modalTitle').textContent = options.title || 'Are you sure?';
    document.getElementById('modalMessage').textContent = options.message || 'This action cannot be undone.';
    document.getElementById('modalConfirmBtn').textContent = options.confirmText || 'Confirm';
    document.getElementById('modalCancelBtn').textContent = options.cancelText || 'Cancel';

    // Set button style based on type
    var confirmBtn = document.getElementById('modalConfirmBtn');
    confirmBtn.className = 'btn ' + (options.type === 'danger' ? 'btn-danger' : 'btn-warning');

    pendingModalAction = options.onConfirm || null;
    overlay.classList.add('active');
}

function closeModal() {
    var overlay = document.getElementById('confirmModal');
    if (overlay) {
        overlay.classList.remove('active');
    }
    pendingModalAction = null;
}

function confirmModalAction() {
    if (pendingModalAction) {
        pendingModalAction();
    }
    closeModal();
}

/* ==================== Delete Confirmation ==================== */
function confirmDelete(url, itemName) {
    showModal({
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete ' + (itemName || 'this item') + '? This action cannot be undone.',
        type: 'danger',
        confirmText: 'Delete',
        cancelText: 'Cancel',
        onConfirm: function() {
            window.location.href = url;
        }
    });
}

/* ==================== Logout Confirmation ==================== */
function confirmLogout(url) {
    showModal({
        title: 'Confirm Logout',
        message: 'Are you sure you want to logout? You will need to login again to access the system.',
        type: 'warning',
        confirmText: 'Logout',
        cancelText: 'Stay',
        onConfirm: function() {
            window.location.href = url;
        }
    });
}

/* ==================== Auto-trigger toast from URL params or session ==================== */
document.addEventListener('DOMContentLoaded', function() {
    // Check for toast message embedded in page (set by JSP)
    var toastEl = document.getElementById('toastData');
    if (toastEl) {
        var msg = toastEl.getAttribute('data-message');
        var type = toastEl.getAttribute('data-type') || 'success';
        if (msg && msg.trim() !== '') {
            showToast(msg, type);
        }
    }

    // Close modal on overlay click
    var overlay = document.getElementById('confirmModal');
    if (overlay) {
        overlay.addEventListener('click', function(e) {
            if (e.target === overlay) {
                closeModal();
            }
        });
    }

    // Close modal on Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });
});
