package com.example.huddle.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.adapters.ChatListAdapter
import com.example.huddle.dialogs.AddChatDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class CommunityFragment : Fragment() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatShimmerLayout: ShimmerFrameLayout
    private lateinit var chatAdapter: ChatListAdapter
    private val chatList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.fragment_chat_add).setOnClickListener {
            val addChatDialog: DialogFragment = AddChatDialog()
            addChatDialog.show(parentFragmentManager, "AddChatDialog")
        }

        chatShimmerLayout = view.findViewById(R.id.chat_shimmer_layout)
        chatShimmerLayout.startShimmer()

        chatRecyclerView = view.findViewById(R.id.chat_list_rv)
        chatRecyclerView.isNestedScrollingEnabled = false
        chatRecyclerView.layoutManager =
            object : LinearLayoutManager(view.context) {
                override fun canScrollVertically() = false
            }
        chatAdapter = ChatListAdapter(chatList)
        chatRecyclerView.adapter = chatAdapter
        val noResultsTv = view.findViewById<TextView>(R.id.no_results_tv)

        val currentUser = Firebase.auth.currentUser?.uid

        if (currentUser != null) {
            val chatListRef = Firebase.firestore.collection("ChatList").document(currentUser)

            chatListRef.addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    Log.e("FireStore", "Error listening to chat list updates", error)
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val chatList = documentSnapshot.get("chatList") as? List<*>
                    if (chatList != null) {
                        this.chatList.clear()
                        this.chatList.addAll(chatList.mapNotNull { it as? String })
                        chatAdapter.notifyDataSetChanged()
                        chatShimmerLayout.stopShimmer()
                        chatShimmerLayout.visibility = View.GONE
                        chatRecyclerView.visibility = View.VISIBLE

                        if (this.chatList.isEmpty()) {
                            noResultsTv.visibility = View.VISIBLE
                        } else {
                            noResultsTv.visibility = View.GONE
                        }
                    }
                } else {
                    noResultsTv.visibility = View.VISIBLE
                    chatShimmerLayout.visibility = View.GONE
                }
            }


        }
    }
}
