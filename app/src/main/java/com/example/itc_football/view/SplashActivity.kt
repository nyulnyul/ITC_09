package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.R
import com.example.itc_football.databinding.ItemActivityBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // 스플래시 화면의 XML 레이아웃을 설정

        // 스플래시 화면을 일정 시간 동안 보여주고 다음 액티비티로 이동
        val delayMillis = 3000L // 2초 동안 스플래시 화면을 보여줌
        Handler().postDelayed({
            // 스플래시 화면 이후에 이동할 액티비티를 지정
            val intent = Intent(this, LoginActivity::class.java) // LoginActivity 대신 시작하고자 하는 액티비티로 변경
            startActivity(intent)
            finish() // 스플래시 액티비티를 종료
        }, delayMillis)
    }
}