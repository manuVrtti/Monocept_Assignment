<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Student Course Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="page-wrapper">
        <nav class="navbar">
            <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">&#x1F393; StudentCourse</a>
            <ul class="navbar-nav">
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/login">Login</a></li>
            </ul>
        </nav>
        <div class="container">
            <div class="error-wrapper fade-in">
                <div>
                    <h1>Oops!</h1>
                    <p>Something went wrong. Please try again or go back to the dashboard.</p>
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">&#x1F3E0; Go to Dashboard</a>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary" style="margin-left: 12px;">&#x1F512; Login Page</a>
                </div>
            </div>
        </div>
        <footer class="footer">&copy; 2026 Student Course Registration & Management System</footer>
    </div>
</body>
</html>
