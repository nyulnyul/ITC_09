package com.example.itc_football

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ProductAdapter (val context: Context, val ProductList: ArrayList<Product>): BaseAdapter() {
    override fun getCount(): Int {
        return ProductList.size
    }

    override fun getItem(position: Int): Any {
        return ProductList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_activity, null)

        val productImage = view.findViewById<ImageView>(R.id.productImage)
        val productName = view.findViewById<TextView>(R.id.productName)
        val productPrice = view.findViewById<TextView>(R.id.productPrice)
        val maxMember = view.findViewById<TextView>(R.id.maxMember)
        val nowMember = view.findViewById<TextView>(R.id.nowMember)

        val product = ProductList[position]

//        productImage.setImageResource(product.productImage)
        productName.text = product.productName
        productPrice.text = product.productPrice.toString()
        maxMember.text = product.maxMember.toString()
        nowMember.text = product.nowMember.toString()

        return view
    }

}