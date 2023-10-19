package com.example.itc_football.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.itc_football.Chat
import com.example.itc_football.databinding.PreviewActivityBinding
import com.google.firebase.auth.FirebaseAuth
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

//        Glide.with(this).load(imageUrl).into(binding.imageUrl)
        binding.productName.text = intent.getStringExtra("productName")
        binding.productDetail.text = intent.getStringExtra("productDetail")
        binding.productPrice.text = intent.getIntExtra("productPrice", 0).toString()
        binding.nowMember.text = intent.getIntExtra("nowMember", 0).toString()
        binding.maxMember.text = intent.getIntExtra("maxMember", 0).toString()
        val productID = intent.getStringExtra("productID")


        binding.getChatBtn.setOnClickListener {

            val intent = Intent(this, ChatActivity::class.java)
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email ?: ""

            intent.putExtra(ChatActivity.USERNAME, email)
            intent.putExtra("productID", productID)
            intent.putExtra("productName", binding.productName.text.toString())
            intent.putExtra("productDetail", binding.productDetail.text.toString())
            intent.putExtra("productPrice",  binding.productPrice.text.toString())
            intent.putExtra("peopleNum", binding.maxMember.text.toString())
            intent.putExtra("nowMember", binding.nowMember.text.toString())

            startActivity(intent)
        }

    }


}
