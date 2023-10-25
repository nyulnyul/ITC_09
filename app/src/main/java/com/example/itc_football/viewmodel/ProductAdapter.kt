package com.example.itc_football.viewmodel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.itc_football.data.Product
import com.example.itc_football.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProductAdapter(private val productList: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = productList[position]

        holder.productName.text = currentItem.productName
        holder.productPrice.text = "${currentItem.productPrice}원"
        holder.maxMember.text = currentItem.maxMember.toString()
        holder.nowMember.text = currentItem.nowMember.toString()
        holder.roomAble.text = currentItem.roomAble


        val storageRef = Firebase.storage.reference.child("${currentItem.productID}.png")
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(holder.itemView.context).load(it).into(holder.imgProduct)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val imgProduct: ShapeableImageView = itemView.findViewById(R.id.img_product)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val maxMember: TextView = itemView.findViewById(R.id.maxMember)
        val nowMember: TextView = itemView.findViewById(R.id.nowMember)
        val roomAble : TextView = itemView.findViewById(R.id.roomAble)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


}