package com.studentcourse.controller;

import com.studentcourse.dao.CourseDAO;
import com.studentcourse.dao.StudentDAO;
import com.studentcourse.model.Course;
import com.studentcourse.model.Student;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * RegistrationFormServlet - Handles GET /registration/add
 * Fetches students and courses for dropdown selection.
 * Forwards to registration-form.jsp.
 */
public class RegistrationFormServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("RegistrationFormServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("RegistrationFormServlet: doGet() called - Showing registration form.");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Fetch students and courses for dropdown
        StudentDAO studentDAO = new StudentDAO();
        CourseDAO courseDAO = new CourseDAO();

        List<Student> students = studentDAO.getAllStudents();
        List<Course> courses = courseDAO.getAllCourses();

        request.setAttribute("students", students);
        request.setAttribute("courses", courses);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registration-form.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("RegistrationFormServlet: destroy() called - Servlet destroyed.");
    }
}
