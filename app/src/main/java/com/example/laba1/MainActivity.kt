package com.example.laba1

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.laba1.databinding.ActivityMainBinding
import com.example.laba1.main.MainFragment
import com.example.laba1.tarrif.TariffViewModel
import com.example.laba1.tarrif.TariffsFragment
import com.example.laba1.welcome.WelcomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}

