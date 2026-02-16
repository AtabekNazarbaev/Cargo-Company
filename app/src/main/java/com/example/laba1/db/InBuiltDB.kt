package com.example.laba1.db

import com.example.laba1.main.Firm
import com.example.laba1.tarrif.Tariff

class InBuiltDB {
    fun getTariffs(): List<Tariff> {
        var list = mutableListOf<Tariff>()
        for (i in 1..10) {
            list.add(Tariff(i, "tariff$i", i * 10.0, 0))
        }
        return list
    }

    fun getFirms(): List<Firm> {
        var list = mutableListOf<Firm>()
        for (i in 1..10) {
            list.add(Firm(i, "firm$i", i * 10.0, "Inbuilt", i * 10.0))
        }
        return list
    }
}