package com.example.huddle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.data.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val userList: List<User>,
                  private val selectedUserIds: MutableSet<String>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTv: TextView = view.findViewById(R.id.user_name)
        val userEmailTv: TextView = view.findViewById(R.id.user_email)
        val profileImage: CircleImageView = view.findViewById(R.id.profile_image)
        val itemBg: ConstraintLayout = view.findViewById(R.id.user_item_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userNameTv.text = user.name
        holder.userEmailTv.text = user.email
        if (user.profile.isNotEmpty() && user.profile != "null") {
            Glide.with(holder.itemView.context)
                .load(user.profile)
                .into(holder.profileImage)
        }

        if(selectedUserIds.contains(user.id))holder.itemBg.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.accent_sec))

        holder.itemBg.setOnClickListener {
            if (selectedUserIds.contains(user.id)) {
                holder.itemBg.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.background))
                selectedUserIds.remove(user.id)
            } else {
                holder.itemBg.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.accent_sec))
                selectedUserIds.add(user.id)
            }
        }
    }

    override fun getItemCount(): Int = userList.size
}
