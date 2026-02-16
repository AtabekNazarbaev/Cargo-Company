package com.example.laba1.welcome

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.laba1.R
import com.example.laba1.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnTariffs.setOnClickListener {
                findNavController().navigate(R.id.action_welcomeFragment_to_tariffsFragment)
            }
            btnFirms.setOnClickListener {
                findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
            }
        }
    }
}