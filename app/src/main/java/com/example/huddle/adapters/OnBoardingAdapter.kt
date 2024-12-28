package com.example.huddle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnBoardingAdapter(private val layouts: List<Int>) : RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>() {

    inner class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    override fun getItemViewType(position: Int): Int = layouts[position]

    override fun getItemCount(): Int = layouts.size
}
