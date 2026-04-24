package com.project.app.app;

import java.util.Scanner;
import com.project.app.service.StudentService;

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        StudentService service = new StudentService();

        while (true) {

            System.out.println("\n===== MENU =====");
            System.out.println("1. Add Student");
            System.out.println("2. Register/Update Course");
            System.out.println("3. View All");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1:
                    System.out.print("ID: ");
                    int id = Integer.parseInt(sc.nextLine());

                    String name;
                    while (true) {
                        System.out.print("Name: ");
                        name = sc.nextLine();
                        if (name.matches("[a-zA-Z ]+")) break;
                        System.out.println("❌ Only alphabets allowed!");
                    }

                    System.out.print("Age: ");
                    int age = Integer.parseInt(sc.nextLine());

                    System.out.println("Select Department:");
                    System.out.println("1. IT\n2. CSE\n3. AI");

                    int d = Integer.parseInt(sc.nextLine());
                    String branch = (d == 1) ? "IT" : (d == 2) ? "CSE" : "AI";

                    if (service.studentExists(id)) {

                        System.out.println("⚠ Student exists!");
                        System.out.println("1. Update\n2. Back");

                        int op = Integer.parseInt(sc.nextLine());

                        if (op == 1)
                            System.out.println(service.updateStudent(id, name, age, branch));

                        break;
                    }

                    System.out.println(service.addStudent(id, name, age, branch));
                    break;

                case 2:
                    System.out.print("Student ID: ");
                    int sid = Integer.parseInt(sc.nextLine());

                    if (!service.studentExists(sid)) {
                        System.out.println("❌ Enter registered ID first!");
                        break;
                    }

                    // 🔥 AUTO FETCH BRANCH
                    String branch2 = service.getStudentBranch(sid);
                    System.out.println("Student Branch: " + branch2);

                    int courseId = 0;

                    switch (branch2) {

                        case "IT":
                            System.out.println("1. Java\n2. Python");
                            int c1 = Integer.parseInt(sc.nextLine());
                            courseId = (c1 == 1) ? 101 : 102;
                            break;

                        case "CSE":
                            System.out.println("1. DS\n2. OS");
                            int c2 = Integer.parseInt(sc.nextLine());
                            courseId = (c2 == 1) ? 201 : 202;
                            break;

                        case "AI":
                            System.out.println("1. ML\n2. DL");
                            int c3 = Integer.parseInt(sc.nextLine());
                            courseId = (c3 == 1) ? 301 : 302;
                            break;

                        default:
                            System.out.println("❌ Invalid branch!");
                            break;
                    }

                    System.out.print("Fee: ");
                    double fee = Double.parseDouble(sc.nextLine());

                    System.out.println(service.registerOrUpdateCourse(sid, courseId, fee));
                    break;

                case 3:
                    service.viewAll();
                    break;

                case 4:
                    System.out.print("Enter ID: ");
                    int did = Integer.parseInt(sc.nextLine());
                    System.out.println(service.deleteStudent(did));
                    break;

                case 5:
                    return;
            }
        }
    }
}