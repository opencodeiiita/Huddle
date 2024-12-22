package com.example.huddle.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.example.huddle.activities.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ProfileFragment : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileNameTv = view.findViewById<TextView>(R.id.profile_name_tv)
        val profileEmailTv = view.findViewById<TextView>(R.id.profile_email_tv)

        val user = Firebase.auth.currentUser
        val myRef = Firebase.database.reference.child("user").child(user?.uid.toString())

        val profile_pic = view.findViewById<ImageView>(R.id.profile_pic)

        profileNameTv.text = "Loading..."
        profileEmailTv.text = "Loading..."

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view.findViewById<TextView>(R.id.profile_name_tv).text = snapshot.child("name").value.toString()
                    view.findViewById<TextView>(R.id.profile_email_tv).text = snapshot.child("email").value.toString()
                    val profile_url = snapshot.child("profile").value.toString()
                    if (profile_url != "null") {
                        Glide.with(context!!)
                            .load(profile_url)
                            .into(profile_pic)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                view.findViewById<TextView>(R.id.profile_name_tv).text = "Error Loading Data"
                view.findViewById<TextView>(R.id.profile_email_tv).text = "Error Loading Data"
            }
        })

        //Temporary SIGN OUT Button
        view.findViewById<MaterialButton>(R.id.profile_edit_btn).setOnClickListener {
            Firebase.auth.signOut()

            if(Firebase.auth.currentUser?.providerId.equals("google.com")){
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                googleSignInClient =
                    activity?.baseContext?.let { GoogleSignIn.getClient(it, gso) }!!

                googleSignInClient.signOut()
                googleSignInClient.revokeAccess()
            }

            activity?.finish()
            startActivity(Intent(context, LoginActivity::class.java))
        }

    }
}
