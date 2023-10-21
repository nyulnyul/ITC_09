package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.itc_football.Product
import com.example.itc_football.ProductAdapter
import com.example.itc_football.R
import com.example.itc_football.databinding.ChatListActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatListActivity : AppCompatActivity() {
    private lateinit var binding: ChatListActivityBinding
    private lateinit var newRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var mysrl: SwipeRefreshLayout? = null

    private var newProductList = arrayListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatListActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 데이터를 가져오는 함수 호출
        getChatData()

        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        // 새로고침
        mysrl = binding.swipeLayout
        mysrl!!.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            newProductList.clear()
            getChatData()
            mysrl!!.isRefreshing = false
        })

        // 어댑터를 생성하고 리사이클러뷰에 연결
        val adapter = ProductAdapter(newProductList)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

                val intent = Intent(this@ChatListActivity, PreviewActivity::class.java)

                intent.putExtra("productID", newProductList[position].productID)
                intent.putExtra("productName", newProductList[position].productName)
                intent.putExtra("productDetail", newProductList[position].productDetail)
                intent.putExtra("productPrice", newProductList[position].productPrice)

                intent.putExtra("maxMember", newProductList[position].maxMember)
                intent.putExtra("nowMember", newProductList[position].nowMember)
                startActivity(intent)
                Log.e("TAG", "onItemClick: " + newProductList[position].productID)
            }

        })




        binding.bottomNavigation.selectedItemId = R.id.bottom_chat
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
    private fun getChatData() {
        // Firestore에서 데이터 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserUid != null) {
                    val productCollection = firestore.collection("product")
                    val productSnapshot = productCollection.get().await()

                    for (productDocument in productSnapshot.documents) {
                        val memberCollection = productDocument.reference.collection("member")
                        val memberSnapshot =
                            memberCollection.whereEqualTo("uid", currentUserUid).get().await()

                        // 만약 현재 사용자의 uid가 'member' 컬렉션에 있다면, 이 상품을 리스트에 추가합니다.
                        if (!memberSnapshot.isEmpty) {
                            val productName = productDocument.getString("productName")
                            val productDetail = productDocument.getString("productDetail")
                            val productPrice = productDocument.getLong("productPrice")
                            val maxMember = productDocument.getLong("maxMember")?.toInt() ?: 0
                            val nowMember = productDocument.getLong("nowMember")?.toInt() ?: 0
                            val productID = productDocument.getString("productID")

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
                    }

                    // 데이터가 변경되었으므로 어댑터에 알리기
                    runOnUiThread {
                        newRecyclerView.adapter!!.notifyDataSetChanged()
                    }
                } else {
                    Log.d("TAG", "getChatData: currentUserUid is null")

                }
            } catch (e: Exception) {
                // 오류 처리
                e.printStackTrace()
            }
        }
    }
}