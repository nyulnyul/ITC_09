package com.example.itc_football.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itc_football.databinding.ChatActivityBinding
import com.example.itc_football.model.Chat
import com.example.itc_football.view_model.ChatAdapter
import com.example.itc_football.view_model.SocketHandler


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ChatActivityBinding
    private lateinit var socketHandler: SocketHandler
    private lateinit var chatAdapter: ChatAdapter

    private val chatList = mutableListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatActivityBinding.inflate(layoutInflater)


        val view = binding.root
        setContentView(view)
        socketHandler = SocketHandler()
        chatAdapter = ChatAdapter()
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }

        binding.btnSend.setOnClickListener {
            val message = binding.etMsg.text.toString()
            if(message.isNotEmpty()){
                val chat = Chat(
                    username = "username", text = message, isSelf = true
                )
                socketHandler.emitChat(chat)
                binding.etMsg.setText("")

            }

        }
        socketHandler.onNewChat.observe(this, {
            chatList.add(it)
            chatAdapter.submitData(chatList)
            binding.rvChat.scrollToPosition(chatList.size - 1)
            Log.d("TAG", "onCreate: $it")
        })
    }


    private object CHET_KEYS {
        const val NEW_MESSAGE = "newMessage"
    }

    companion object {
        private const val SOCKET_URL = "http://10.0.2.2/3001"
    }

    override fun onDestroy() {

        socketHandler.disconnectSocket()
        super.onDestroy()
    }
}