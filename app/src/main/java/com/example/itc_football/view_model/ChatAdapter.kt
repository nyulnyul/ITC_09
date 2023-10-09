package com.example.itc_football.view_model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.itc_football.databinding.ItemChatOtherBinding
import com.example.itc_football.databinding.ItemChatSelfBinding
import com.example.itc_football.model.Chat

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SELF = 1
    private val ITEM_OTHER = 2

    private val diffacallback = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffacallback)

    fun submitData(chats: List<Chat>) {
        differ.submitList(chats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ITEM_SELF){
            val binding = ItemChatSelfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SelfChatItemViewHolder(binding)
    }else{
            val binding = ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OtherChatItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = differ.currentList[position]
        if(chat.isSelf){
            (holder as SelfChatItemViewHolder).bind(chat)
        }else{
            (holder as OtherChatItemViewHolder).bind(chat)
        }
    }

    inner class OtherChatItemViewHolder(val binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(chat: Chat){
                binding.apply {
                    name.text=chat.username
                    msg.text=chat.text

                }
            }
    }

    inner class SelfChatItemViewHolder(val binding: ItemChatSelfBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat){
            binding.apply {
                name.text=chat.username
                msg.text=chat.text

            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = differ.currentList[position]
        return if (chat.isSelf) ITEM_SELF else ITEM_OTHER
    }

    override fun getItemCount(): Int {
        return 0
    }
}