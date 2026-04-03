package com.Day29.IMSfinal;

class ReorderService {
    void reorder(Product p) {
        System.out.println("Reorder placed for 20 units of '" + p.name + "'");
        p.quantity += 20;
    }
}