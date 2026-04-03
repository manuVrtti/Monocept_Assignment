package com.Day29.IMSfinal;

class Product {
    int id;
    String name;
    int quantity;
    int threshold;
    double price;

    Product(int id, String name, int quantity, int threshold, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.price = price;
    }
}