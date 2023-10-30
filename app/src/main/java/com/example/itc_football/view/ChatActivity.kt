package com.example.itc_football.view

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itc_football.R
import com.example.itc_football.data.Chat
import com.example.itc_football.viewmodel.ChatAdapter
import com.example.itc_football.viewmodel.SocketHandler
import com.example.itc_football.databinding.ChatActivityBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatActivity : AppCompatActivity() {

    private lateinit var socketHandler: SocketHandler
    private lateinit var binding: ChatActivityBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var progressBar: ProgressBar

    private val chatList = mutableListOf<Chat>()
    private var userName = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        if (user != null) {
            fetchUserName(user)
        }

        // Load chat messages
        progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE
        loadChatMessages()

        // Initialize chat functionality
        initChat()

        // Load product details
        loadProductDetails()

        // Handle UI scrolling
        handleScrollBehavior()
    }

    override fun onDestroy() {
        socketHandler.disconnectSocket()
        super.onDestroy()
    }

    companion object {
        const val USERNAME = "username"
    }


        // productID를 받아옴

        val productID = intent.getStringExtra("productID")
        userName = intent.getStringExtra(USERNAME) ?: ""
        val storage = Firebase.storage.reference.child("${productID}.png")
        storage.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.imgProduct)
        }
        binding.productName.text = intent.getStringExtra("productName")
        binding.productPrice.text = "${intent.getIntExtra("productPrice", 0)}원"
    }

        progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE
        loadChatMessages()

        if (userName.isEmpty()) {
            finish()
        } else {
            socketHandler = SocketHandler()

            chatAdapter = ChatAdapter(userName)


        binding.sendButton.setOnClickListener {
            val message = binding.etMsg.text.toString()
            if (message.isNotEmpty()) {
                val chat = Chat(
                    username = userName,
                    text = message,
                    timestamp = Timestamp.now()
                )
                socketHandler.emitChat(chat)
                saveChatToFirestore(chat)
                binding.etMsg.setText("")
            }
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
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }


    private fun saveChatToFirestore(chat: Chat) {
        val productID = intent.getStringExtra("productID")
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
        binding.etMsg.setText("")
    }

    private fun handleScrollBehavior() {
        val initialMargin = resources.getDimensionPixelSize(R.dimen.initial_margin)
        val constraintLayout = binding.chatLayout
        val rvChat = binding.rvChat
        val sendMessageLayout = binding.sendMessageLayout

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !rvChat.canScrollVertically(1)) {
                    constraintSet.clone(constraintLayout)
                    constraintSet.setMargin(rvChat.id, ConstraintSet.BOTTOM, initialMargin)
                    constraintSet.applyTo(constraintLayout)
                } else if (rvChat.canScrollVertically(1)) {
                    constraintSet.clone(constraintLayout)
                    constraintSet.setMargin(rvChat.id, ConstraintSet.BOTTOM, 12)
                    constraintSet.applyTo(constraintLayout)
                } else {
                    constraintSet.clone(constraintLayout)
                    constraintSet.setMargin(rvChat.id, ConstraintSet.BOTTOM, initialMargin)
                    constraintSet.applyTo(constraintLayout)
                }
            }
        })


    }

    override fun onDestroy() {
        socketHandler.disconnectSocket()
        super.onDestroy()
    }

    companion object {
        const val USERNAME = "username"

    }

    // Firestore에서 데이터를 로드한 후, 자신과 상대방의 메시지를 구별하여 정렬
    private fun loadChatMessages() {
        val productID = intent.getStringExtra("productID")
        if (productID != null) {
            db.collection("product").document(productID).collection("msg")
                .orderBy(
                    "timestamp",
                    Query.Direction.ASCENDING
                )  // Assuming that 'timestamp' field exists in your Chat data class.

                .get()
                .addOnSuccessListener { result ->
                    val currentUserUID = firebaseAuth.currentUser?.uid // 현재 사용자의 UID
                    chatList.clear()

                    for (document in result) {
                        val chat = document.toObject(Chat::class.java)
                        val isSelf =
                            chat.username == userName || document.getString("userUID") == currentUserUID

                        // isSelf 값이 true면 자신의 메시지, false면 상대방의 메시지로 분류
                        chatList.add(chat.copy(isSelf = isSelf))
                    }

                    chatAdapter.submitChat(chatList)
                    binding.rvChat.scrollToPosition(chatList.size - 1)
                    progressBar.visibility = View.GONE
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                    progressBar.visibility = View.GONE
                }
        }
    }

}
