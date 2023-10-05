package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.RegisterActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: RegisterActivityBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        binding.regBtn.setOnClickListener {
            val email = binding.regEmail.text.toString() + "@itc.ac.kr"
            val password = binding.regPassword.text.toString()
            val confirmPassword = binding.regCheckpassword.text.toString()
            val name = binding.regName.text.toString()
            val dept = binding.regDept.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && name.isNotEmpty() && dept.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                //회원가입 정보를 파이어스토어에 담는 기능
                                Firebase_Auth.saveUserInfo(name,email,dept)
                            }else{
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "빠짐 없이 채워주세요!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
