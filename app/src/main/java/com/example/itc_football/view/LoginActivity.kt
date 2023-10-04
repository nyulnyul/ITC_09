package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.LoginActivityBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding

    lateinit var username : EditText
    lateinit var password : EditText
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, ItemListActivity::class.java)
            startActivity(intent)
        }
        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


}