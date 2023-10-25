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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
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
            override fun onItemClick(position:Int){
                val productID = newProductList[position].productID
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                // Firestore 인스턴스 생성
                val firestore = FirebaseFirestore.getInstance()

                // 파이어스토어에서 member 컬렉션 조회
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val productDocument = firestore.collection("product").document(productID)
                        val memberSnapshot =
                            productDocument.collection("member").whereEqualTo("uid", currentUserUid).get().await()

                        // 현재 사용자의 uid가 member 컬렉션에 있다면 ChatActivity로 이동, 없다면 PreviewActivity로 이동
                        activity?.runOnUiThread {

                                val previewIntent = Intent(context, PreviewActivity::class.java)
                                previewIntent.putExtra("productID", newProductList[position].productID)
                                previewIntent.putExtra("productName", newProductList[position].productName)
                                previewIntent.putExtra("productDetail", newProductList[position].productDetail)
                                previewIntent.putExtra("productPrice", newProductList[position].productPrice)

                                previewIntent.putExtra("maxMember", newProductList[position].maxMember)
                                previewIntent.putExtra("nowMember", newProductList[position].nowMember)

                                startActivity(previewIntent)


                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }

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
                    val roomAble = document.getString("roomAble")

                    if (productName != null && productDetail != null && productPrice != null && productID != null) {
                        val product = Product(
                            productName,
                            productDetail,
                            productPrice.toInt(),
                            maxMember,
                            nowMember,
                            productID,
                            roomAble.toString()

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
