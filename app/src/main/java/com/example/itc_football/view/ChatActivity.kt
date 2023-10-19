package com.example.itc_football.view

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.itc_football.Chat
import com.example.itc_football.ChatAdapter
import com.example.itc_football.SocketHandler
import com.example.itc_football.databinding.ChatActivityBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ChatActivity : AppCompatActivity() {

    private lateinit var socketHandler: SocketHandler
    private lateinit var binding: ChatActivityBinding
    private lateinit var chatAdapter: ChatAdapter

    private val chatList = mutableListOf<Chat>()

    private var userName = ""

//    val productName = intent.getStringExtra("productName")
//    val productPrice = intent.getStringExtra("productPrice")

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra(USERNAME) ?: ""

        binding.productName.text = intent.getStringExtra("productName")
        binding.productPrice.text = intent.getIntExtra("productPrice", 0).toString()

        loadChatMessages()

//        binding.productName.text = productName
//        binding.productPrice.text = productPrice

        if (userName.isEmpty()) {
            finish()
        } else {
            socketHandler = SocketHandler()

            chatAdapter = ChatAdapter()

            binding.rvChat.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = chatAdapter
            }

            binding.sendButton.setOnClickListener {//채팅을 보내기
                val message = binding.etMsg.text.toString()
                if (message.isNotEmpty()) {
                    val chat = Chat(
                        username = userName,
                        text = message,
                        timestamp = Timestamp.now()
                    )
                    socketHandler.emitChat(chat)
// Save the chat to Firestore
                    if (productID != null) {
                        db.collection("product").document(productID).collection("msg")
                            .add(chat)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }


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

    companion object {
        const val USERNAME = "username"
    }

    private fun loadChatMessages() {
        val productID = intent.getStringExtra("productID")
        if (productID != null) {
            db.collection("product").document(productID).collection("msg")
                .orderBy("timestamp", Query.Direction.ASCENDING)  // Assuming that 'timestamp' field exists in your Chat data class.
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        var chat = document.toObject(Chat::class.java)
                        if (chat.username == userName) {
                            chat = chat.copy(isSelf = true)
                        }
                        chatList.add(chat)
                    }
                    chatAdapter.submitChat(chatList)
                    binding.rvChat.scrollToPosition(chatList.size - 1)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }


}
