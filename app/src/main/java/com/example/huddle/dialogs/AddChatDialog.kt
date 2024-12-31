package com.example.huddle.dialogs

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.adapters.AddChatAdapter
import com.example.huddle.data.User
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AddChatDialog : DialogFragment() {
    private lateinit var userRv: RecyclerView
    private val userList = mutableListOf<User>()
    private lateinit var userAdapter: AddChatAdapter
    private val searchResults = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_chat, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.dialog_search_user_close).setOnClickListener {
            dialog?.dismiss()
        }

        userRv = view.findViewById(R.id.user_rv)
        userRv.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = AddChatAdapter(userList, this)
        userRv.adapter = userAdapter

        val user = Firebase.auth.currentUser

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    userList.clear()
                    for (document in snapshots) {
                        val userData = document.toObject(User::class.java)
                        if (userData.id != user?.uid) userList.add(userData)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }

        view.findViewById<TextInputEditText>(R.id.member_search_edt).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                if (query.isNotEmpty()) {
                    db.collection("users")
                        .orderBy("email", Query.Direction.ASCENDING)
                        .startAt(query)
                        .endAt(query + "\uf8ff")
                        .addSnapshotListener { snapshots, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }

                            if (snapshots != null) {
                                userList.clear()
                                for (document in snapshots) {
                                    val userData = document.toObject(User::class.java)
                                    if (userData.id != user?.uid) userList.add(userData)
                                }
                                userAdapter.notifyDataSetChanged()
                            }
                        }
                } else {
                    searchResults.clear()
                    db.collection("users")
                        .addSnapshotListener { snapshots, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }

                            if (snapshots != null) {
                                userList.clear()
                                for (document in snapshots) {
                                    val userData = document.toObject(User::class.java)
                                    if (userData.id != user?.uid) userList.add(userData)
                                }
                                userAdapter.notifyDataSetChanged()
                            }
                        }
                }
            }
        })
    }
}