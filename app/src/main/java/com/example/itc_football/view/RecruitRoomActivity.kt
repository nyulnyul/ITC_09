package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.RecrruitRoomActivityBinding

class RecruitRoomActivity : AppCompatActivity() {
    private lateinit var binding: RecrruitRoomActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecrruitRoomActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.recruitOkBtn.setOnClickListener {
            val intent = Intent(this, ItemListActivity::class.java)
            startActivity(intent)
            finish() // 방 생성 후 스택에서 제거 (뒤로가기 눌렀을 때 다시 방생성으로 돌아가는 것 방지)
        }

    }
}