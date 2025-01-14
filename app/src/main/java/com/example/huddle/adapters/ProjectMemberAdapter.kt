package com.example.huddle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.utility.decodeBase64ToBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProjectMemberAdapter(private val memberList: List<String>) : RecyclerView.Adapter<ProjectMemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memberImage: ImageView = view.findViewById(R.id.image_member)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_member_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        if (position == 0) {
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = 0.dp.value.toInt()
            holder.itemView.layoutParams = layoutParams
        }

        Firebase.firestore.collection("users").document(memberList[position]).get().addOnSuccessListener {
            val profile = it.getString("profile")
            if (!profile.isNullOrEmpty() && profile != "null" && profile != "1") {
                Glide.with(holder.itemView.context)
                    .load(profile)
                    .into(holder.memberImage)
            } else if (profile == "1") {
                val profile_64 = it.getString("profile_64")
                holder.memberImage.setImageBitmap(profile_64?.let { decodeBase64ToBitmap(it) })
            }
        }
    }

    override fun getItemCount(): Int = memberList.size
}
