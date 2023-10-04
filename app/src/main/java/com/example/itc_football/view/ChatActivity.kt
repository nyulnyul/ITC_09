package com.example.itc_football.view

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.ChatactivityBinding


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ChatactivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatactivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

    }
}