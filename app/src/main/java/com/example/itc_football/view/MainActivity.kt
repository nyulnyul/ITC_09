package com.example.itc_football.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.itc_football.R
import com.example.itc_football.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var homeFragment: ItemListFragment
    private lateinit var chatFragment: ChatListFragment
    private lateinit var myPageFragment: MyPageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프래그먼트 초기화
        homeFragment = ItemListFragment()
        chatFragment = ChatListFragment()
        myPageFragment = MyPageFragment()

        // 기본 화면으로 홈 프래그먼트 표시
        setFragment(homeFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> setFragment(homeFragment)
                R.id.bottom_chat -> setFragment(chatFragment)
                R.id.bottom_mypage -> setFragment(myPageFragment)
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // 프래그먼트 전환 애니메이션 설정
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.container, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }
}