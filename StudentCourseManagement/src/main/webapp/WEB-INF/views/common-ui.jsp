<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    // Flash message logic
    String toastMsg = (String) session.getAttribute("toastMessage");
    String toastType = (String) session.getAttribute("toastType");
    if (toastMsg != null) {
        session.removeAttribute("toastMessage");
        session.removeAttribute("toastType");
%>
    <div id="toastData" data-message="<%= toastMsg.replace("\"", "&quot;") %>" data-type="<%= toastType != null ? toastType : "success" %>" style="display:none;"></div>
<%
    } else {
        // Also check request parameters (like ?toast=loggedout)
        String paramToast = request.getParameter("toast");
        if ("loggedout".equals(paramToast)) {
%>
            <div id="toastData" data-message="You have been successfully logged out." data-type="info" style="display:none;"></div>
<%
        }
    }
%>

<!-- Modal Overlay Structure -->
<div id="confirmModal" class="modal-overlay">
    <div class="modal-box">
        <div id="modalIcon" class="modal-icon danger">
            <span id="modalIconEmoji">&#x26A0;&#xFE0F;</span>
        </div>
        <h3 id="modalTitle" class="modal-title">Confirm Action</h3>
        <p id="modalMessage" class="modal-message">Are you sure you want to proceed?</p>
        <div class="modal-actions">
            <button id="modalCancelBtn" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
            <button id="modalConfirmBtn" class="btn btn-danger" onclick="confirmModalAction()">Confirm</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/common.js"></script>
