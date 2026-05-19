<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Registration - Student Course Management</title>
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
                <h1>New Registration</h1>
                <p>Register a student for a course.</p>
            </div>
            <div class="form-card">
                <c:if test="${not empty error}">
                    <div class="alert alert-error">&#x26A0; ${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/registration/register" method="POST" id="registrationForm">
                    <div class="form-group">
                        <label for="studentId">Select Student <span class="required">*</span></label>
                        <select id="studentId" name="studentId" class="form-control" required>
                            <option value="">-- Choose Student --</option>
                            <c:forEach var="s" items="${students}">
                                <option value="${s.studentId}" ${selectedStudentId == s.studentId ? 'selected' : ''}>
                                    ${s.studentId} - ${s.studentName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="courseId">Select Course <span class="required">*</span></label>
                        <select id="courseId" name="courseId" class="form-control" required>
                            <option value="">-- Choose Course --</option>
                            <c:forEach var="c" items="${courses}">
                                <option value="${c.courseId}" ${selectedCourseId == c.courseId ? 'selected' : ''}>
                                    ${c.courseId} - ${c.courseName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="registrationDate">Registration Date <span class="required">*</span></label>
                        <input type="date" id="registrationDate" name="registrationDate" class="form-control"
                               value="${registrationDate}" required>
                    </div>
                    <div class="form-group">
                        <label for="status">Status <span class="required">*</span></label>
                        <select id="status" name="status" class="form-control" required>
                            <option value="">-- Select Status --</option>
                            <option value="Active" ${selectedStatus == 'Active' ? 'selected' : ''}>Active</option>
                            <option value="Completed" ${selectedStatus == 'Completed' ? 'selected' : ''}>Completed</option>
                            <option value="Cancelled" ${selectedStatus == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">&#x1F517; Register</button>
                        <a href="${pageContext.request.contextPath}/registrations" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
        <footer class="footer">&copy; 2026 Student Course Registration & Management System</footer>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
