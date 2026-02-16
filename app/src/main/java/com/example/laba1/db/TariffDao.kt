package com.example.laba1.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laba1.tarrif.Tariff
import kotlinx.coroutines.flow.Flow

@Dao
interface TariffDao {

    @Query("SELECT * FROM Tariffs")
    fun getTariffs(): Flow<List<Tariff>>

    @Query("SELECT name FROM Tariffs")
    suspend fun getTariffsNames(): List<String>

    @Query("SELECT * FROM Tariffs WHERE name = :name")
    suspend fun getTariffByName(name: String): Tariff?

    @Insert
    suspend fun insertTariff(tariff: Tariff)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTariffs(tariffs: List<Tariff>)

    @Delete
    suspend fun deleteTariff(tariff: Tariff)

    @Query("UPDATE Tariffs SET discount = :newDiscount WHERE id = :tariffId")
    suspend fun updateDiscount(tariffId: Int, newDiscount: Int)
}
