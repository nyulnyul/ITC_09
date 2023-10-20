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
//    private val departments = arrayOf("기계공학과", "기계설계공학과", "메카트로닉스공학과", "반도체기계정비학과", "조선기계공학과",
//        "항공기계공학과", "자동차공학과", "전기공학과", "전자공학과", "컴퓨터정보공학과", "컴퓨터시스템공학과", "디지털마케팅공학과",
//        "건설환경공학과", "공간정보빅데이터학과", "화학생명공학과", "재료공학과", "건축학과", "실내건축학과", "산업디자인학과",
//        "패션디자인학과", "항공운항과", "항공경영학과", "관광경영학과", "경영비서학과", "호텔경영학과", "물류시스템학과", "스포츠헬스케어학과")

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
            val dept = binding.regDept.selectedItem.toString()

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
