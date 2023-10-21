package com.example.itc_football.view

import android.annotation.SuppressLint
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
//    private lateinit var imageUrl: String
    private val firestore = FirebaseFirestore.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreviewActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        // productID값을 받아옴
        val productID = intent.getStringExtra("productID")

        // 이미지 설정부분
        val storage = Firebase.storage.reference.child("${productID}.png")
        storage.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.imageUrl)
        }
        // 닉네임 설정 부분
        val productCollection = firestore.collection("product").document(productID.toString())
        Log.d("productCollection", productCollection.toString())
        productCollection.get().addOnSuccessListener { document ->
            Log.d("document", document.toString())
            if (document != null) {
                val maker = document.data?.get("maker").toString() // 방의 maker값을 가져옴
                val makerParts = maker.split("_") // maker값을 _로 나눔
                binding.nickName.text = makerParts[1] // _로 나눈 maker의 두번째 값이 닉네임을 입력

                // 학과 설정 부분
                val userCollection = firestore.collection("users").whereEqualTo("name", makerParts[1])
                userCollection.get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // 적절한 사용자 문서가 있는 경우 이곳에 도달합니다.
                        val userDocument = querySnapshot.documents[0] // 여러 사용자 중 첫 번째 문서를 가져옵니다
                        val userDept = userDocument.data?.get("dept").toString()
                        binding.department.text = userDept
                    } else {
                        Log.d("userDept", "찾지못함")
                    }
                }
            }

            binding.productName.text = intent.getStringExtra("productName")
            binding.productDetail.text = intent.getStringExtra("productDetail")
            binding.productPrice.text = "${intent.getIntExtra("productPrice", 0)}원"
            binding.nowMember.text = intent.getIntExtra("nowMember", 0).toString()
            binding.maxMember.text = intent.getIntExtra("maxMember", 0).toString()
            Log.d("productPrice1", "${intent.getIntExtra("productPrice", 0)}")

            // 채팅하기 버튼 클릭 시
            binding.getChatBtn.setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java)
//                val user = FirebaseAuth.getInstance().currentUser
//                val email = user?.email ?: ""

//                intent.putExtra(ChatActivity.USERNAME, email)
                intent.putExtra("productID", productID)
                intent.putExtra("productName", binding.productName.text.toString())
                intent.putExtra("productDetail", binding.productDetail.text.toString())
                intent.putExtra("productPrice", binding.productPrice.text.toString())
                Log.d("productPrice2", binding.productPrice.text.toString())
                intent.putExtra("peopleNum", binding.maxMember.text.toString())
                intent.putExtra("nowMember", binding.nowMember.text.toString())

                startActivity(intent)
            }

        }

    }
}
