package com.example.huddle.dialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.adapters.UserAdapter
import com.example.huddle.data.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchUserDialog : DialogFragment() {
    private lateinit var user_rv: RecyclerView
    private val userList = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter
    private var selectedUserIds = mutableSetOf<String>()
    private var onUsersSelected: ((List<String>) -> Unit)? = null
    private val searchResults = mutableListOf<String>()

    companion object {
        private const val ARG_STRING_LIST = "string_list"

        fun newInstance(stringList: ArrayList<String>): SearchUserDialog {
            val fragment = SearchUserDialog()
            val args = Bundle()
            args.putStringArrayList(ARG_STRING_LIST, stringList)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_search_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.dialog_search_user_close).setOnClickListener {
            dialog?.dismiss()
        }

        selectedUserIds = arguments?.getStringArrayList(ARG_STRING_LIST)?.toMutableSet() ?: mutableSetOf()

        user_rv = view.findViewById(R.id.user_rv)
        user_rv.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(userList, selectedUserIds)
        user_rv.adapter = userAdapter

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
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    db.collection("users")
                        .orderBy("name", Query.Direction.ASCENDING)
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

        view.findViewById<MaterialButton>(R.id.search_member_done).setOnClickListener {
            onUsersSelected?.invoke(selectedUserIds.toList())
            dialog?.dismiss()
        }
    }

    fun setOnUsersSelectedListener(listener: (List<String>) -> Unit) {
        onUsersSelected = listener
    }
}