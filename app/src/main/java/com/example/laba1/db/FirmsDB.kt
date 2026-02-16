package com.example.laba1.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.laba1.main.Firm

@Database(entities = [Firm::class], version = 1)
abstract class FirmsDB : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: FirmsDB? = null
        fun getDB(context: Context): FirmsDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FirmsDB::class.java,
                    "firms.db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}