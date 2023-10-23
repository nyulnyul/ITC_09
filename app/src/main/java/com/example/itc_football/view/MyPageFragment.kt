package com.example.itc_football.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.itc_football.databinding.MyPageFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageFragment : Fragment() {
    private var _binding: MyPageFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyPageFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

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
                    }
                }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
