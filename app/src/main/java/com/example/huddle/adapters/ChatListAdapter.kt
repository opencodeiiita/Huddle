package com.example.huddle.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.activities.ChatActivity
import com.example.huddle.utility.getTimeAgo
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter(private val userList: List<String>) : RecyclerView.Adapter<ChatListAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTv: TextView = view.findViewById(R.id.user_name)
        val userEmailTv: TextView = view.findViewById(R.id.user_email)
        val profileImage: CircleImageView = view.findViewById(R.id.profile_image)
        val itemBg: MaterialCardView = view.findViewById(R.id.user_item_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return UserViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        val userDocument = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user)

        holder.userNameTv.text = "Loading..."
        holder.userEmailTv.text = "Loading..."

        holder.itemBg.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("id", user)
            holder.itemView.context.startActivity(intent)
        }

        userDocument.addSnapshotListener { documentSnapshot, error ->
            if (error != null) {
                holder.userNameTv.text = "Error Loading Data"
                holder.userEmailTv.text = "Error Loading Data"
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name")
                val lastSeen = documentSnapshot.getLong("lastSeen")
                val profileUrl = documentSnapshot.getString("profile")

                holder.userNameTv.text = name ?: "N/A"

                if (lastSeen?.toInt() == 0) {
                    holder.userEmailTv.text = "Active Now"
                } else {
                    holder.userEmailTv.text = lastSeen?.let { getTimeAgo(it) }
                }

                if (!profileUrl.isNullOrEmpty() && profileUrl != "null") {
                    try {
                        Glide.with(holder.itemView.context)
                            .load(profileUrl)
                            .into(holder.profileImage)
                    } catch(e: Exception) {
                        Log.e("ProfileFragment", "Error loading profile picture: ${e.message}")
                    }
                }
            } else {
                holder.userNameTv.text = "No Data Found"
                holder.userEmailTv.text = "No Data Found"
            }
        }
    }


    override fun getItemCount(): Int = userList.size
}
