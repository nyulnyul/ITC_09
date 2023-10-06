package com.example.itc_football.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.itc_football.Chat
import com.example.itc_football.databinding.PreviewActivityBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: PreviewActivityBinding
//    private lateinit var imageUrl: ImageView
    private lateinit var imageUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreviewActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

//        val productId = intent.getStringExtra("productId")

// Firebase Firestore에서 도큐먼트를 가져옵니다.
//        val firestore = FirebaseFirestore.getInstance()
//        val productsCollection = firestore.collection("product")
//
//        productsCollection.document(productId!!).get().addOnSuccessListener { documentSnapshot ->
//            if (documentSnapshot.exists()) {
//                // 도큐먼트가 존재하는 경우에만 데이터를 처리합니다.
//                val imageUrl = documentSnapshot.getString("imageUrl")
//
//                if (imageUrl != null) {
//                    // imageUrl을 Glide를 사용하여 ImageView에 표시합니다.
//                    Glide.with(this)
//                        .load(imageUrl)
//                        .into(binding.imageUrl)
//                }


        imageUrl = intent.getStringExtra("imageUrl").toString()
        Glide.with(this).load(imageUrl).into(binding.imageUrl)
        binding.productName.text = intent.getStringExtra("productName")
        binding.productDetail.text = intent.getStringExtra("productDetail")
        binding.productPrice.text = intent.getIntExtra("productPrice", 0).toString()
        binding.nowMember.text = intent.getIntExtra("nowMember", 0).toString()
        binding.maxMember.text = intent.getIntExtra("maxMember", 0).toString()


        binding.getChatBtn.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

    }


}
