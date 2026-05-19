package com.studentcourse.controller;

import com.studentcourse.dao.CourseDAO;
import com.studentcourse.model.Course;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * EditCourseServlet - Handles GET /course/edit?id=X
 * Loads the course data and forwards to course-edit.jsp.
 */
public class EditCourseServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("EditCourseServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("EditCourseServlet: doGet() called - Loading course for edit.");

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
            Course course = courseDAO.getCourseById(courseId);

            if (course != null) {
                request.setAttribute("course", course);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-edit.jsp");
                dispatcher.forward(request, response);
            } else {
                request.setAttribute("error", "Course not found with ID: " + courseId);
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
        System.out.println("EditCourseServlet: destroy() called - Servlet destroyed.");
    }
}
