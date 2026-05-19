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
 * UpdateStudentServlet - Handles POST /student/update
 * Validates input and updates student in the database.
 */
public class UpdateStudentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("UpdateStudentServlet: init() called - Servlet initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("UpdateStudentServlet: doPost() called - Processing student update.");

        // Session check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String studentIdStr = request.getParameter("studentId");
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
        int studentId = 0;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

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

        // If validation fails, forward back to edit form with error
        if (error.length() > 0) {
            Student student = new Student();
            student.setStudentId(studentId);
            student.setStudentName(studentName);
            student.setEmail(email);
            student.setPhone(phone);
            student.setAge(age);
            student.setCity(city);

            request.setAttribute("error", error.toString().trim());
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-edit.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Duplicate checks (exclude current student ID from checks)
        StudentDAO studentDAO = new StudentDAO();
        StringBuilder dupError = new StringBuilder();

        if (studentName != null && studentDAO.isStudentNameExists(studentName.trim(), studentId)) {
            dupError.append("A student with this name already exists. ");
        }
        if (email != null && studentDAO.isEmailExists(email.trim(), studentId)) {
            dupError.append("This email is already registered to another student. ");
        }
        if (phone != null && studentDAO.isPhoneExists(phone.trim(), studentId)) {
            dupError.append("This phone number is already registered to another student. ");
        }

        if (dupError.length() > 0) {
            Student s = new Student();
            s.setStudentId(studentId);
            s.setStudentName(studentName);
            s.setEmail(email);
            s.setPhone(phone);
            s.setAge(age);
            s.setCity(city);

            request.setAttribute("error", dupError.toString().trim());
            request.setAttribute("student", s);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-edit.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Update student
        Student student = new Student();
        student.setStudentId(studentId);
        student.setStudentName(studentName.trim());
        student.setEmail(email.trim());
        student.setPhone(phone.trim());
        student.setAge(age);
        student.setCity(city.trim());

        boolean success = studentDAO.updateStudent(student);

        if (success) {
            HttpSession sess = request.getSession();
            sess.setAttribute("toastMessage", "Student updated successfully!");
            sess.setAttribute("toastType", "success");
            response.sendRedirect(request.getContextPath() + "/students");
        } else {
            request.setAttribute("error", "Failed to update student. Please try again.");
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/student-edit.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("UpdateStudentServlet: destroy() called - Servlet destroyed.");
    }
}
