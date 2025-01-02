package com.example.huddle.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.adapters.ChatAdapter
import com.example.huddle.data.Chat
import com.example.huddle.data.Task
import com.example.huddle.utility.getTimeAgo
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiPopup

class ChatActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getStringExtra("id") ?: ""
        val profilePic = findViewById<ImageView>(R.id.chat_image)
        val profileNameTv = findViewById<TextView>(R.id.chat_name_tv)
        val profileTimeTv = findViewById<TextView>(R.id.chat_time_tv)

        findViewById<MaterialCardView>(R.id.chat_back).setOnClickListener {
            finish()
        }

        Firebase.firestore.collection("users").document(id).addSnapshotListener { documentSnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name")
                val profileUrl = documentSnapshot.getString("profile")
                val lastSeen = documentSnapshot.getLong("lastSeen")

                profileNameTv.text = name ?: "N/A"
                profileTimeTv.text = if(lastSeen == 0.toLong()) "Online" else lastSeen?.let { getTimeAgo(it) } ?: "N/A"

                if (!profileUrl.isNullOrEmpty() && profileUrl != "null") {
                    try {
                        Glide.with(this)
                            .load(profileUrl)
                            .into(profilePic)
                    } catch(e: Exception) {
                        Log.e("ProfileFragment", "Error loading profile picture: ${e.message}")
                    }
                }
            } else {
                profileNameTv.text = "No Data Found"
                profileTimeTv.text = "No Data Found"
            }
        }
        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("isNightMode", false)

        val window = window
        if (isNightMode) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val emojiEditText = findViewById<EmojiEditText>(R.id.chat_message_edt)
        val rootView = findViewById<RelativeLayout>(R.id.main)
        val emojiPopup = EmojiPopup(rootView, emojiEditText)
        val emojiPng = findViewById<ImageView>(R.id.chat_emoji_iv)
        val sendButton = findViewById<MaterialCardView>(R.id.btn_send)

        findViewById<MaterialCardView>(R.id.chat_emoji_cv).setOnClickListener {
            if (emojiPopup.isShowing) {
                emojiPng.setImageURI(Uri.parse("android.resource://com.example.huddle/drawable/emoji_icon"))
            } else {
                emojiPng.setImageURI(Uri.parse("android.resource://com.example.huddle/drawable/emoji_icon_filled"))
            }
            emojiPopup.toggle()
        }

        emojiEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    sendButton.visibility = View.GONE
                } else {
                    sendButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        val chatRecyclerView = findViewById<RecyclerView>(R.id.chat_rv)
        val user = Firebase.auth.currentUser?.uid.toString()

        val chatAdapter = ChatAdapter(user)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter
        val chatProgress = findViewById<MaterialCardView>(R.id.chat_progress_cv)

        val chats = Firebase.firestore.collection("Chats")
        val chatList = mutableListOf<Chat>()

        chats.orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshots != null) {
                chatList.clear()
                for (document in snapshots) {
                    val message = document.toObject(Chat::class.java)
                    if((message.senderId == user && message.receiverId == id) || (message.senderId == id && message.receiverId == user)) chatList.add(message)
                }
            }
            chatAdapter.submitList(chatList)
            chatAdapter.notifyDataSetChanged()
            chatProgress.visibility = View.GONE
        }

        sendButton.setOnClickListener {
            val messageText = emojiEditText.text.toString()
            if (messageText.isNotBlank()) {
                val userDocument = chats.document()

                val projectMap = hashMapOf(
                    "message" to messageText,
                    "time" to System.currentTimeMillis(),
                    "root" to userDocument.id,
                    "senderId" to user,
                    "receiverId" to id,
                )

                userDocument.set(projectMap)
                emojiEditText.text?.clear()
            }
        }
    }
}