package com.example.huddle.fragments


import android.widget.RelativeLayout
import com.example.huddle.activities.SettingsActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
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
import com.google.firebase.firestore.FirebaseFirestore

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
        val userDocument = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user?.uid.toString())

        val profilePic = view.findViewById<ImageView>(R.id.profile_pic)

        profileNameTv.text = "Loading..."
        profileEmailTv.text = "Loading..."

        userDocument.addSnapshotListener { documentSnapshot, error ->
            if (error != null) {
                profileNameTv.text = "Error Loading Data"
                profileEmailTv.text = "Error Loading Data"
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name")
                val email = documentSnapshot.getString("email")
                val profileUrl = documentSnapshot.getString("profile")

                profileNameTv.text = name ?: "N/A"
                profileEmailTv.text = email ?: "N/A"

                if (!profileUrl.isNullOrEmpty() && profileUrl != "null") {
                    try {
                        Glide.with(requireContext())
                            .load(profileUrl)
                            .into(profilePic)
                    } catch(e: Exception) {
                        Log.e("ProfileFragment", "Error loading profile picture: ${e.message}")
                    }
                }
            } else {
                profileNameTv.text = "No Data Found"
                profileEmailTv.text = "No Data Found"
            }
        }


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

            val navSp = activity?.getSharedPreferences("navigation", MODE_PRIVATE)
            val editor = navSp?.edit()
            editor?.putString("nav_item", "Home")
            editor?.apply()

            activity?.finish()
            startActivity(Intent(context, LoginActivity::class.java))
        }

        view.findViewById<RelativeLayout>(R.id.settings_layout).setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }


    }
}
