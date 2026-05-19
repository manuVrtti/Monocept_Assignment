<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrations - Student Course Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="page-wrapper">
        <nav class="navbar">
            <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">&#x1F393; StudentCourse</a>
            <ul class="navbar-nav">
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/students">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/courses">Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/registrations" class="active">Registrations</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="btn-logout" onclick="event.preventDefault(); confirmLogout(this.href);">Logout</a></li>
            </ul>
        </nav>
        <div class="container fade-in">
            <div class="page-header">
                <h1>Registration Management</h1>
                <p>View and manage all student-course registrations.</p>
            </div>
            <c:if test="${not empty error}">
                <div class="alert alert-error">&#x26A0; ${error}</div>
            </c:if>
            <div class="table-wrapper">
                <div class="table-header">
                    <h2>All Registrations (${registrations.size()})</h2>
                    <a href="${pageContext.request.contextPath}/registration/add" class="btn btn-primary btn-sm">&#x2795; New Registration</a>
                </div>
                <c:choose>
                    <c:when test="${not empty registrations}">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th><th>Student</th><th>Course</th>
                                    <th>Date</th><th>Status</th><th>Update Status</th><th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${registrations}">
                                    <tr>
                                        <td>${r.registrationId}</td>
                                        <td style="color: var(--text-primary); font-weight: 600;">${r.studentName}</td>
                                        <td>${r.courseName}</td>
                                        <td>${r.registrationDate}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r.status == 'Active'}"><span class="badge badge-active">Active</span></c:when>
                                                <c:when test="${r.status == 'Completed'}"><span class="badge badge-completed">Completed</span></c:when>
                                                <c:when test="${r.status == 'Cancelled'}"><span class="badge badge-cancelled">Cancelled</span></c:when>
                                                <c:otherwise><span class="badge">${r.status}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/registration/status" method="POST" class="status-form">
                                                <input type="hidden" name="registrationId" value="${r.registrationId}">
                                                <select name="status">
                                                    <option value="Active" ${r.status == 'Active' ? 'selected' : ''}>Active</option>
                                                    <option value="Completed" ${r.status == 'Completed' ? 'selected' : ''}>Completed</option>
                                                    <option value="Cancelled" ${r.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                                                </select>
                                                <button type="submit" class="btn btn-success btn-sm">Update</button>
                                            </form>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/registration/delete?id=${r.registrationId}" class="btn btn-danger btn-sm"
                                               onclick="event.preventDefault(); confirmDelete(this.href, 'Registration #${r.registrationId}');">Delete</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="table-empty">No registrations found. Click "New Registration" to get started.</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <footer class="footer">&copy; 2026 Student Course Registration & Management System</footer>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
