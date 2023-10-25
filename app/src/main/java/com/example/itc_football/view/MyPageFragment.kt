package com.example.itc_football.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.itc_football.databinding.MyPageFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Handler

class MyPageFragment : Fragment() {
    private var _binding: MyPageFragmentBinding? = null

    private val binding get() = _binding!!
    private lateinit var handler: Handler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = MyPageFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.dataview.visibility = View.GONE

//        // 쉬머 효과 시작하기 전에 3초 지연 실행을 위한 핸들러 생성
//        handler = Handler()
//
//        handler.postDelayed({
//            stopShimmerEffect()
//            showDataView()
//        }, 1000)
//
//        startShimmerEffect()




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
}
