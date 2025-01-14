package com.example.huddle.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.activities.ChatActivity
import com.example.huddle.data.User
import com.example.huddle.utility.decodeBase64ToBitmap
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import de.hdodenhof.circleimageview.CircleImageView

class AddChatAdapter(private val userList: List<User>, private val fragment: DialogFragment) : RecyclerView.Adapter<AddChatAdapter.UserViewHolder>() {
    private fun dismissDialog() {
        fragment.dismiss()
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTv: TextView = view.findViewById(R.id.user_name)
        val userEmailTv: TextView = view.findViewById(R.id.user_email)
        val profileImage: CircleImageView = view.findViewById(R.id.profile_image)
        val itemBg: MaterialCardView = view.findViewById(R.id.user_item_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userNameTv.text = user.name
        holder.userEmailTv.text = user.email
        if (user.profile.isNotEmpty() && user.profile != "null" && user.profile != "1") {
            Glide.with(holder.itemView.context)
                .load(user.profile)
                .into(holder.profileImage)
        } else if (user.profile == "1") {
            val profile_64 = Firebase.firestore.collection("users").document(user.id).get().result.getString("profile_64")
            holder.profileImage.setImageBitmap(profile_64?.let { decodeBase64ToBitmap(it) })
        }
        val currentUser = Firebase.auth.currentUser?.uid

        holder.itemBg.setOnClickListener {
            if (currentUser != null) {
                val chatListRef = Firebase.firestore.collection("ChatList").document(currentUser)

                chatListRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            chatListRef.update("chatList", FieldValue.arrayUnion(user.id))
                        } else {
                            val chatListData = mapOf("chatList" to listOf(user.id))
                            chatListRef.set(chatListData)
                        }

                    }
                    .addOnFailureListener { exception ->
                        Log.e("FireStore", "Error updating chat list", exception)
                    }

                val chatListRef2 = Firebase.firestore.collection("ChatList").document(user.id)

                chatListRef2.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            chatListRef2.update("chatList", FieldValue.arrayUnion(currentUser))
                        } else {
                            val chatListData = mapOf("chatList" to listOf(currentUser))
                            chatListRef2.set(chatListData)
                        }

                        val intent = Intent(holder.itemView.context, ChatActivity::class.java)
                        intent.putExtra("id", user.id)
                        holder.itemView.context.startActivity(intent)
                        dismissDialog()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FireStore", "Error updating chat list", exception)
                    }
            }
        }
    }

    override fun getItemCount(): Int = userList.size
}
