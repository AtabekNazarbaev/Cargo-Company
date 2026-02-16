package com.example.laba1.main

import com.example.laba1.db.Dao
import kotlinx.coroutines.flow.Flow

class FirmRepository(
    private val dao: Dao
) {

    val firms: Flow<List<Firm>> = dao.getFirms()

    suspend fun insertFirm(firm: Firm) {
        dao.insertFirm(firm)
    }

    suspend fun insertFirms(firms: List<Firm>) {
        dao.insertFirms(firms)
    }

    suspend fun deleteFirm(firm: Firm) {
        dao.delete(firm)
    }
}
