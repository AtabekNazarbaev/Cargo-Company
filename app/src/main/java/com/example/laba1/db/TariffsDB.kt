package com.example.laba1.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.laba1.tarrif.Tariff

@Database(entities = [Tariff::class], version = 1)
abstract class TariffsDB : RoomDatabase() {
    abstract fun getTariffDao(): TariffDao

    companion object {
        @Volatile
        private var INSTANCE: TariffsDB? = null
        fun getDB(context: Context): TariffsDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TariffsDB::class.java,
                    "tariffs.db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}