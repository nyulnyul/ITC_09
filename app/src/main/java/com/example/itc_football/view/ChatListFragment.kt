package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.itc_football.data.MyChat
import com.example.itc_football.databinding.ChatListFragmentBinding
import com.example.itc_football.viewmodel.MyChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ChatListFragment : Fragment() {
    private lateinit var binding: ChatListFragmentBinding
    private lateinit var newRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var mysrl: SwipeRefreshLayout? = null

    private var newProductList = arrayListOf<MyChat>()
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newRecyclerView.setHasFixedSize(true)

        // 새로고침
        mysrl = binding.swipeLayout
        mysrl!!.setOnRefreshListener {
            refreshData()
        }

        runBlocking {
            getChatData()
        }


        // 어댑터를 생성하고 리사이클러뷰에 연결
        val adapter = MyChatAdapter(newProductList)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : MyChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), ChatActivity::class.java)
                val user = FirebaseAuth.getInstance().currentUser
                val email = user?.email ?: ""

                intent.putExtra(ChatActivity.USERNAME, email)
                intent.putExtra("productID", newProductList[position].productID)
                intent.putExtra("productName", newProductList[position].productName)
                intent.putExtra("productPrice", newProductList[position].productPrice)
                intent.putExtra("maxMember", newProductList[position].maxMember)
                intent.putExtra("nowMember", newProductList[position].nowMember)
                startActivity(intent)
                Log.e("TAG", "onItemClick: " + newProductList[position].productID)
            }
        })

        return view
    }

    private fun refreshData() {
        getChatData()
        mysrl!!.isRefreshing = false
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    private fun getChatData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                val tempProductList = mutableListOf<MyChat>()
                if (currentUserUid != null) {
                    val productCollection = firestore.collection("product")
                    val productSnapshot = productCollection.get().await()

                    for (productDocument in productSnapshot.documents) {
                        val memberCollection = productDocument.reference.collection("member")
                        val memberSnapshot =
                            memberCollection.whereEqualTo("uid", currentUserUid).get().await()

                        // 만약 현재 사용자의 uid가 'member' 컬렉션에 있다면, 이 상품을 리스트에 추가합니다.
                        if (!memberSnapshot.isEmpty) {
                            val orignPname = productDocument.getString("productName")
                            var productName = orignPname?.take(7) ?: "" // 처음 7글자만 가져오기
                            if (orignPname != null && orignPname.length > 7) { // 만약 길이가 7을 초과한다면
                                productName += ".." // ".." 추가하기
                            }
                            val splitName = productDocument.getString("maker")?.split("_")
                            val userName = splitName?.get(1)
                            var lastTalk: String?
                            val msgCollection = productDocument.reference.collection("msg")
                            val lastMsgDocSnapshots =
                                msgCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                                    .limit(1).get().await()

                            lastTalk = if (!lastMsgDocSnapshots.isEmpty) {
                                lastMsgDocSnapshots.documents[0].getString("text")
                            } else {
                                "아직 채팅이 시작되지 않았습니다"
                            }
                            val maxMember = productDocument.getLong("maxMember")?.toInt() ?: 0
                            val nowMember = productDocument.getLong("nowMember")?.toInt() ?: 0
                            val productID = productDocument.getString("productID")
                            val productPrice = productDocument.getLong("productPrice")?.toInt() ?: 0

                            if (userName != null && lastTalk != null && productID != null) {
                                val product = MyChat(
                                    productName,
                                    userName,
                                    lastTalk,
                                    maxMember,
                                    nowMember,
                                    productID,
                                    productPrice
                                )
                                tempProductList.add(product)
                                Log.d("productList", "getProductData: $newProductList")
                            }
                        }
                    }

                    // 데이터가 변경되었으므로 어댑터에 알리기
                    activity?.runOnUiThread {
                        newProductList.clear()
                        newProductList.addAll(tempProductList) // 기존 리스트를 새로운 리스트로 교체
                        newRecyclerView.adapter?.notifyDataSetChanged()
                    }
                    isDataLoaded = true
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
