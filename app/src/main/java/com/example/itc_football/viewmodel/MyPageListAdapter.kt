package com.example.itc_football.viewmodel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.itc_football.R

import com.example.itc_football.data.MypageList


class MyPageListAdapter(private val MyPageList: ArrayList<MypageList>) :
    RecyclerView.Adapter<MyPageListAdapter.MyViewHolder>() {
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.mypage_item, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = MyPageList[position]

        holder.productName.text = currentItem.productName
        holder.productPrice.text = currentItem.productPrice.toString()
        holder.roomAble.text = currentItem.roomAble
        when (currentItem.roomAble) {
            "공구중" -> holder.roomAble.setTextColor(holder.itemView.context.getColor(R.color.green))
            "공구완료" -> holder.roomAble.setTextColor(holder.itemView.context.getColor(R.color.red))
            else -> holder.roomAble.setTextColor(holder.itemView.context.getColor(R.color.gray))
        }
        holder.maxMember.text = currentItem.maxMember.toString()
        holder.nowMember.text = currentItem.nowMember.toString()


    }

    override fun getItemCount(): Int {
        return MyPageList.size
    }

    class MyViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val roomAble: TextView = itemView.findViewById(R.id.roomAble)
        val maxMember: TextView = itemView.findViewById(R.id.maxMember)
        val nowMember: TextView = itemView.findViewById(R.id.nowMember)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}