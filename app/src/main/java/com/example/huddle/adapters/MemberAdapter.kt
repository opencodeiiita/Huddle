package com.example.huddle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.utility.decodeBase64ToBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import de.hdodenhof.circleimageview.CircleImageView

class MemberAdapter(private val memberList: List<String>) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memberImage: CircleImageView = view.findViewById(R.id.image_member)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = memberList[position]
        Firebase.firestore.collection("users").document(member).get().addOnSuccessListener {
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
