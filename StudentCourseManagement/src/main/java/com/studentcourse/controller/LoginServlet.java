package com.studentcourse.controller;

import com.studentcourse.dao.AdminDAO;
import com.studentcourse.model.Admin;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LoginServlet - Handles POST /login-action
 * Validates credentials, manages session and cookies, redirects or forwards with error.
 */
public class LoginServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("LoginServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("LoginServlet: doPost() called - Processing login.");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        // Validate empty fields
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            request.setAttribute("error", "Username and password are required.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Validate credentials against database
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.validateAdmin(username.trim(), password.trim());

        if (admin != null) {
            // Successful login - Create session
            HttpSession session = request.getSession();
            session.setAttribute("adminUsername", admin.getUsername());

            // Handle Remember Username cookie
            if ("on".equals(rememberMe)) {
                // Store only username in cookie (never password)
                Cookie cookie = new Cookie("rememberedUsername", admin.getUsername());
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days expiry
                cookie.setPath(request.getContextPath());
                response.addCookie(cookie);
            } else {
                // Delete existing remembered username cookie
                Cookie cookie = new Cookie("rememberedUsername", "");
                cookie.setMaxAge(0);
                cookie.setPath(request.getContextPath());
                response.addCookie(cookie);
            }

            // Set flash message for toast
            session.setAttribute("toastMessage", "Welcome back, " + admin.getUsername() + "!");
            session.setAttribute("toastType", "success");

            // Redirect to dashboard using sendRedirect
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } else {
            // Invalid login - Forward back to login page with error
            request.setAttribute("error", "Invalid username or password.");
            request.setAttribute("rememberedUsername", username);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("LoginServlet: destroy() called - Servlet destroyed.");
    }
}
