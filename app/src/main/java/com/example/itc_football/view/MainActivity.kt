package com.example.itc_football.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itc_football.Chat
import com.example.itc_football.SocketHandler
import com.example.itc_football.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var socketHandler: SocketHandler
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendHello.setOnClickListener {
            val intent = Intent(this, PreviewActivity::class.java)
            startActivity(intent)
        }


    }



}