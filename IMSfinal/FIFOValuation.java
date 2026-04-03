package com.Day29.IMSfinal;

class FIFOValuation implements ValuationStrategy {
    public double calculate(Product p) {
        return p.quantity * p.price;
    }
}