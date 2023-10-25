package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.itc_football.R
import com.example.itc_football.databinding.PreviewActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.View
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: PreviewActivityBinding

    //    private lateinit var imageUrl: ImageView
    private lateinit var imageUrl: String
    private val firestore = FirebaseFirestore.getInstance()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreviewActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        ArrayAdapter.createFromResource(
            this,
            R.array.roomAble,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.whatable.adapter = adapter
        }
        val productID = intent.getStringExtra("productID")
        val storage = Firebase.storage.reference.child("${productID}.png")
        storage.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.imageUrl)
        }

        val productCollection = firestore.collection("product").document(productID.toString())
        Log.d("productCollection", productCollection.toString())
        productCollection.get().addOnSuccessListener { document ->
            Log.d("document", document.toString())
            if (document != null) {
                val maker = document.data?.get("maker").toString() // 방의 maker값을 가져옴
                binding.whatable.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        p1: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedStatus = parent?.getItemAtPosition(position).toString()

                        // 선택된 상태를 파이어베이스에 업데이트
                        productCollection.update("roomAble", selectedStatus)
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating document", e)
                            }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // 아무 것도 선택되지 않았을 때의 처리
                    }
                }
                val makerParts = maker.split("_") // maker값을 _로 나눔
                binding.nickName.text = makerParts[1] // _로 나눈 maker의 두번째 값이 닉네임을 입력
//                binding.whatable.text = roomable.toString() // roomable값을 가져옴

                // 학과 설정 부분
                val userCollection =
                    firestore.collection("users").whereEqualTo("name", makerParts[1])
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

            var pP =intent.getIntExtra("productPrice", 0)
            binding.productName.text = intent.getStringExtra("productName")
            binding.productDetail.text = intent.getStringExtra("productDetail")
            binding.productPrice.text = "${pP}원"
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
                intent.putExtra("productPrice", pP)
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
}
