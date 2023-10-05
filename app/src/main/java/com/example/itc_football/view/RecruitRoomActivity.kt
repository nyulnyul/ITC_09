package com.example.itc_football.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.databinding.RecrruitRoomActivityBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date

class RecruitRoomActivity : AppCompatActivity() {
    private lateinit var binding: RecrruitRoomActivityBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var uri: Uri
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecrruitRoomActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
            val intent = Intent(this, ItemListActivity::class.java)
            addProduct(uri.toString())
            startActivity(intent)
            finish()
        }
    }

    private fun imageUpload(uri: Uri) {
        // storage 인스턴스 생성
        val storage = Firebase.storage
        // storage 참조
        val storageRef = storage.getReference("images")
        // storage에 저장할 파일명 선언
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mountainsRef = storageRef.child("${fileName}.png")

        val uploadTask = mountainsRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // 파일 업로드 성공
            val imageUrl = uri.toString()
            addProduct(imageUrl)
            Toast.makeText(this, "사진 업로드 성공", Toast.LENGTH_SHORT).show();
        }.addOnFailureListener {
            // 파일 업로드 실패
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // 방 생성 메서드
    private fun addProduct(imageUrl: String) {
        val productCollection = firestore.collection("product")

        // 사용자가 입력한 정보를 가져와서 Firebase Firestore에 추가합니다.
        val productName = binding.productName.text.toString()
        val productDetail = binding.productDetail.text.toString()
        val productPrice = binding.productPrice.text.toString().toLong()
        val maxMember = binding.maxMember.selectedItem.toString().toInt()
        val nowMember = 1 // 기본으로 1로 설정
        val imageUrl = uri.toString()

        val productData = hashMapOf(
            "productName" to productName,
            "productDetail" to productDetail,
            "productPrice" to productPrice,
            "maxMember" to maxMember,
            "nowMember" to nowMember,
            "imageUrl" to imageUrl
        )

        productCollection.add(productData)
            .addOnSuccessListener { documentReference ->
                // 성공적으로 추가된 경우
                val productId = documentReference.id
                // 추가 작업을 수행하거나 다른 화면으로 이동할 수 있습니다.
//                documentReference.update("productId", productId)
//                    .addOnSuccessListener {
//                        // 도큐먼트 ID가 업데이트된 경우
//                    }
//                    .addOnFailureListener { e ->
//                        // 업데이트 실패 시 처리
//                    }

            }
            .addOnFailureListener { e ->
                // 추가 실패 시 처리
            }
    }
}
