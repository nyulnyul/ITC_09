package com.example.itc_football.view_model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itc_football.model.Product
import com.example.itc_football.R
import com.google.android.material.imageview.ShapeableImageView

class ProductAdapter(private val productList : ArrayList<Product>): RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = productList[position]

        holder.productName.text = currentItem.productName
        holder.productPrice.text = currentItem.productPrice.toString()
        holder.maxMember.text = currentItem.maxMember.toString()
        holder.nowMember.text = currentItem.nowMember.toString()
        // Glide를 이용하여 ImageView에 url 이미지를 세팅
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.imageUrl)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val imageUrl: ShapeableImageView = itemView.findViewById(R.id.imageUrl)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val maxMember: TextView = itemView.findViewById(R.id.maxMember)
        val nowMember: TextView = itemView.findViewById(R.id.nowMember)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    //    override fun getCount(): Int {
//        return ProductList.size
//    }
//
//    override fun getItem(position: Int): Any {
//        return ProductList[position]
//    }
//
//    override fun getItemId(position: Int): Long {
//        return 0
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val view: View = LayoutInflater.from(context).inflate(R.layout.item_activity, null)
//
//        val imageUrl = view.findViewById<ShapeableImageView>(R.id.imageUrl)
//        val productName = view.findViewById<TextView>(R.id.productName)
//        val productPrice = view.findViewById<TextView>(R.id.productPrice)
//        val maxMember = view.findViewById<TextView>(R.id.maxMember)
//        val nowMember = view.findViewById<TextView>(R.id.nowMember)
//
//        val product = ProductList[position]
//
//        productName.text = product.productName
//        productPrice.text = product.productPrice.toString()
//        maxMember.text = product.maxMember.toString()
//        nowMember.text = product.nowMember.toString()
//        // Glide를 이용하여 ImageView에 url 이미지를 세팅
//        Glide.with(context).load(product.imageUrl).into(imageUrl)
//
//        return view
//    }

}