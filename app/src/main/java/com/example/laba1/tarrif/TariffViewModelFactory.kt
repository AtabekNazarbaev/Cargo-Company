package com.example.laba1.tarrif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TariffViewModelFactory(
    private val repository: TariffRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TariffViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TariffViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
