package com.project.app.dao;

import java.sql.*;
import com.project.app.util.DBUtil;

public class RegistrationDAO {

    public boolean isAlreadyRegistered(int studentId, int courseId) {
        try (Connection con = DBUtil.getConnection()) {

            String query = "SELECT * FROM registration WHERE student_id=? AND course_id=?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            return ps.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }

    public boolean registerCourse(Connection con, int studentId, int courseId, double fee) throws Exception {

        String query = "INSERT INTO registration(student_id, course_id, fees_paid) VALUES (?,?,?)";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setInt(1, studentId);
        ps.setInt(2, courseId);
        ps.setDouble(3, fee);

        return ps.executeUpdate() > 0;
    }

    public boolean updateCourse(int studentId, int courseId, double fee) {

        try (Connection con = DBUtil.getConnection()) {

            String query = "UPDATE registration SET fees_paid=? WHERE student_id=? AND course_id=?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setDouble(1, fee);
            ps.setInt(2, studentId);
            ps.setInt(3, courseId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteRegistrations(Connection con, int id) throws Exception {

        PreparedStatement ps = con.prepareStatement("DELETE FROM registration WHERE student_id=?");
        ps.setInt(1, id);
        ps.executeUpdate();

        return true;
    }

    public void viewAll() {

        try (Connection con = DBUtil.getConnection()) {

            String query =
            "SELECT s.id, s.name, s.branch, d.dept_name, c.course_name, r.fees_paid " +
            "FROM student s " +
            "LEFT JOIN registration r ON s.id = r.student_id " +
            "LEFT JOIN course c ON r.course_id = c.course_id " +
            "LEFT JOIN department d ON c.dept_id = d.dept_id";

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nID | Name | Branch | Dept | Course | Fee");
            System.out.println("--------------------------------------------------");

            while (rs.next()) {

                String dept = rs.getString(4);
                String course = rs.getString(5);

                if (dept == null) dept = "No Dept";
                if (course == null) course = "No Course";

                System.out.println(
                    rs.getInt(1)+" | "+
                    rs.getString(2)+" | "+
                    rs.getString(3)+" | "+
                    dept+" | "+
                    course+" | "+
                    rs.getDouble(6)
                );
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}