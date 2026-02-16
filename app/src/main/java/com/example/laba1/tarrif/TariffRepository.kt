package com.example.laba1.tarrif

import com.example.laba1.db.TariffDao
import kotlinx.coroutines.flow.Flow

class TariffRepository(
    private val dao: TariffDao
) {

    val tariffs: Flow<List<Tariff>> = dao.getTariffs()

    suspend fun getTariffNames(): List<String> =
        dao.getTariffsNames()

    suspend fun getTariffByName(name: String): Tariff? =
        dao.getTariffByName(name)

    suspend fun insertTariff(tariff: Tariff) {
        dao.insertTariff(tariff)
    }

    suspend fun insertTariffs(tariffs: List<Tariff>) {
        dao.insertTariffs(tariffs)
    }

    suspend fun deleteTariff(tariff: Tariff) {
        dao.deleteTariff(tariff)
    }

    suspend fun updateDiscount(id: Int, discount: Int) {
        dao.updateDiscount(id, discount)
    }
}
