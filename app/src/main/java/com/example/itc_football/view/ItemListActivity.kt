package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.Product
import com.example.itc_football.ProductAdapter
import com.example.itc_football.R
import com.example.itc_football.databinding.ItemListActivityBinding
import com.google.firebase.firestore.FirebaseFirestore

class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ItemListActivityBinding

    var ProductList = arrayListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemListActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        // 리스트뷰에 어댑터 연결
        val Adapter = ProductAdapter(this, ProductList)
        binding.listView.adapter = Adapter

        val firestore = FirebaseFirestore.getInstance()
        val productsCollection = firestore.collection("product")

        productsCollection.get().addOnSuccessListener { documents ->
            ProductList.clear() // 기존 리스트 초기화
            for (document in documents) {
                val productName = document.getString("productName")
                val productPrice = document.getLong("productPrice")
//                val productImage = document.getString("productImage")
                val maxMember = document.getLong("maxMember")?.toInt() ?: 0
                val nowMember = document.getLong("nowMember")?.toInt() ?: 0

                if (productName != null && productPrice != null) {
                    val product = Product(productName, productPrice.toInt(), maxMember, nowMember)
                    ProductList.add(product)
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
//                intent.putExtra("productImage", selectItem.productImage)
                intent.putExtra("peopleNum", selectItem.maxMember)
                intent.putExtra("nowMember", selectItem.nowMember)
                startActivity(intent)
            }

        // 방생성 버튼 클릭시 이벤트
        binding.recruitBtn.setOnClickListener() {
            val intent = Intent(this, RecruitRoomActivity::class.java)
            startActivity(intent)
        }

    }


}