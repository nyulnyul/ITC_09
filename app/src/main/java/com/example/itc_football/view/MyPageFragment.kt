package com.example.itc_football.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.itc_football.databinding.MyPageFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.itc_football.data.MypageList
import com.example.itc_football.viewmodel.MyChatAdapter
import com.example.itc_football.viewmodel.MyPageListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyPageFragment : Fragment() {
    private var _binding: MyPageFragmentBinding? = null
    private lateinit var newRecyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private var newMypageList = arrayListOf<MypageList>()
    private var isDataLoaded = false
    private val binding get() = _binding!!
    private lateinit var handler: Handler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyPageFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        newRecyclerView.setHasFixedSize(true)
        if (!isDataLoaded) {
            // 데이터를 가져오기
            getChatData()
        }

        binding.dataview.visibility = View.GONE

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val dept = document.getString("dept")
                        val email = document.getString("email")

                        binding.myname.text = name
                        binding.mydept.text = dept
                        if (email != null) {
                            binding.mynum.text = email.split("@")[0]
                        }
                        // 쉬머 효과 중지 및 데이터 뷰 표시
                        stopShimmerEffect()
                        showDataView()
                    }
                }
        }

        startShimmerEffect()
        // 어댑터를 생성하고 리사이클러뷰에 연결
        val adapter = MyPageListAdapter(newMypageList)
        newRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : MyPageListAdapter.OnItemClickListener {
                        override fun onItemClick(position:Int){

                // 파이어스토어에서 member 컬렉션 조회
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        activity?.runOnUiThread {

                                val previewIntent = Intent(context, PreviewActivity::class.java)
                                previewIntent.putExtra("productID", newMypageList[position].productID)
                                previewIntent.putExtra("productName", newMypageList[position].productName)
                                previewIntent.putExtra("productDetail", newMypageList[position].productDetail)
                                previewIntent.putExtra("productPrice", newMypageList[position].productPrice)

                                previewIntent.putExtra("maxMember", newMypageList[position].maxMember)
                                previewIntent.putExtra("nowMember", newMypageList[position].nowMember)

                                startActivity(previewIntent)


                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                }

            }
//            override fun onItemClick(position: Int) {
//                val intent = Intent(requireContext(), ChatActivity::class.java)
//                val user = FirebaseAuth.getInstance().currentUser
//                val email = user?.email ?: ""
//
//                intent.putExtra(ChatActivity.USERNAME, email)
//                intent.putExtra("productID", newMypageList[position].productID)
//                intent.putExtra("productName", newMypageList[position].productName)
//                intent.putExtra("productPrice", newMypageList[position].productPrice)
//                intent.putExtra("maxMember", newMypageList[position].maxMember)
//                intent.putExtra("nowMember", newMypageList[position].nowMember)
//                startActivity(intent)
//                Log.e("TAG", "onItemClick: " + newMypageList[position].productID)
//            }
        })
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

//        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
    private fun startShimmerEffect() {
        binding.shimmerViewMypage.visibility = View.VISIBLE
        binding.shimmerViewMypage.startShimmer()
    }

    private fun stopShimmerEffect() {
        binding.shimmerViewMypage.stopShimmer()
        binding.shimmerViewMypage.visibility = View.GONE
    }

    private fun showDataView() {
        binding.dataview.visibility = View.VISIBLE
    }
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("NotifyDataSetChanged")
    private fun getChatData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                val tempProductList = mutableListOf<MypageList>()
                if (currentUserUid != null) {
                    val productCollection = firestore.collection("product")
                    val productSnapshot = productCollection.get().await()

                    for (productDocument in productSnapshot.documents) {
                        val maker = productDocument.getString("maker")
                        val makerUid = maker?.split("_")?.get(0)
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                        if (currentUserId == makerUid) {
                            val orignPname = productDocument.getString("productName")
                            var productName = orignPname?.take(7) ?: "" // 처음 7글자만 가져오기
                            if (orignPname != null && orignPname.length > 7) { // 만약 길이가 7을 초과한다면
                                productName += ".." // ".." 추가하기
                            }

                            val maxMember = productDocument.getLong("maxMember")?.toInt() ?: 0
                            val nowMember = productDocument.getLong("nowMember")?.toInt() ?: 0
                            val productID = productDocument.getString("productID")
                            val productPrice = productDocument.getLong("productPrice")?.toInt() ?: 0
                            val roomAble = productDocument.getString("roomAble")
                            val productDetail = productDocument.getString("productDetail")

                            if (productName != null && productDetail!= null &&roomAble != null && productID != null) {
                                val product = MypageList(
                                    productName,
                                    maxMember,
                                    nowMember,
                                    productID,
                                    productPrice,
                                    roomAble,
                                    productDetail

                                )
                                tempProductList.add(product)
                                Log.d("productList", "getProductData: $newMypageList")
                            }
                        }
                    }

                    // 데이터가 변경되었으므로 어댑터에 알리기
                    activity?.runOnUiThread {
                        newMypageList.clear()
                        newMypageList.addAll(tempProductList) // 기존 리스트를 새로운 리스트로 교체
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
