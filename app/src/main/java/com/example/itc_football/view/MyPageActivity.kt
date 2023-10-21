package com.example.itc_football.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.R
import com.example.itc_football.databinding.MyPageActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: MyPageActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyPageActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val dept = document.getString("dept")
                        val email = document.getString("email")


                        binding.myname.text = name

                        binding.mydept.text = dept
                        if (email != null) {
                            binding.mynum.text = email.split("@")[0]
                        }

                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        } ?: run {
            // No user is signed in
        }



        binding.bottomNavigation.selectedItemId = R.id.bottom_mypage
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    val intent = Intent(this, ItemListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.bottom_chat -> {
                    val intent = Intent(this, ChatListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.bottom_mypage -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

    }
}