package com.example.laba1

import com.example.laba1.main.Firm

class CargoRevenue(private val pricePerTon: Double = 1000.0) {
    private val firms: MutableList<Firm> = mutableListOf()
    private var nextId: Int = 0

    fun addFirm(name: String, tonnage: Double): Firm? {
        if (name.isBlank() || tonnage <= 0.0) {
            return null
        }
        val firm = Firm(nextId++, name, tonnage,"",0.0)
        firms.add(firm)
        return firm
    }

    fun getFirms(): List<Firm> = firms.toList()

    fun calculateTotalRevenue(): Double {
        return firms.sumOf { it.tonnage * pricePerTon }
    }
}
