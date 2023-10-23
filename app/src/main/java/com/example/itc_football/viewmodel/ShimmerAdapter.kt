package com.example.itc_football.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.itc_football.R

class ShimmerAdapter(private val itemCount: Int) : RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {

    class ShimmerViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity_placeholder, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        // No data binding is needed for the loading placeholder.
    }

    override fun getItemCount(): Int = itemCount
}
