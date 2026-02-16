package com.example.laba1.main

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Firm")
data class Firm(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "tonnage")
    var tonnage: Double,
    @ColumnInfo(name = "tariff")
    var tariff: String,
    @ColumnInfo(name = "tariffPrice")
    var tariffPrice: Double
)