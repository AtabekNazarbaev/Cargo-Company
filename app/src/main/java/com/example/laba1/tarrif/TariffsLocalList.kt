package com.example.laba1.tarrif

class TariffsLocalList {
    fun listOfTariffs(): List<Tariff> {
        var list = mutableListOf<Tariff>()
        for (i in 1..10) {
            list.add(Tariff(i, "tariff$i", i * 17.0, 0))
        }
        return list
    }
}