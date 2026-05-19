package com.studentcourse.controller;

import com.studentcourse.dao.StudentDAO;
import com.studentcourse.model.Student;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AddStudentServlet - Handles GET and POST /student/add
 * GET: Shows the add student form.
 * POST: Validates input and inserts student into database.
 */
public class AddStudentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("AddStudentServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AddStudentServlet: doGet() called - Showing add student form.");

        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-form.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("AddStudentServlet: doPost() called - Processing add student.");

        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String studentName = request.getParameter("studentName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String ageStr = request.getParameter("age");
        String city = request.getParameter("city");

        // Validation
        StringBuilder error = new StringBuilder();

        if (studentName == null || studentName.trim().isEmpty()) {
            error.append("Student Name is required. ");
        }
        if (email == null || email.trim().isEmpty()) {
            error.append("Email is required. ");
        }
        if (phone == null || phone.trim().isEmpty()) {
            error.append("Phone is required. ");
        } else if (!phone.trim().matches("^[6-9]\\d{9}$")) {
            error.append("Phone must be exactly 10 digits and start with 6, 7, 8, or 9. ");
        }
        if (city == null || city.trim().isEmpty()) {
            error.append("City is required. ");
        }

        int age = 0;
        if (ageStr == null || ageStr.trim().isEmpty()) {
            error.append("Age is required. ");
        } else {
            try {
                age = Integer.parseInt(ageStr.trim());
                if (age < 18) {
                    error.append("Age must be 18 or above. ");
                }
            } catch (NumberFormatException e) {
                error.append("Age must be a valid number. ");
            }
        }

        // If basic validation fails, forward back to form with error
        if (error.length() > 0) {
            request.setAttribute("error", error.toString().trim());
            request.setAttribute("studentName", studentName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("age", ageStr);
            request.setAttribute("city", city);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Duplicate checks
        StudentDAO studentDAO = new StudentDAO();
        StringBuilder dupError = new StringBuilder();

        if (studentDAO.isStudentNameExists(studentName.trim())) {
            dupError.append("A student with this name already exists. ");
        }
        if (studentDAO.isEmailExists(email.trim())) {
            dupError.append("This email is already registered. ");
        }
        if (studentDAO.isPhoneExists(phone.trim())) {
            dupError.append("This phone number is already registered. ");
        }

        if (dupError.length() > 0) {
            request.setAttribute("error", dupError.toString().trim());
            request.setAttribute("studentName", studentName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("age", ageStr);
            request.setAttribute("city", city);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Create and insert student
        Student student = new Student();
        student.setStudentName(studentName.trim());
        student.setEmail(email.trim());
        student.setPhone(phone.trim());
        student.setAge(age);
        student.setCity(city.trim());

        boolean success = studentDAO.addStudent(student);

        if (success) {
            HttpSession sess = request.getSession();
            sess.setAttribute("toastMessage", "Student '" + studentName.trim() + "' added successfully!");
            sess.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/students");
        } else {
            request.setAttribute("error", "Failed to add student. Please try again.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("AddStudentServlet: destroy() called - Servlet destroyed.");
    }
}
