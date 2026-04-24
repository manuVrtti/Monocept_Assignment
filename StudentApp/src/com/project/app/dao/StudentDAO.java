package com.project.app.dao;

import java.sql.*;
import com.project.app.util.DBUtil;
import com.project.app.model.Student;

public class StudentDAO {

    public boolean addStudent(Student s) {

        try (Connection con = DBUtil.getConnection()) {

            String check = "SELECT * FROM student WHERE id=?";
            PreparedStatement ps1 = con.prepareStatement(check);
            ps1.setInt(1, s.getId());

            if (ps1.executeQuery().next()) return false;

            String query = "INSERT INTO student VALUES (?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, s.getId());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getAge());
            ps.setString(4, s.getBranch());

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean studentExists(int id) {
        try (Connection con = DBUtil.getConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM student WHERE id=?");
            ps.setInt(1, id);

            return ps.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateStudent(int id, String name, int age, String branch) {

        try (Connection con = DBUtil.getConnection()) {

            String query = "UPDATE student SET name=?, age=?, branch=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, branch);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteStudent(Connection con, int id) throws Exception {

        PreparedStatement ps = con.prepareStatement("DELETE FROM student WHERE id=?");
        ps.setInt(1, id);

        return ps.executeUpdate() > 0;
    }

    // 🔥 NEW METHOD (IMPORTANT)
    public String getBranchById(int id) {

        try (Connection con = DBUtil.getConnection()) {

            String query = "SELECT branch FROM student WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("branch");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}