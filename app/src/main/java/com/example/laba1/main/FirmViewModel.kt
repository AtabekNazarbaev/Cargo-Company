package com.example.laba1.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FirmViewModel(
    private val repository: FirmRepository
) : ViewModel() {

    val firms: StateFlow<List<Firm>> = repository.firms.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalRevenue: StateFlow<Double> =
        firms.map { list -> list.sumOf { it.tonnage * it.tariffPrice } }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0
        )

    fun addFirm(firm: Firm) {
        viewModelScope.launch {
            repository.insertFirm(firm)
        }
    }

    suspend fun insertFirmsFromJson(firms: List<Firm>) {
        viewModelScope.launch {
            repository.insertFirms(firms)
        }
    }


    fun deleteFirm(firm: Firm) {
        viewModelScope.launch {
            repository.deleteFirm(firm)
        }
    }
}
