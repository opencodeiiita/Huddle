package com.example.huddle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class UserAdapter(private val userList: List<User>,
                  private val selectedUserIds: MutableSet<String>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val user_name_tv = view.findViewById<TextView>(R.id.user_name)
        val user_email_tv = view.findViewById<TextView>(R.id.user_email)
        val profile_image = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profile_image)
        val item_bg = view.findViewById<ConstraintLayout>(R.id.user_item_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.user_name_tv.text = user.name
        holder.user_email_tv.text = user.email
        if (!user.profile.isNullOrEmpty() && user.profile != "null") {
            Glide.with(holder.itemView.context)
                .load(user.profile)
                .into(holder.profile_image)
        }

        if(selectedUserIds.contains(user.id))holder.item_bg.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.accent_sec))

        holder.item_bg.setOnClickListener {
            if (selectedUserIds.contains(user.id)) {
                holder.item_bg.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.background))
                selectedUserIds.remove(user.id)
            } else {
                holder.item_bg.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.accent_sec))
                selectedUserIds.add(user.id)
            }
        }
    }

    override fun getItemCount(): Int = userList.size
}
