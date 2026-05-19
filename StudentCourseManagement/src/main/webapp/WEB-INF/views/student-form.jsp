<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Student - Student Course Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="page-wrapper">
        <nav class="navbar">
            <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">&#x1F393; StudentCourse</a>
            <ul class="navbar-nav">
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/students" class="active">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/courses">Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/registrations">Registrations</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="btn-logout" onclick="event.preventDefault(); confirmLogout(this.href);">Logout</a></li>
            </ul>
        </nav>
        <div class="container fade-in">
            <div class="page-header">
                <h1>Add New Student</h1>
                <p>Fill in the details to register a new student.</p>
            </div>
            <div class="form-card">
                <c:if test="${not empty error}">
                    <div class="alert alert-error">&#x26A0; ${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/student/add" method="POST" id="addStudentForm">
                    <div class="form-group">
                        <label for="studentName">Student Name <span class="required">*</span></label>
                        <input type="text" id="studentName" name="studentName" class="form-control"
                               placeholder="Enter student full name" value="${studentName}" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Email <span class="required">*</span></label>
                        <input type="email" id="email" name="email" class="form-control"
                               placeholder="student@example.com" value="${email}" required>
                    </div>
                    <div class="form-group">
                        <label for="phone">Phone <span class="required">*</span></label>
                        <input type="text" id="phone" name="phone" class="form-control"
                               placeholder="Enter phone number" value="${phone}" required>
                    </div>
                    <div class="form-group">
                        <label for="age">Age <span class="required">*</span> (Must be 18 or above)</label>
                        <input type="number" id="age" name="age" class="form-control"
                               placeholder="Enter age" value="${age}" min="18" required>
                    </div>
                    <div class="form-group">
                        <label for="city">City <span class="required">*</span></label>
                        <input type="text" id="city" name="city" class="form-control"
                               placeholder="Enter city" value="${city}" required>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">&#x2795; Add Student</button>
                        <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
        <footer class="footer">&copy; 2026 Student Course Registration & Management System</footer>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
