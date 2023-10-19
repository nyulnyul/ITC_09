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
import com.example.itc_football.Product
import com.example.itc_football.ProductAdapter
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

        // 데이터를 가져오는 함수 호출
        getProductData()

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

                intent.putExtra("productID", newProductList[position].productID)
                intent.putExtra("productName", newProductList[position].productName)
                intent.putExtra("productDetail", newProductList[position].productDetail)
                intent.putExtra("productPrice", newProductList[position].productPrice)

                intent.putExtra("peopleNum", newProductList[position].maxMember)
                intent.putExtra("nowMember", newProductList[position].nowMember)
                startActivity(intent)
                Log.e("TAG", "onItemClick: " + newProductList[position].productID)
            }

        })

        // 방생성 버튼
        binding.recruitBtn.setOnClickListener() {
            val intent = Intent(this, RecruitRoomActivity::class.java)
            startActivity(intent)
        }
        // 채팅 테스트 버튼
//        binding.chatBtn.setOnClickListener() {
//            val email = intent.getStringExtra(ChatActivity.USERNAME) ?: ""
//            val intent = Intent(this, ChatActivity::class.java)
//            intent.putExtra(ChatActivity.USERNAME, email)
//            startActivity(intent)
//        }


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
                    val productID = document.getString("productID")

                    if (productName != null && productDetail != null && productPrice != null && productID != null) {
                        val product = Product(
                            productName,
                            productDetail,
                            productPrice.toInt(),
                            maxMember,
                            nowMember,
                            productID,
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
}
