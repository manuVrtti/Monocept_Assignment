package com.Day29.IMSfinal;

class NonPerishableProduct extends Product {
    int warranty;

    NonPerishableProduct(int id, String name, int quantity, int threshold, double price, int warranty) {
        super(id, name, quantity, threshold, price);
        this.warranty = warranty;
    }
}