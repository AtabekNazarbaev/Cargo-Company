package com.example.laba1.helper

import android.content.Context
import android.widget.Toast

object Singleton {
    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}