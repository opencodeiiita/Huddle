package com.example.huddle.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.huddle.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Get references to views
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvUserEmail: TextView = view.findViewById(R.id.tvUserEmail)
        val btnEditProfile: Button = view.findViewById(R.id.btnEditProfile)

        // Set dummy data (you can replace this with data fetched from your database)
        tvUserName.text = "Albert Einstein"
        tvUserEmail.text = "albert.einstein@example.com"

        // Handle Edit button click
        btnEditProfile.setOnClickListener {
            // Implement edit functionality here
            // For example, navigate to an EditProfileFragment
        }

        return view
    }
}
