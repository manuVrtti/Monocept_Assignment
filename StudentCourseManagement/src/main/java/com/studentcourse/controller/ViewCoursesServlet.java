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
import java.util.List;

/**
 * ViewCoursesServlet - Handles GET /courses
 * Fetches all courses and forwards to course-list.jsp.
 */
public class ViewCoursesServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("ViewCoursesServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("ViewCoursesServlet: doGet() called - Loading course list.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CourseDAO courseDAO = new CourseDAO();
        List<Course> courses = courseDAO.getAllCourses();

        request.setAttribute("courses", courses);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/course-list.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("ViewCoursesServlet: destroy() called - Servlet destroyed.");
    }
}
