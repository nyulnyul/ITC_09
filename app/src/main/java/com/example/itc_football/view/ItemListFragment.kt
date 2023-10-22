package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.itc_football.Product
import com.example.itc_football.ProductAdapter
import com.example.itc_football.databinding.ItemListFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ItemListFragment : Fragment() {
    private var _binding: ItemListFragmentBinding ?= null
    private val binding get() = _binding!!
    private lateinit var newRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var mysrl: SwipeRefreshLayout? = null

    private var newProductList = arrayListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        // 데이터를 가져오는 함수 호출
        getProductData()

        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.setHasFixedSize(true)

        // 새로고침
        mysrl = binding.swipeLayout
        mysrl!!.setOnRefreshListener {
            newProductList.clear()
            getProductData()
            mysrl!!.isRefreshing = false
        }

        // 어댑터를 생성하고 리사이클러뷰에 연결
        val adapter = ProductAdapter(newProductList)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(context, PreviewActivity::class.java)

                intent.putExtra("productID", newProductList[position].productID)
                intent.putExtra("productName", newProductList[position].productName)
                intent.putExtra("productDetail", newProductList[position].productDetail)
                intent.putExtra("productPrice", newProductList[position].productPrice)

                intent.putExtra("maxMember", newProductList[position].maxMember)
                intent.putExtra("nowMember", newProductList[position].nowMember)
                startActivity(intent)
            }
        })

        // 방 생성 버튼
        binding.recruitBtn.setOnClickListener {
            val intent = Intent(context, RecruitRoomActivity::class.java)
            startActivity(intent)
        }

        return view
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
                            productID
                        )
                        newProductList.add(product)
                    }
                }

                // 데이터가 변경되었으므로 어댑터에 알리기
                activity?.runOnUiThread {
                    newRecyclerView.adapter?.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                // 오류 처리
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
