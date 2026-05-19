<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Student Course Management System</title>
    <meta name="description" content="Admin login for Student Course Registration and Management System">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="login-wrapper">
        <div class="login-card">
            <div class="login-header">
                <div class="login-logo">&#x1F393;</div>
                <h1>Welcome Back</h1>
                <p class="login-subtitle">Student Course Management System</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-error">&#x26A0; ${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login-action" method="POST" id="loginForm">
                <div class="form-group">
                    <label for="username">Username <span class="required">*</span></label>
                    <input type="text" id="username" name="username" class="form-control"
                           placeholder="Enter your username"
                           value="${not empty rememberedUsername ? rememberedUsername : ''}" required>
                </div>

                <div class="form-group">
                    <label for="password">Password <span class="required">*</span></label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Enter your password" required>
                </div>

                <div class="form-group">
                    <div class="checkbox-group">
                        <input type="checkbox" id="rememberMe" name="rememberMe"
                               ${not empty rememberedUsername ? 'checked' : ''}>
                        <label for="rememberMe">Remember Username</label>
                    </div>
                </div>

                <div class="form-actions" style="border: none; padding-top: 8px;">
                    <button type="submit" class="btn btn-primary" style="width: 100%; justify-content: center;">
                        &#x1F512; Sign In
                    </button>
                </div>
            </form>
        </div>
    </div>
    <jsp:include page="common-ui.jsp" />
</body>
</html>
