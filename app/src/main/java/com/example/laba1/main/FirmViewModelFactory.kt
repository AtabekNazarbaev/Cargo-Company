package com.example.laba1.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FirmViewModelFactory(
    private val repository: FirmRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FirmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
