package com.example.laba1.helper

interface DiscountStrategy {
    fun calculate(discount: Double, amount: Double): Double
}