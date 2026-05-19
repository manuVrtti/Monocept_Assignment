<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Course - Student Course Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="page-wrapper">
        <nav class="navbar">
            <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">&#x1F393; StudentCourse</a>
            <ul class="navbar-nav">
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/students">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/courses" class="active">Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/registrations">Registrations</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="btn-logout" onclick="event.preventDefault(); confirmLogout(this.href);">Logout</a></li>
            </ul>
        </nav>
        <div class="container fade-in">
            <div class="page-header">
                <h1>Add New Course</h1>
                <p>Fill in the details to create a new course.</p>
            </div>
            <div class="form-card">
                <c:if test="${not empty error}">
                    <div class="alert alert-error">&#x26A0; ${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/course/add" method="POST" id="addCourseForm">
                    <div class="form-group">
                        <label for="courseName">Course Name <span class="required">*</span></label>
                        <input type="text" id="courseName" name="courseName" class="form-control"
                               placeholder="Enter course name" value="${courseName}" required>
                    </div>
                    <div class="form-group">
                        <label for="duration">Duration <span class="required">*</span></label>
                        <input type="text" id="duration" name="duration" class="form-control"
                               placeholder="e.g., 3 Months, 6 Weeks" value="${duration}" required>
                    </div>
                    <div class="form-group">
                        <label for="fees">Fees <span class="required">*</span> (Must be greater than 0)</label>
                        <input type="number" id="fees" name="fees" class="form-control"
                               placeholder="Enter course fees" value="${fees}" step="0.01" min="0.01" required>
                    </div>
                    <div class="form-group">
                        <label for="trainerName">Trainer Name <span class="required">*</span></label>
                        <input type="text" id="trainerName" name="trainerName" class="form-control"
                               placeholder="Enter trainer name" value="${trainerName}" required>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">&#x2795; Add Course</button>
                        <a href="${pageContext.request.contextPath}/courses" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
        <footer class="footer">&copy; 2026 Student Course Registration & Management System</footer>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
