package com.studentcourse.controller;

import com.studentcourse.dao.RegistrationDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * DeleteRegistrationServlet - Handles GET /registration/delete?id=X
 * Deletes a registration record.
 */
public class DeleteRegistrationServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("DeleteRegistrationServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DeleteRegistrationServlet: doGet() called - Processing registration delete.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/registrations");
            return;
        }

        try {
            int registrationId = Integer.parseInt(idStr.trim());

            RegistrationDAO registrationDAO = new RegistrationDAO();
            registrationDAO.deleteRegistration(registrationId);

            session.setAttribute("toastMessage", "Registration deleted successfully!");
            session.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/registrations");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/registrations");
        }
    }

    @Override
    public void destroy() {
        System.out.println("DeleteRegistrationServlet: destroy() called - Servlet destroyed.");
    }
}
