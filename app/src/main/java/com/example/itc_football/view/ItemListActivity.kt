package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.itc_football.Product
import com.example.itc_football.ProductAdapter
import com.example.itc_football.R
import com.example.itc_football.databinding.ItemListActivityBinding
import com.google.firebase.firestore.FirebaseFirestore

class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ItemListActivityBinding
    private val firestore = FirebaseFirestore.getInstance()

    var ProductList = arrayListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemListActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        // 리스트뷰에 어댑터 연결
        val Adapter = ProductAdapter(this, ProductList)
        binding.listView.adapter = Adapter

        // firestore에서 데이터 가져오기
        val productsCollection = firestore.collection("product")

        productsCollection.get().addOnSuccessListener { documents ->
            ProductList.clear() // 기존 리스트 초기화
            for (document in documents) {
                val productName = document.getString("productName")
                val productPrice = document.getLong("productPrice")
                val maxMember = document.getLong("maxMember")?.toInt() ?: 0
                val nowMember = document.getLong("nowMember")?.toInt() ?: 0
                val imageUrl = document.getString("imageUrl")

                // null 값이 아닐 경우에만 리스트에 추가
                // imageUrl은 null이어도 상관없나 고민중
                if (productName != null && productPrice != null && imageUrl != null) {
                    val product = Product(productName, productPrice.toInt(), imageUrl, maxMember, nowMember)
                    ProductList.add(product) // 리스트에 추가
                }
                Log.d("Firestore", "Product List: $ProductList")
            }
            Adapter.notifyDataSetChanged()
        }

        // 리스트뷰의 아이템 클릭시 이벤트
        binding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectItem = parent.getItemAtPosition(position) as Product
                intent = Intent(this, PreviewActivity::class.java)
                intent.putExtra("productName", selectItem.productName)
                intent.putExtra("productPrice", selectItem.productPrice)
                intent.putExtra("imageUrl", selectItem.imageUrl)
                intent.putExtra("peopleNum", selectItem.maxMember)
                intent.putExtra("nowMember", selectItem.nowMember)
                startActivity(intent)
            }

        // 방생성 버튼 클릭시 이벤트
        binding.recruitBtn.setOnClickListener() {
            val intent = Intent(this, RecruitRoomActivity::class.java)
            startActivity(intent)
        }
        binding.bottomNavigation.selectedItemId = binding.bottomNavigation.menu.getItem(0).itemId
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