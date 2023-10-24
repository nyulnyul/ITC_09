package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.itc_football.data.Product
import com.example.itc_football.databinding.ItemListFragmentBinding
import com.example.itc_football.viewmodel.ProductAdapter
import com.example.itc_football.viewmodel.ShimmerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ItemListFragment : Fragment() {
    private var _binding: ItemListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var newRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var mysrl: SwipeRefreshLayout? = null
    private var hasLoadedData = false

    private var newProductList = arrayListOf<Product>()
    private lateinit var shimmerAdapter: ShimmerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.setHasFixedSize(true)

        mysrl = binding.swipeLayout
        mysrl!!.setOnRefreshListener {
            refreshData()
        }

        // 방생성 버튼
        binding.recruitBtn.setOnClickListener {
            val intent = Intent(context, RecruitRoomActivity::class.java)
            startActivity(intent)
        }

        if (!hasLoadedData) {
            shimmerAdapter = ShimmerAdapter(10)  // Use an arbitrary number for the item count.
            newRecyclerView.adapter = shimmerAdapter
            getProductData()
        } else {
            setUpRealData(newProductList)
        }

        return view
    }

    private fun setUpRealData(productList: ArrayList<Product>) {
        val adapter = ProductAdapter(productList)

        newRecyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(context, PreviewActivity::class.java)
                val product = productList[position]

                intent.putExtra("productID", product.productID)
                intent.putExtra("productName", product.productName)
                intent.putExtra("productDetail", product.productDetail)
                intent.putExtra("productPrice", product.productPrice)
                intent.putExtra("maxMember", product.maxMember)
                intent.putExtra("nowMember", product.nowMember)
                startActivity(intent)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshData() {
        getProductData()
        mysrl!!.isRefreshing = false
    }

    @SuppressLint("NotifyDataSetChanged")
    @OptIn(DelicateCoroutinesApi::class)
    private fun getProductData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val querySnapshot = firestore.collection("product").get().await()
                val tempProductList = mutableListOf<Product>()

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
                        tempProductList.add(product)
                    }
                }

                activity?.runOnUiThread {
                    newProductList.clear()
                    newProductList.addAll(tempProductList)
                    newRecyclerView.adapter?.notifyDataSetChanged()
                    setUpRealData(newProductList)
                }
                hasLoadedData = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
