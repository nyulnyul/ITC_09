package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.itc_football.model.Product
import com.example.itc_football.view_model.ProductAdapter
import com.example.itc_football.R
import com.example.itc_football.databinding.ItemListActivityBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ItemListActivityBinding
    private lateinit var newRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var mysrl: SwipeRefreshLayout? = null

    private var newProductList = arrayListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemListActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        // 새로고침
        mysrl = binding.swipeLayout
        mysrl!!.setOnRefreshListener(OnRefreshListener {
            newProductList.clear()
            getProductData()
            mysrl!!.isRefreshing = false
        })

        // 어댑터를 생성하고 리사이클러뷰에 연결
        val adapter = ProductAdapter(newProductList)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@ItemListActivity, PreviewActivity::class.java)
                intent.putExtra("productName", newProductList[position].productName)
                intent.putExtra("productDetail", newProductList[position].productDetail)
                intent.putExtra("productPrice", newProductList[position].productPrice)
                intent.putExtra("imageUrl", newProductList[position].imageUrl)
                intent.putExtra("peopleNum", newProductList[position].maxMember)
                intent.putExtra("nowMember", newProductList[position].nowMember)
                startActivity(intent)
            }

        })

        // 데이터를 가져오는 함수 호출
        getProductData()


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

    @SuppressLint("NotifyDataSetChanged")
    private fun getProductData() {
        // Firestore에서 데이터 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = firestore.collection("product").get().await()
                for (document in querySnapshot.documents) {
                    val productName = document.getString("productName")
                    val productDetail = document.getString("productDetail")
                    val productPrice = document.getLong("productPrice")
                    val maxMember = document.getLong("maxMember")?.toInt() ?: 0
                    val nowMember = document.getLong("nowMember")?.toInt() ?: 0
                    val imageUrl = document.getString("imageUrl")

                    if (productName != null && productDetail != null && productPrice != null && imageUrl != null) {
                        val product = Product(
                            productName,
                            productDetail,
                            productPrice.toInt(),
                            imageUrl,
                            maxMember,
                            nowMember
                        )
                        newProductList.add(product)
                        Log.d("productList", "getProductData: $newProductList")
                    }
                }

                // 데이터가 변경되었으므로 어댑터에 알리기
                runOnUiThread {
                    newRecyclerView.adapter!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                // 오류 처리
                e.printStackTrace()
            }
        }


    }


//        // 리스트뷰에 어댑터 연결
//        val Adapter = ProductAdapter(this, ProductList)
//        binding.listView.adapter = Adapter
//
//        // firestore에서 데이터 가져오기
//        val productsCollection = firestore.collection("product")
//
//        productsCollection.get().addOnSuccessListener { documents ->
//            ProductList.clear() // 기존 리스트 초기화
//            for (document in documents) {
//                val productName = document.getString("productName")
//                val productDetail = document.getString("productDetail")
//                val productPrice = document.getLong("productPrice")
//                val maxMember = document.getLong("maxMember")?.toInt() ?: 0
//                val nowMember = document.getLong("nowMember")?.toInt() ?: 0
//                val imageUrl = document.getString("imageUrl")
////                val productId = document.getString("productId")
//
//                // null 값이 아닐 경우에만 리스트에 추가
//                // imageUrl은 null이어도 상관없나 고민중
//                if (productName != null && productDetail != null && productPrice != null && imageUrl != null) {
//                    val product = Product(productName, productDetail, productPrice.toInt(), imageUrl, maxMember, nowMember)
//                    ProductList.add(product) // 리스트에 추가
//                }
//                Log.d("Firestore", "Product List: $ProductList")
//            }
//            Adapter.notifyDataSetChanged()
//        }
//
//        // 리스트뷰의 아이템 클릭시 이벤트
//        binding.listView.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
//                val selectItem = parent.getItemAtPosition(position) as Product
//                intent = Intent(this, PreviewActivity::class.java)
//                intent.putExtra("productName", selectItem.productName)
//                intent.putExtra("productDetail", selectItem.productDetail)
//                intent.putExtra("productPrice", selectItem.productPrice)
//                intent.putExtra("imageUrl", selectItem.imageUrl)
//                intent.putExtra("peopleNum", selectItem.maxMember)
//                intent.putExtra("nowMember", selectItem.nowMember)
////                intent.putExtra("productId", selectItem.productId)
//                startActivity(intent)
//            }
//
//        // 방생성 버튼 클릭시 이벤트
//        binding.recruitBtn.setOnClickListener() {
//            val intent = Intent(this, RecruitRoomActivity::class.java)
//            startActivity(intent)
//        }
//        binding.bottomNavigation.selectedItemId = binding.bottomNavigation.menu.getItem(0).itemId
//        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.bottom_home -> {
//                    val intent = Intent(this, ItemListActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.bottom_chat -> {
//                    val intent = Intent(this, ChatListActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                R.id.bottom_mypage -> {
//                    val intent = Intent(this, MyPageActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//                else -> false
//            }
//        }
//
//    }
}
