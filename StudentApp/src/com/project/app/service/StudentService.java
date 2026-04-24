package com.project.app.service;

import java.sql.Connection;
import com.project.app.dao.*;
import com.project.app.model.Student;
import com.project.app.util.DBUtil;

public class StudentService {

    StudentDAO sdao = new StudentDAO();
    RegistrationDAO rdao = new RegistrationDAO();

    public boolean studentExists(int id) {
        return sdao.studentExists(id);
    }

    public String getStudentBranch(int id) {
        return sdao.getBranchById(id);
    }

    public String addStudent(int id, String name, int age, String branch) {

        if (name.trim().isEmpty() || branch.trim().isEmpty() || age <= 0)
            return "❌ Invalid input!";

        Student s = new Student(id, name, age, branch);

        if (sdao.addStudent(s))
            return "✅ Student added!";
        else
            return "❌ Student already exists!";
    }

    public String updateStudent(int id, String name, int age, String branch) {

        return sdao.updateStudent(id, name, age, branch)
                ? "✅ Student Updated!"
                : "❌ Update Failed!";
    }

    public String registerOrUpdateCourse(int studentId, int courseId, double fee) {

        if (!sdao.studentExists(studentId))
            return "❌ Enter registered ID first!";

        if (fee <= 0)
            return "❌ Fee must be positive!";

        if (rdao.isAlreadyRegistered(studentId, courseId)) {

            if (rdao.updateCourse(studentId, courseId, fee))
                return "⚠ Course Updated!";
            else
                return "❌ Update Failed!";
        }

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            boolean result = rdao.registerCourse(con, studentId, courseId, fee);

            if (!result) {
                con.rollback();
                return "❌ Failed!";
            }

            con.commit();
            return "✅ Registered!";

        } catch (Exception e) {
            return "❌ Transaction Failed!";
        }
    }

    public String deleteStudent(int id) {

        if (!sdao.studentExists(id))
            return "❌ Student not found!";

        try (Connection con = DBUtil.getConnection()) {

            con.setAutoCommit(false);

            rdao.deleteRegistrations(con, id);
            boolean deleted = sdao.deleteStudent(con, id);

            if (!deleted) {
                con.rollback();
                return "❌ Delete failed!";
            }

            con.commit();
            return "✅ Student deleted!";

        } catch (Exception e) {
            return "❌ Transaction failed!";
        }
    }

    public void viewAll() {
        rdao.viewAll();
    }
}