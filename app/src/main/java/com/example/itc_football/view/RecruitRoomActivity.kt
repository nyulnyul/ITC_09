package com.example.itc_football.view

import Firebase_Auth.db
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.RecrruitRoomActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class RecruitRoomActivity : AppCompatActivity() {
    private lateinit var binding: RecrruitRoomActivityBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var uri: Uri
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth
    private var userName = ""
    private var roomMaker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecrruitRoomActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: ""
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userName = document.data?.get("name").toString()
                        roomMaker = "${uid}_${userName}"
                        Log.d("maker : ", roomMaker)
                    } else {
                        Log.d(ContentValues.TAG, "로그인 때 유저 닉네임 가져오기 실패")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "로그인 때 users DB가져오기 실패", exception)
                }
        }

        firebaseAuth = FirebaseAuth.getInstance()

        // 이미지 업로드 버튼 클릭시 결과 (파일 업로드)
        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        uri = result.data?.data!!
//                        imageUpload(uri)
                        binding.imgProduct.setImageURI(uri)
                    }
                }
            }
        // 이미지 업로드 버튼 클릭시 이벤트
        binding.imgUploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            activityLauncher.launch(intent)
        }

        // 방 생성 버튼 클릭시 이벤트
        binding.recruitOkBtn.setOnClickListener {
            addProductWithDelay()
        }
    }

    // 리스트에 업데이트 하는데에 시간이 걸림. 딜레이를 줘서 업데이트가 되도록 함
    private fun addProductWithDelay() {
        addProduct() // 방 생성 메서드 호출
        // 딜레이를 주고 다음 액티비티로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 2000 밀리초 (2초) 딜레이
    }

    private fun imageUpload(productID: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference.child("$productID.png")

        binding.imgProduct.isDrawingCacheEnabled = true
        binding.imgProduct.buildDrawingCache()
        val bitmap = (binding.imgProduct.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
        }.addOnSuccessListener { taskSnapshot -> }
    }

    //     방 생성 메서드
    private fun addProduct() {
        val productCollection = firestore.collection("product")
        val user = firebaseAuth.currentUser


        // 사용자가 입력한 정보를 가져와서 Firebase Firestore에 추가합니다.
        val productName = binding.productName.text.toString()
        val productDetail = binding.productDetail.text.toString()
        val productPriceText = binding.productPrice.text.toString()
        val productPrice = try {
            productPriceText.toLong()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val maxMember = binding.maxMember.selectedItem.toString().toInt()
        val nowMember = 1 // 기본으로 1로 설정
        val maker = roomMaker
        val roomAble = "공구중"
        Log.d("roomMaker2 : ", roomMaker)

        val productData = hashMapOf(
            "productName" to productName,
            "productDetail" to productDetail,
            "productPrice" to productPrice,
            "maxMember" to maxMember,
            "nowMember" to nowMember,
            "maker" to maker,
            "roomAble" to roomAble
        )

        productCollection.add(productData)
            .addOnSuccessListener { documentReference ->
                // 성공적으로 추가된 경우
                val productID = documentReference.id
                // 추가 작업을 수행하거나 다른 화면으로 이동할 수 있습니다.
                documentReference.update("productID", productID)
                    .addOnSuccessListener {
                        // 업데이트 성공 시 처리
                        imageUpload(productID)
                    }
                    .addOnFailureListener { e ->
                        // 업데이트 실패 시 처리
                    }
            }
            .addOnFailureListener { e ->
                // 추가

            }
    }
}
