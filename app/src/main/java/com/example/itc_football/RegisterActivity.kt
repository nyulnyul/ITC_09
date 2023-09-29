package com.example.itc_football

import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.ActivityMainBinding
import com.example.itc_football.databinding.RegisteractivityBinding

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {

    private lateinit var binding: RegisteractivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisteractivityBinding.inflate(
            LayoutInflater.from(this)
        )
        setContentView(binding.root)

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
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        TODO("Not yet implemented")
    }
}