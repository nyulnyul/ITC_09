package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.Chat
import com.example.itc_football.databinding.PreviewActivityBinding

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: PreviewActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreviewActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.getChatBtn.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

    }


}