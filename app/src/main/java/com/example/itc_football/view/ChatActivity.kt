package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ChatActivity : AppCompatActivity() {

    private lateinit var socketHandler: SocketHandler
    private lateinit var binding: ChatActivityBinding
    private lateinit var chatAdapter: ChatAdapter

    private val chatList = mutableListOf<Chat>()

    private var userNickname = ""

    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userNickname = document.data?.get("name").toString()
                        Log.d(TAG, "userName: $userNickname")
                        loadChatMessages()
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }


        // productID를 받아옴
        val productID = intent.getStringExtra("productID")
        userNickname = intent.getStringExtra(USERNAME) ?: ""

        // productID를 이용하여 스토리지에서 이미지를 다운로드
        val storage = Firebase.storage.reference.child("${productID}.png")
        storage.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.imgProduct)
        }

        binding.productName.text = intent.getStringExtra("productName")
        binding.productPrice.text = "${intent.getIntExtra("productPrice", 0)}원"

        if (userNickname.isEmpty()) {
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
                        username = userNickname,
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


                    Log.d("Chatting", "$chat")
                    binding.etMsg.setText("")
                }
            }

            socketHandler.onNewChat.observe(this) {
                val chat = it.copy(isSelf = it.username == userNickname)
                chatList.add(chat)
                chatAdapter.submitChat(chatList)
                Log.d("ChatList", "$chatList")
                binding.rvChat.scrollToPosition(chatList.size - 1)
            }
        }

//        val initialMargin = resources.getDimensionPixelSize(R.dimen.initial_margin)
//
//        val constraintLayout = binding.chatLayout // 여기에 자신의 ConstraintLayout ID를 사용하세요
//        val rvChat = binding.rvChat
//        val sendMessageLayout = binding.sendMessageLayout
//
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(constraintLayout)
//
//        // rvChat의 아래 여백을 initialMargin만큼 설정 (처음에는 이 여백이 적용됨)
//        constraintSet.setMargin(rvChat.id, ConstraintSet.BOTTOM, initialMargin)
//        constraintSet.applyTo(constraintLayout)
//
//        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy < 0 && !rvChat.canScrollVertically(1)) {
//                    // 스크롤을 올릴 때 (메시지 아래로 스크롤)
//                    constraintSet.clone(constraintLayout)
//                    constraintSet.setMargin(rvChat.id, ConstraintSet.BOTTOM, initialMargin)
//                    constraintSet.applyTo(constraintLayout)
//                } else if (rvChat.canScrollVertically(1)) {
//                    // 스크롤이 아래로 진행 중
//                    constraintSet.clone(constraintLayout)
//                    // rvChat의 아래 여백을 0으로 설정 (스크롤 중에는 여백을 없애 줍니다)
//                    constraintSet.setMargin(rvChat.id, ConstraintSet.BOTTOM, 12)
//                    constraintSet.applyTo(constraintLayout)
//                } else {
//                    // 스크롤 위치가 최하단이 아닌 경우
//                    constraintSet.clone(constraintLayout)
//                    constraintSet.setMargin(rvChat.id, ConstraintSet.BOTTOM, initialMargin)
//                    constraintSet.applyTo(constraintLayout)
//                }
//            }
//        })


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
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val fetchedChatList = mutableListOf<Chat>()

                    for (document in result) {
                        val chat = document.toObject(Chat::class.java)
                        fetchedChatList.add(chat)
                    }

                    // 여기서 내 채팅인지 여부를 판별하고 isSelf 값을 설정합니다.
                    fetchedChatList.forEach { chat ->
                        chat.isSelf = chat.username == userNickname
                    }

                    // 기존 채팅 리스트를 업데이트하고 UI를 갱신합니다.
                    chatList.clear()
                    chatList.addAll(fetchedChatList)
                    chatAdapter.submitChat(chatList)
                    binding.rvChat.scrollToPosition(chatList.size - 1)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "문서 가져오기 실패.", exception)
                }
        }
    }


}
