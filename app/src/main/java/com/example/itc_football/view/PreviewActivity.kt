package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.Chat
import com.example.itc_football.databinding.PreviewactivityBinding

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: PreviewactivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreviewactivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.getChatBtn.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

    }


}