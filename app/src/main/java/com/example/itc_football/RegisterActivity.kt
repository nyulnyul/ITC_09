package com.example.itc_football

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.ActivityMainBinding
import com.example.itc_football.databinding.RegisteractivityBinding
import com.example.itc_football.db.AppDatabase
import com.example.itc_football.db.RegisterDao
import com.example.itc_football.db.RegisterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

    private lateinit var binding: RegisteractivityBinding
    private lateinit var db: AppDatabase
    private lateinit var registerDao: RegisterDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisteractivityBinding.inflate(
            LayoutInflater.from(this)
        )
        setContentView(binding.root)
        db= AppDatabase.getInstance(this)!!
        registerDao = db.getRegisterDao()
        binding.regBtn.setOnClickListener{
            if(validateName() && validateEmail() && validatePassword() && validateCheckPassword() && validatePasswordMatch() && validateDept()){
                val name = binding.regName.text.toString()
                val email = binding.regEmail.text.toString()
                val password = binding.regPassword.text.toString()
                val checkPassword = binding.regCheckpassword.text.toString()
                val department = binding.regDept.text.toString()
                val registerEntity = RegisterEntity(null, name, email, department ,password, checkPassword)

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


    }

    private fun validateName(): Boolean {
        var errorMessage: String? =null
        val value: String = binding.regName.text.toString()
        if(value.isEmpty()){
            errorMessage = "이름을 입력해주세요"
        }
        if(errorMessage!=null){
            binding.regName.apply {
                var isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    private fun validateDept(): Boolean {
        var errorMessage: String? =null
        val value: String = binding.regDept.text.toString()
        if(value.isEmpty()){
            errorMessage = "학과 입력해주세요"
        }
        if(errorMessage!=null){
            binding.regDept.apply {
                var isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    private fun validateEmail(): Boolean {
        var errorMessage: String? =null

        val value: String = binding.regEmail.text.toString()
        if(value.isEmpty()) {
            errorMessage = "학번을 입력해주세요"
        }else if(!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            errorMessage = "이메일 형식이 아닙니다"
        }

        return errorMessage == null
    }private fun validatePassword(): Boolean {
        var error: String? =null
        val value: String = binding.regPassword.text.toString()
        if(value.isEmpty()){
            error = "비밀번호를 입력해주세요"
        }else if(value.length <6){
            error ="비밀번호를 6글자 이상 입력해주세요"
        }
        return error == null
    }
    private fun validateCheckPassword(): Boolean {
        var error: String? =null
        val value: String = binding.regCheckpassword.text.toString()
        if(value.isEmpty()){
            error = "비밀번호 확인란을 입력해주세요"
        }else if(value.length <6){
            error ="비밀번호를 6글자 이상 입력해주세요"
        }
        return error == null
    }
    private fun validatePasswordMatch(): Boolean {
        var error: String? =null
        val password: String = binding.regPassword.text.toString()
        val confirmPassword: String = binding.regCheckpassword.text.toString()
        if(password != confirmPassword){
            error = "비밀번호가 일치하지 않습니다"
        }
        return error == null
    }
    override fun onClick(view: View?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if(view!=null){
            when(view.id){
                R.id.reg_name -> {
                    if(!hasFocus){
                        validateName()
                    }
                }
                R.id.reg_email -> {
                    if(!hasFocus){
                        validateEmail()
                    }
                }
                R.id.reg_password -> {
                    if(!hasFocus){
                        validatePassword()
                    }
                }
                R.id.reg_checkpassword -> {
                    if(!hasFocus){
                        validateCheckPassword()
                    }
                }
                R.id.reg_dept -> {
                    if(!hasFocus){
                        validateDept()
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        TODO("Not yet implemented")
    }
}