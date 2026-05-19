<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Courses - Student Course Management</title>
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
                <h1>Course Management</h1>
                <p>View, edit, and manage all courses.</p>
            </div>
            <c:if test="${not empty error}">
                <div class="alert alert-error">&#x26A0; ${error}</div>
            </c:if>
            <div class="table-wrapper">
                <div class="table-header">
                    <h2>All Courses (${courses.size()})</h2>
                    <a href="${pageContext.request.contextPath}/course/add" class="btn btn-primary btn-sm">&#x2795; Add Course</a>
                </div>
                <c:choose>
                    <c:when test="${not empty courses}">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th><th>Course Name</th><th>Duration</th>
                                    <th>Fees</th><th>Trainer</th><th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${courses}">
                                    <tr>
                                        <td>${c.courseId}</td>
                                        <td style="color: var(--text-primary); font-weight: 600;">${c.courseName}</td>
                                        <td>${c.duration}</td>
                                        <td>&#x20B9; <fmt:formatNumber value="${c.fees}" type="number" minFractionDigits="2"/></td>
                                        <td>${c.trainerName}</td>
                                        <td>
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/course/edit?id=${c.courseId}" class="btn btn-warning btn-sm">Edit</a>
                                                <a href="${pageContext.request.contextPath}/course/delete?id=${c.courseId}" class="btn btn-danger btn-sm"
                                                   onclick="event.preventDefault(); confirmDelete(this.href, '${c.courseName.replace("\'", "\\\'")}');">Delete</a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="table-empty">No courses found. Click "Add Course" to get started.</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <footer class="footer">&copy; 2026 Student Course Registration & Management System</footer>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
