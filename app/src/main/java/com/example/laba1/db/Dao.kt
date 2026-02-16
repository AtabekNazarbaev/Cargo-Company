package com.example.laba1.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laba1.main.Firm
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT * FROM Firm")
    fun getFirms(): Flow<List<Firm>>

    @Insert()
    suspend fun insertFirm(firm: Firm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFirms(firms: List<Firm>)

    @Delete
    suspend fun delete(firm: Firm)
}
