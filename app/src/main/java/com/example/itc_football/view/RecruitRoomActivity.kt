package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.RecrruitRoomActivityBinding
import com.google.firebase.firestore.FirebaseFirestore

class RecruitRoomActivity : AppCompatActivity() {
    private lateinit var binding: RecrruitRoomActivityBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecrruitRoomActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.recruitOkBtn.setOnClickListener {
            val intent = Intent(this, ItemListActivity::class.java)
            addProduct()
            startActivity(intent)
            finish()
        }

    }

    private fun addProduct() {
        val productCollection = firestore.collection("product")

        // 사용자가 입력한 정보를 가져와서 Firebase Firestore에 추가합니다.
        val productName = binding.productName.text.toString()
        val productPrice = binding.productPrice.text.toString().toLong()
        val maxMember = binding.maxMember.selectedItem.toString().toInt()
        val nowMember = 1 // 기본으로 1로 설정

        val productData = hashMapOf(
            "productName" to productName,
            "productPrice" to productPrice,
            "maxMember" to maxMember,
            "nowMember" to nowMember
        )

        productCollection.add(productData)
            .addOnSuccessListener { documentReference ->
                // 성공적으로 추가된 경우
                val productId = documentReference.id
                // 추가 작업을 수행하거나 다른 화면으로 이동할 수 있습니다.
            }
            .addOnFailureListener { e ->
                // 추가 실패 시 처리
            }
    }
}