package com.example.itc_football.view

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itc_football.Chat
import com.example.itc_football.ChatAdapter
import com.example.itc_football.SocketHandler
import com.example.itc_football.databinding.ChatActivityBinding


class ChatActivity : AppCompatActivity() {

    private lateinit var socketHandler: SocketHandler
    private lateinit var binding: ChatActivityBinding
    private lateinit var chatAdapter: ChatAdapter

    private val chatList = mutableListOf<Chat>()

    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra(USERNAME) ?: ""

        if (userName.isEmpty()) {
            finish()
        } else {
            socketHandler = SocketHandler()

            chatAdapter = ChatAdapter()

            binding.rvChat.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = chatAdapter
            }

            binding.sendButton.setOnClickListener {
                val message = binding.etMsg.text.toString()
                if (message.isNotEmpty()) {
                    val chat = Chat(
                        username = userName,
                        text = message
                    )
                    socketHandler.emitChat(chat)
                    Log.d("Chatting", "$chat")
                    binding.etMsg.setText("")
                }
            }

            socketHandler.onNewChat.observe(this) {
                val chat = it.copy(isSelf = it.username == userName)
                chatList.add(chat)
                chatAdapter.submitChat(chatList)
                Log.d("ChatList", "$chatList")
                binding.rvChat.scrollToPosition(chatList.size - 1)
            }
        }


    }


    override fun onDestroy() {
        socketHandler.disconnectSocket()
        super.onDestroy()
    }

    companion object{
        const val USERNAME = "username"
    }

}