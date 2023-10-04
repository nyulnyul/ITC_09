package com.example.itc_football.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.ItemActivityBinding

class ItemActivity : AppCompatActivity(){
    private lateinit var binding: ItemActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

    }
}