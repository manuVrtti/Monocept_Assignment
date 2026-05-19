-- ============================================================
-- Student Course Registration & Management System
-- Database Schema
-- ============================================================

-- Create and use the database
CREATE DATABASE IF NOT EXISTS student_course_db;
USE student_course_db;

-- ============================================================
-- 1. Admin Table
-- ============================================================
CREATE TABLE IF NOT EXISTS admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Seed default admin credentials
INSERT INTO admin (username, password)
SELECT 'admin', 'admin123'
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin');

-- ============================================================
-- 2. Students Table
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    age INT NOT NULL,
    city VARCHAR(50) NOT NULL
);

-- ============================================================
-- 3. Courses Table
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    fees DOUBLE NOT NULL,
    trainer_name VARCHAR(100) NOT NULL
);

-- ============================================================
-- 4. Registrations Table
-- ============================================================
CREATE TABLE IF NOT EXISTS registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    registration_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);
