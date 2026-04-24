
DROP DATABASE IF EXISTS studentdb;


CREATE DATABASE studentdb;
USE studentdb;


CREATE TABLE student (
 id INT PRIMARY KEY,
 name VARCHAR(50) NOT NULL,
 age INT CHECK (age > 0),
 branch VARCHAR(50) NOT NULL
);


CREATE TABLE department (
 dept_id INT PRIMARY KEY,
 dept_name VARCHAR(50) UNIQUE NOT NULL
);


CREATE TABLE course (
 course_id INT PRIMARY KEY,
 course_name VARCHAR(50) NOT NULL,
 dept_id INT,
 FOREIGN KEY (dept_id) REFERENCES department(dept_id)
);


CREATE TABLE registration (
 reg_id INT AUTO_INCREMENT PRIMARY KEY,
 student_id INT,
 course_id INT,
 fees_paid DOUBLE CHECK (fees_paid > 0),
 FOREIGN KEY (student_id) REFERENCES student(id),
 FOREIGN KEY (course_id) REFERENCES course(course_id)
);


INSERT INTO department VALUES
(1,'IT'),
(2,'CSE'),
(3,'AI');


INSERT INTO course VALUES
(101,'Java',1),
(102,'Python',1),
(201,'Data Structures',2),
(202,'Operating System',2),
(301,'Machine Learning',3),
(302,'Deep Learning',3);


SHOW TABLES;


SELECT * FROM department;
SELECT * FROM course;
SELECT * FROM student;
SELECT * FROM registration;