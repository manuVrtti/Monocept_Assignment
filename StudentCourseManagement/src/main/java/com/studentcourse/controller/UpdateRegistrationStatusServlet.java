package com.studentcourse.controller;

import com.studentcourse.dao.RegistrationDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * UpdateRegistrationStatusServlet - Handles POST /registration/status
 * Updates the status of an existing registration.
 */
public class UpdateRegistrationStatusServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("UpdateRegistrationStatusServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("UpdateRegistrationStatusServlet: doPost() called - Updating registration status.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String registrationIdStr = request.getParameter("registrationId");
        String status = request.getParameter("status");

        if (registrationIdStr == null || status == null ||
            registrationIdStr.trim().isEmpty() || status.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/registrations");
            return;
        }

        try {
            int registrationId = Integer.parseInt(registrationIdStr.trim());

            RegistrationDAO registrationDAO = new RegistrationDAO();
            registrationDAO.updateStatus(registrationId, status.trim());

            session.setAttribute("toastMessage", "Registration status updated successfully!");
            session.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/registrations");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/registrations");
        }
    }

    @Override
    public void destroy() {
        System.out.println("UpdateRegistrationStatusServlet: destroy() called - Servlet destroyed.");
    }
}
