package com.studentcourse.controller;

import com.studentcourse.dao.CourseDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * DeleteCourseServlet - Handles GET /course/delete?id=X
 * Checks if course has active registrations before deleting.
 * Per SRS: Delete course only if not actively assigned.
 */
public class DeleteCourseServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("DeleteCourseServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DeleteCourseServlet: doGet() called - Processing course delete.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/courses");
            return;
        }

        try {
            int courseId = Integer.parseInt(idStr.trim());
            CourseDAO courseDAO = new CourseDAO();

            // Check if course has active registrations
            if (courseDAO.hasActiveRegistrations(courseId)) {
                request.setAttribute("error", "Cannot delete course. Course has active registrations. Please remove registrations first.");
                request.setAttribute("courses", courseDAO.getAllCourses());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-list.jsp");
                dispatcher.forward(request, response);
                return;
            }

            boolean success = courseDAO.deleteCourse(courseId);

            if (success) {
                session.setAttribute("toastMessage", "Course deleted successfully!");
                session.setAttribute("toastType", "success");
                response.sendRedirect(request.getContextPath() + "/courses");
            } else {
                request.setAttribute("error", "Failed to delete course. Course not found.");
                request.setAttribute("courses", courseDAO.getAllCourses());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-list.jsp");
                dispatcher.forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/courses");
        }
    }

    @Override
    public void destroy() {
        System.out.println("DeleteCourseServlet: destroy() called - Servlet destroyed.");
    }
}
