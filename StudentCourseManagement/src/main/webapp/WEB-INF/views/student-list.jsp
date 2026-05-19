<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Students - Student Course Management</title>
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
                <h1>Student Management</h1>
                <p>View, edit, and manage all student records.</p>
            </div>
            <c:if test="${not empty error}">
                <div class="alert alert-error">&#x26A0; ${error}</div>
            </c:if>
            <div class="table-wrapper">
                <div class="table-header">
                    <h2>All Students (${students.size()})</h2>
                    <a href="${pageContext.request.contextPath}/student/add" class="btn btn-primary btn-sm">&#x2795; Add Student</a>
                </div>
                <c:choose>
                    <c:when test="${not empty students}">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th><th>Name</th><th>Email</th>
                                    <th>Phone</th><th>Age</th><th>City</th><th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${students}">
                                    <tr>
                                        <td>${s.studentId}</td>
                                        <td style="color: var(--text-primary); font-weight: 600;">${s.studentName}</td>
                                        <td>${s.email}</td>
                                        <td>${s.phone}</td>
                                        <td>${s.age}</td>
                                        <td>${s.city}</td>
                                        <td>
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/student/edit?id=${s.studentId}" class="btn btn-warning btn-sm">Edit</a>
                                                <a href="${pageContext.request.contextPath}/student/delete?id=${s.studentId}" class="btn btn-danger btn-sm"
                                                   onclick="event.preventDefault(); confirmDelete(this.href, '${s.studentName.replace("\'", "\\\'")}');">Delete</a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="table-empty">No students found. Click "Add Student" to get started.</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <footer class="footer">&copy; 2026 Student Course Registration & Management System</footer>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
