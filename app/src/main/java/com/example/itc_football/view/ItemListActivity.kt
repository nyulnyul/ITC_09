package com.example.itc_football.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.itc_football.Product
import com.example.itc_football.ProductAdapter
import com.example.itc_football.R
import com.example.itc_football.databinding.ItemListActivityBinding

class ItemListActivity : AppCompatActivity() {
    private lateinit var binding: ItemListActivityBinding

    var ProductList = arrayListOf<Product>(
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10),
        Product("pencil", 2000000, R.drawable.baseline_mode_24, 10)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemListActivityBinding.inflate(layoutInflater)
        Log.e("err", "err")
        print("good")

        val view = binding.root
        setContentView(view)

        val Adapter = ProductAdapter(this, ProductList)
        binding.listView.adapter = Adapter

        binding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectItem = parent.getItemAtPosition(position) as Product
                intent = Intent(this, DetailedActivity::class.java)
                intent.putExtra("productName", selectItem.productName)
                intent.putExtra("productPrice", selectItem.productPrice)
                intent.putExtra("productImage", selectItem.productImage)
                intent.putExtra("peopleNum", selectItem.peopleNum)
                startActivity(intent)
            }

    }


}