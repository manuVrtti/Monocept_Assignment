# Student Course Registration & Management System

A complete Java Servlet-based web application for managing student course registrations, built with JSP, JDBC, MySQL, and deployed on Apache Tomcat.

## Prerequisites

- **JDK 8+** (Java Development Kit)
- **Apache Maven 3.6+**
- **MySQL 8.0+**
- **Apache Tomcat 9+**

## Database Setup

1. Open MySQL command line or a client (MySQL Workbench, DBeaver, etc.)
2. Run the schema file to create the database and tables:

```sql
source src/main/resources/schema.sql
```

Or copy and paste the contents of `src/main/resources/schema.sql` into your MySQL client.

This will:
- Create the `student_course_db` database
- Create all 4 tables (admin, students, courses, registrations)
- Insert the default admin credentials

## Configuration

Edit `src/main/java/com/studentcourse/util/DBConnection.java` if your MySQL credentials differ:

```java
private static final String URL = "jdbc:mysql://localhost:3306/student_course_db";
private static final String USERNAME = "root";     // Change if needed
private static final String PASSWORD = "root";     // Change if needed
```

## Build

```bash
mvn clean package
```

This generates `target/StudentCourseManagement.war`.

## Deployment

1. Copy `target/StudentCourseManagement.war` to your Tomcat `webapps/` directory
2. Start Tomcat
3. Open browser: `http://localhost:8080/StudentCourseManagement/`

## Default Login

| Username | Password |
|----------|----------|
| admin    | admin123 |

## Project Structure

```
StudentCourseManagement/
├── pom.xml
├── src/main/
│   ├── java/com/studentcourse/
│   │   ├── model/         (Admin, Student, Course, Registration)
│   │   ├── dao/           (AdminDAO, StudentDAO, CourseDAO, RegistrationDAO)
│   │   ├── controller/    (19 Servlet controllers)
│   │   └── util/          (DBConnection)
│   ├── resources/
│   │   └── schema.sql
│   └── webapp/
│       ├── css/style.css
│       ├── index.jsp
│       └── WEB-INF/
│           ├── web.xml
│           └── views/     (11 JSP pages)
```

## Features

- **Login Module** - Admin authentication with session & cookie management
- **Dashboard** - Summary stats with quick action links
- **Student CRUD** - Add, view, edit, delete students (with registration check)
- **Course CRUD** - Add, view, edit, delete courses (with registration check)
- **Registration CRUD** - Register students to courses, update status, delete
- **Validation** - Server-side validation on all forms
- **Security** - Session-based access control, PreparedStatements for SQL injection prevention
