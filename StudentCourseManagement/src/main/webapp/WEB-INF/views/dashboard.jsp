<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Student Course Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="page-wrapper">
        <!-- Navbar -->
        <nav class="navbar">
            <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">&#x1F393; StudentCourse</a>
            <ul class="navbar-nav">
                <li><a href="${pageContext.request.contextPath}/dashboard" class="active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/students">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/courses">Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/registrations">Registrations</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="btn-logout" onclick="event.preventDefault(); confirmLogout(this.href);">Logout</a></li>
            </ul>
        </nav>

        <div class="container fade-in">
            <!-- Welcome -->
            <div class="page-header">
                <div class="welcome-section">
                    <h2>Welcome, <span>${adminUsername}</span> &#x1F44B;</h2>
                    <p class="welcome-time">Manage your students, courses, and registrations from here.</p>
                </div>
            </div>

            <!-- Stats -->
            <div class="section-title">Overview</div>
            <div class="stats-grid">
                <div class="stat-card students">
                    <div class="stat-icon">&#x1F464;</div>
                    <div class="stat-value">${studentCount}</div>
                    <div class="stat-label">Total Students</div>
                </div>
                <div class="stat-card courses">
                    <div class="stat-icon">&#x1F4DA;</div>
                    <div class="stat-value">${courseCount}</div>
                    <div class="stat-label">Total Courses</div>
                </div>
                <div class="stat-card registrations">
                    <div class="stat-icon">&#x1F4CB;</div>
                    <div class="stat-value">${registrationCount}</div>
                    <div class="stat-label">Total Registrations</div>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="section-title">Quick Actions</div>
            <div class="quick-actions">
                <a href="${pageContext.request.contextPath}/student/add" class="action-card">
                    <div class="action-icon">&#x2795;</div>
                    <div><div class="action-text">Add Student</div><div class="action-desc">Register a new student</div></div>
                </a>
                <a href="${pageContext.request.contextPath}/course/add" class="action-card">
                    <div class="action-icon">&#x2795;</div>
                    <div><div class="action-text">Add Course</div><div class="action-desc">Create a new course</div></div>
                </a>
                <a href="${pageContext.request.contextPath}/registration/add" class="action-card">
                    <div class="action-icon">&#x1F517;</div>
                    <div><div class="action-text">New Registration</div><div class="action-desc">Register student for course</div></div>
                </a>
                <a href="${pageContext.request.contextPath}/students" class="action-card">
                    <div class="action-icon">&#x1F4CB;</div>
                    <div><div class="action-text">View Students</div><div class="action-desc">Browse all student records</div></div>
                </a>
                <a href="${pageContext.request.contextPath}/courses" class="action-card">
                    <div class="action-icon">&#x1F4DA;</div>
                    <div><div class="action-text">View Courses</div><div class="action-desc">Browse all courses</div></div>
                </a>
                <a href="${pageContext.request.contextPath}/registrations" class="action-card">
                    <div class="action-icon">&#x1F4C4;</div>
                    <div><div class="action-text">View Registrations</div><div class="action-desc">Browse all registrations</div></div>
                </a>
            </div>
        </div>

        <footer class="footer">
            &copy; 2026 Student Course Registration & Management System
        </footer>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
