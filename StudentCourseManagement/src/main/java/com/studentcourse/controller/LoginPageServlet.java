package com.studentcourse.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * LoginPageServlet - Handles GET /login
 * Reads remembered username cookie and forwards to login.jsp.
 * Demonstrates Servlet lifecycle methods with console logging.
 */
public class LoginPageServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("LoginPageServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("LoginPageServlet: doGet() called - Showing login page.");

        // Read remembered username from cookie
        String rememberedUsername = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberedUsername".equals(cookie.getName())) {
                    rememberedUsername = cookie.getValue();
                    break;
                }
            }
        }

        request.setAttribute("rememberedUsername", rememberedUsername);

        // Forward to login.jsp using RequestDispatcher
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("LoginPageServlet: destroy() called - Servlet destroyed.");
    }
}
