package com.example.laba1.tarrif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TariffViewModel(
    private val repository: TariffRepository
) : ViewModel() {

    val tariffs: StateFlow<List<Tariff>> =
        repository.tariffs
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )



    val tariffNames: StateFlow<List<String>> =
        tariffs.map { it.map(Tariff::name) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateDiscount(tariffId: Int, discount: Int) {
        viewModelScope.launch {
            repository.updateDiscount(tariffId, discount)
        }
    }

    fun addTariff(tariff: Tariff) {
        viewModelScope.launch {
            repository.insertTariff(tariff)
        }
    }

    fun deleteTariff(tariff: Tariff) {
        viewModelScope.launch {
            repository.deleteTariff(tariff)
        }
    }

    fun getTariffPriceByName(name: String): Double? {
        return tariffs.value.firstOrNull { it.name == name }?.price
    }
    suspend fun insertTariffsFromJson(tariffs: List<Tariff>) {
        viewModelScope.launch {
            repository.insertTariffs(tariffs)
        }
    }
}

