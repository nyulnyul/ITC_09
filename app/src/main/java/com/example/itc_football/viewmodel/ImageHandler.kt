package com.example.itc_football.viewmodel

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date

private fun imageUpload(Uri: Uri) {
    val storage = Firebase.storage
    val storageRef = storage.reference.child("images")
    val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
    val mountainRef = storageRef.child("${fileName}.png")

    val uploadTask = mountainRef.putFile(Uri)
    uploadTask.addOnSuccessListener { taskSnapshot ->
//        Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener {
//        Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()

    }
}

private fun imageDownload() {
    // storage 인스턴스 생성
    val storage = Firebase.storage
    // storage 참조
    val storageRef = storage.getReference("images")
    // storage에서 가져올 파일명 선언
    val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
    val mountainsRef = storageRef.child("${fileName}.png")

    val downloadTask = mountainsRef.downloadUrl
    downloadTask.addOnSuccessListener { uri ->
        // 파일 다운로드 성공
        // Glide를 사용하여 이미지를 ImageView에 직접 가져오기
//        Glide.with(RecruitRoomActivity).load(uri).into(binding.imageArea)
    }.addOnFailureListener {
        // 파일 다운로드 실패
    }
}