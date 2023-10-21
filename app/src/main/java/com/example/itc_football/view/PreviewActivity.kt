package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.ContentValues
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: PreviewActivityBinding
//    private lateinit var imageUrl: ImageView
    private lateinit var imageUrl: String


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreviewActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        val productID = intent.getStringExtra("productID")
        val storage = Firebase.storage.reference.child("${productID}.png")
        storage.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.imageUrl)
        }

//        Glide.with(this).load(imageUrl).into(binding.imageUrl)
        binding.productName.text = intent.getStringExtra("productName")
        binding.productDetail.text = intent.getStringExtra("productDetail")
        binding.productPrice.text = "${intent.getIntExtra("productPrice", 0)}원"
        binding.nowMember.text = intent.getIntExtra("nowMember", 0).toString()
        binding.maxMember.text = intent.getIntExtra("maxMember", 0).toString()
        Log.d("productPrice1", "${intent.getIntExtra("productPrice", 0)}")

        binding.getChatBtn.setOnClickListener {

            val intent = Intent(this, ChatActivity::class.java)
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email ?: ""

            intent.putExtra(ChatActivity.USERNAME, email)
            intent.putExtra("productID", productID)
            intent.putExtra("productName", binding.productName.text.toString())
            intent.putExtra("productDetail", binding.productDetail.text.toString())
            intent.putExtra("productPrice",  binding.productPrice.text.toString())
            Log.d("productPrice2", binding.productPrice.text.toString())
            intent.putExtra("peopleNum", binding.maxMember.text.toString())
            intent.putExtra("nowMember", binding.nowMember.text.toString())


            if (user != null) {
                val productCollection = FirebaseFirestore.getInstance().collection("product")
                val memberCollection = productCollection.document(productID!!).collection("member")

                memberCollection.document(user.uid).get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            // 현재 유저의 uid가 아직 'member' 컬렉션에 없으므로 추가합니다.
                            memberCollection.document(user.uid).set(hashMapOf("uid" to user.uid))
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error writing document", e)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(ContentValues.TAG, "get failed with ", exception)
                    }
            }

                startActivity(intent)
        }

    }


}
