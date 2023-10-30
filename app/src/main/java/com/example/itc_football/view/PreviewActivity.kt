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
import android.view.View
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: PreviewActivityBinding

    //    private lateinit var imageUrl: ImageView
    private lateinit var imageUrl: String
    private val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser


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
                // 파이어베이스에서 "roomAble" 필드를 가져옴

                val roomAble = document.data?.get("roomAble").toString()

                // 스피너의 아이템 목록에서 가져온 "roomAble" 값을 찾아서 선택
                val roomAbleIndex = resources.getStringArray(R.array.roomAble).indexOf(roomAble)
                if (roomAbleIndex != -1) {
                    // 찾은 "roomAble" 값이 아이템 목록에 있을 경우, 그 값을 초기 선택값으로 설정
                    binding.whatable.setSelection(roomAbleIndex)
                }
                binding.whatable.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
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
                                    Log.d(
                                        ContentValues.TAG,
                                        "DocumentSnapshot successfully updated!"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error updating document", e)
                                }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // 아무 것도 선택되지 않았을 때의 처리
                        }
                    }
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

// "maker" 값에서 UID 부분만 추출
                val makerUid = maker.split("_")[0]

// "maker"의 UID와 현재 유저의 UID 비교
                if (currentUserId == makerUid) {
                    // 동일 인물일 경우, 스피너 보이기
                    binding.whatable.visibility = View.VISIBLE
                    binding.updateBtn.visibility = View.VISIBLE
                    binding.deleteBtn.visibility = View.VISIBLE
                } else {
                    // 동일 인물이 아닐 경우, 스피너 숨기기
                    binding.whatable.visibility = View.GONE
                    binding.updateBtn.visibility = View.GONE
                    binding.deleteBtn.visibility = View.GONE
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

            val pP = intent.getIntExtra("productPrice", 0)
            binding.productName.text = intent.getStringExtra("productName")
            binding.productDetail.text = intent.getStringExtra("productDetail")
            binding.productPrice.text = "${pP}원"
            binding.nowMember.text = intent.getIntExtra("nowMember", 0).toString()
            binding.maxMember.text = intent.getIntExtra("maxMember", 0).toString()
            Log.d("productPrice1", "${intent.getIntExtra("productPrice", 0)}")

            // 채팅하기 버튼 클릭 메서드
            binding.getChatBtn.setOnClickListener {

                val intent = Intent(this, ChatActivity::class.java)
                val email = user?.email ?: ""

                intent.putExtra(ChatActivity.USERNAME, email)
                intent.putExtra("productID", productID)
                intent.putExtra("productName", binding.productName.text.toString())
                intent.putExtra("productDetail", binding.productDetail.text.toString())
                intent.putExtra("productPrice", pP)
                Log.d("productPrice2", binding.productPrice.text.toString())
                intent.putExtra("peopleNum", binding.maxMember.text.toString())
                intent.putExtra("nowMember", binding.nowMember.text.toString())

                // 멤버추가 메서드
                addToMember(productID.toString(), user)
                binding.nowMember.text = (binding.nowMember.text.toString().toInt() + 1).toString()

                startActivity(intent)
            }
        }

    }

    // 멤버추가 메서드
    private fun addToMember(productID: String, user: FirebaseUser?) {
        val productCollection = FirebaseFirestore.getInstance().collection("product")
        val memberCollection =
            productCollection.document(productID).collection("member")

        user?.uid?.let {
            memberCollection.document(it).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        // 현재 유저의 uid가 아직 'member' 컬렉션에 없으므로 추가합니다.
                        memberCollection.document(user.uid)
                            .set(hashMapOf("uid" to user.uid))
                            .addOnSuccessListener {
                                Log.d("addToMember", "멤버 추가 성공")
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "멤버 추가 실패...", e)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }
        }
        // nowMember값 증가
        productCollection.document(productID).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nowMember = document.data?.get("nowMember").toString().toInt()
                    val maxMember = document.data?.get("maxMember").toString().toInt()
                    if (nowMember < maxMember) {
                        productCollection.document(productID).update("nowMember", nowMember + 1)
                            .addOnSuccessListener {
                                Log.d(
                                    ContentValues.TAG,
                                    "DocumentSnapshot successfully updated!"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating document", e)
                            }
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }
}