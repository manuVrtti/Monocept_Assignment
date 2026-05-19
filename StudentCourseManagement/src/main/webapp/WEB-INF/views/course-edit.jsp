<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Course - Student Course Management</title>
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
                <h1>Edit Course</h1>
                <p>Update the course details below.</p>
            </div>
            <div class="form-card">
                <c:if test="${not empty error}">
                    <div class="alert alert-error">&#x26A0; ${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/course/update" method="POST" id="editCourseForm">
                    <input type="hidden" name="courseId" value="${course.courseId}">
                    <div class="form-group">
                        <label for="courseName">Course Name <span class="required">*</span></label>
                        <input type="text" id="courseName" name="courseName" class="form-control"
                               value="${course.courseName}" required>
                    </div>
                    <div class="form-group">
                        <label for="duration">Duration <span class="required">*</span></label>
                        <input type="text" id="duration" name="duration" class="form-control"
                               value="${course.duration}" required>
                    </div>
                    <div class="form-group">
                        <label for="fees">Fees <span class="required">*</span> (Must be greater than 0)</label>
                        <input type="number" id="fees" name="fees" class="form-control"
                               value="${course.fees}" step="0.01" min="0.01" required>
                    </div>
                    <div class="form-group">
                        <label for="trainerName">Trainer Name <span class="required">*</span></label>
                        <input type="text" id="trainerName" name="trainerName" class="form-control"
                               value="${course.trainerName}" required>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">&#x1F4BE; Update Course</button>
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
