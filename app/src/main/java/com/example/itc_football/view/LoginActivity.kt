package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.Chat
import com.example.itc_football.databinding.LoginActivityBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString() + "@itc.ac.kr"
            val password = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, ItemListActivity::class.java)
                            intent.putExtra(ChatActivity.USERNAME, email)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "정보가 올바르지 않아요!", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "빠짐 없이 채워주세요!!", Toast.LENGTH_SHORT).show()

            }
        }
        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.loginEmail.requestFocus()
    }


}