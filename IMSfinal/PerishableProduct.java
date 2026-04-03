package com.Day29.IMSfinal;

class PerishableProduct extends Product {
    String expiryDate;

    PerishableProduct(int id, String name, int quantity, int threshold, double price, String expiryDate) {
        super(id, name, quantity, threshold, price);
        this.expiryDate = expiryDate;
    }
}