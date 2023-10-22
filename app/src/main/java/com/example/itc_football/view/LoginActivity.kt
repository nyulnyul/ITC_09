package com.example.itc_football.view

import Firebase_Auth.db
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.LoginActivityBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var userName = ""

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userName = document.data?.get("name").toString()
                    } else {
                        Log.d(ContentValues.TAG, "로그인 때 유저 닉네임 가져오기 실패")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "로그인 때 users DB가져오기 실패", exception)
                }
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString() + "@itc.ac.kr"
            val password = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra(ChatActivity.USERNAME, userName)
                            startActivity(intent)
                            finish()
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