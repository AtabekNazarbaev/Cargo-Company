package com.example.laba1.helper

class PercentageDiscount : DiscountStrategy {

    override fun calculate(discount: Double, amount: Double): Double {
        return amount-((amount * discount)/100)
    }
}
