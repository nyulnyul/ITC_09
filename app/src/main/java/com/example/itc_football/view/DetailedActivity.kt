package com.example.itc_football.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itc_football.databinding.ItemDetailActivityBinding

class DetailedActivity : AppCompatActivity() {
    private lateinit var binding: ItemDetailActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemDetailActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.productName.text = intent.getStringExtra("productName")
        binding.productPrice.text = intent.getIntExtra("productPrice", 0).toString()
        binding.productImage.setImageResource(intent.getIntExtra("productImage", 0))
    }
}