package com.example.itc_football.view

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.R
import com.example.itc_football.databinding.RegisteractivityBinding
import com.example.itc_football.db.AppDatabase
import com.example.itc_football.db.RegisterDao
import com.example.itc_football.db.RegisterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisteractivityBinding
    private lateinit var db: AppDatabase
    private lateinit var registerDao: RegisterDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisteractivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        db = AppDatabase.getInstance(this)
        registerDao = db.getRegisterDao()

        binding.regBtn.setOnClickListener {
            if (validateInputs()) {
                val name = binding.regName.text.toString()
                val email = binding.regEmail.text.toString()
                val password = binding.regPassword.text.toString()
                val checkPassword = binding.regCheckpassword.text.toString()
                val department = binding.regDept.text.toString()
                val registerEntity = RegisterEntity(null, name, email, department, password, checkPassword)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        registerDao.insertUser(registerEntity)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "회원가입이 성공했습니다!", Toast.LENGTH_SHORT).show()
                            Log.d("RegisterActivity", "회원가입 성공")
                            finish()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "회원가입이 실패했습니다!", Toast.LENGTH_SHORT).show()
                            Log.e("RegisterActivity", "회원 가입 실패", e)
                        }
                    }
                }
            }
        }

        // EditText에 포커스가 변경되었을 때 각각의 입력 유효성 검사를 수행
        binding.regName.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateName() }
        binding.regEmail.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateEmail() }
        binding.regPassword.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validatePassword() }
        binding.regCheckpassword.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateCheckPassword() }
        binding.regDept.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) validateDept() }
    }

    // 모든 입력값의 유효성을 검사하는 함수
    private fun validateInputs(): Boolean {
        return validateName() && validateEmail() && validatePassword() && validateCheckPassword() && validatePasswordMatch() && validateDept()
    }

    // 유효성 검사 함수들은 유사하므로 하나의 함수로 통합하여 재사용
    private fun validateField(value: String, fieldName: String): Boolean {
        return if (value.isEmpty()) {
            setError(fieldName, "$fieldName 을(를) 입력해주세요")
            false
        } else {
            clearError(fieldName)
            true
        }
    }

    // 에러 메시지 설정
    private fun setError(fieldName: String, errorMessage: String) {
        when (fieldName) {
            "이름" -> binding.regName.apply { error = errorMessage }
            "이메일" -> binding.regEmail.apply { error = errorMessage }
            "비밀번호" -> binding.regPassword.apply { error = errorMessage }
            "비밀번호 확인" -> binding.regCheckpassword.apply { error = errorMessage }
            "학과" -> binding.regDept.apply { error = errorMessage }
        }
    }

    // 에러 메시지 제거
    private fun clearError(fieldName: String) {
        when (fieldName) {
            "이름" -> binding.regName.apply { error = null }
            "이메일" -> binding.regEmail.apply { error = null }
            "비밀번호" -> binding.regPassword.apply { error = null }
            "비밀번호 확인" -> binding.regCheckpassword.apply { error = null }
            "학과" -> binding.regDept.apply { error = null }
        }
    }

    private fun validateName() = validateField(binding.regName.text.toString(), "이름")
    private fun validateDept() = validateField(binding.regDept.text.toString(), "학과")
    private fun validateEmail() =
        if (Patterns.EMAIL_ADDRESS.matcher(binding.regEmail.text.toString()).matches()) {
            clearError("이메일")
            true
        } else {
            setError("이메일", "이메일 형식이 아닙니다")
            false
        }

    private fun validatePassword() =
        validateField(binding.regPassword.text.toString(), "비밀번호")

    private fun validateCheckPassword() =
        validateField(binding.regCheckpassword.text.toString(), "비밀번호 확인")

    private fun validatePasswordMatch(): Boolean {
        return if (binding.regPassword.text.toString() == binding.regCheckpassword.text.toString()) {
            clearError("비밀번호")
            clearError("비밀번호 확인")
            true
        } else {
            setError("비밀번호", "비밀번호가 일치하지 않습니다")
            setError("비밀번호 확인", "비밀번호가 일치하지 않습니다")
            false
        }
    }
}
