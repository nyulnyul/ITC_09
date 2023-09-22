package com.example.itc_football

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.itc_football.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var socketHandler: SocketHandler
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        socketHandler = SocketHandler()
        binding.sendHello.setOnClickListener {
            val chat = Chat("nyulnyul", "kkkkkkkk")
            socketHandler.emitChat(chat)
        }
        socketHandler.onNewChat.observe(this) {

        }

    }


    override fun onDestroy() {

        socketHandler.disconnectSocket()
        super.onDestroy()
    }


}