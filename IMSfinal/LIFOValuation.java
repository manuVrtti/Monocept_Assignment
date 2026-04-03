package com.Day29.IMSfinal;

class LIFOValuation implements ValuationStrategy {
    public double calculate(Product p) {
        return p.quantity * (p.price + 5); // dummy difference
    }
}