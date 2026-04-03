package com.Day29.IMSfinal;

import java.util.*;

class App {

    Scanner sc = new Scanner(System.in);
    InventoryService service = new InventoryService();

    void start() {

        service.addNotifier(new EmailNotifier());
        service.addNotifier(new SMSNotifier());

        while (true) {
            System.out.println("\n1.Add 2.Remove 3.Value 4.Exit");
            System.out.print("Enter choice: ");

            int ch = getInt();

            if (ch == 1) addProduct();
            else if (ch == 2) removeStock();
            else if (ch == 3) value();
            else if (ch == 4) break;
            else System.out.println("Invalid choice!");
        }
    }


    int getInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.print(" Invalid number, re-enter: ");
            }
        }
    }

    double getDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (Exception e) {
                System.out.print(" Invalid number, re-enter: ");
            }
        }
    }

   
    void addProduct() {

        System.out.print("Enter ID: ");
        int id = getInt();

        Product existing = service.find(id);

        
        if (existing != null) {
            System.out.println("⚠ Product already exists: " + existing.name);
            System.out.print("Enter quantity to add: ");
            int qty = getInt();

            existing.quantity += qty;
            System.out.println(" Quantity updated! New stock: " + existing.quantity);
            return;
        }

       
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Quantity: ");
        int qty = getInt();

        System.out.print("Enter Threshold: ");
        int th = getInt();

        System.out.print("Enter Price: ");
        double price = getDouble();

        service.addProduct(new Product(id, name, qty, th, price));
    }

  
    void removeStock() {

        while (true) {

            System.out.print("Enter Product ID: ");
            int id = getInt();

            Product p = service.find(id);

           
            if (p == null) {
                System.out.println(" Invalid Product ID: " + id);
                System.out.println("1. Re-enter\n2. Back to Menu");

                int ch = getInt();

                if (ch == 1) continue;
                else return;
            }

           
            System.out.println("Product Found: " + p.name);
            System.out.print("Enter no. of Stock remove - ");
            int qty = getInt();

            service.removeStock(id, qty);
            return;
        }
    }

 
    void value() {

        System.out.println("1.FIFO 2.LIFO");
        int ch = getInt();

        if (ch == 1)
            service.calculateValue(new FIFOValuation());
        else
            service.calculateValue(new LIFOValuation());
    }
}
