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
import com.example.itc_football.databinding.ChatListFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatListFragment : Fragment() {
    private var _binding: ChatListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var chatList = arrayListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ChatListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        // 데이터를 가져오는 함수 호출
        getChatData()

        chatRecyclerView = binding.recyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        chatRecyclerView.setHasFixedSize(true)

        // 새로고침
        swipeRefreshLayout = binding.swipeLayout
        swipeRefreshLayout!!.setOnRefreshListener {
            chatList.clear()
            getChatData()
            swipeRefreshLayout!!.isRefreshing = false
        }

        // 어댑터를 생성하고 리사이클러뷰에 연결
        val adapter = ProductAdapter(chatList)
        chatRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(context, PreviewActivity::class.java)

                intent.putExtra("productID", chatList[position].productID)
                intent.putExtra("productName", chatList[position].productName)
                intent.putExtra("productDetail", chatList[position].productDetail)
                intent.putExtra("productPrice", chatList[position].productPrice)

                intent.putExtra("maxMember", chatList[position].maxMember)
                intent.putExtra("nowMember", chatList[position].nowMember)
                startActivity(intent)
            }
        })
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getChatData() {
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
                        chatList.add(product)
                    }
                }

                // 데이터가 변경되었으므로 어댑터에 알리기
                activity?.runOnUiThread {
                    chatRecyclerView.adapter?.notifyDataSetChanged()
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
