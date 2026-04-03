package com.Day29.IMSfinal;

import java.util.*;

class InventoryService {
    ArrayList<Product> list = new ArrayList<>();
    ArrayList<Notifier> notifiers = new ArrayList<>();
    ReorderService reorderService = new ReorderService();

    void addNotifier(Notifier n) {
        notifiers.add(n);
    }

    void addProduct(Product p) {
        list.add(p);
        System.out.println("Product Added!");
    }

    Product find(int id) {
        for (Product p : list) {
            if (p.id == id) return p;
        }
        return null;
    }

    void removeStock(int id, int qty) {
        Product p = find(id);

        if (p == null) {
            System.out.println("Product not found!");
            return;
        }

        if (qty > p.quantity) {
            System.out.println("Not enough stock!");
            return;
        }

        p.quantity -= qty;

        System.out.println("Removed " + qty + " units of " + p.name);
        System.out.println("Current stock: " + p.quantity);

        if (p.quantity < p.threshold) {
            System.out.println("⚠ Low stock! Reordering...");

            reorderService.reorder(p);

            for (Notifier n : notifiers) {
                n.send("Low stock alert for " + p.name);
            }
        }
    }

    void calculateValue(ValuationStrategy vs) {
        double total = 0;

        for (Product p : list) {
            total += vs.calculate(p);
        }

        System.out.println("Total Inventory Value: " + total);
    }
}