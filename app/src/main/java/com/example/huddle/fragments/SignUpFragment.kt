package com.example.huddle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.huddle.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class SignUpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.fragment_signup_back)
            ?.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

        view.findViewById<MaterialButton>(R.id.sign_up_btn)?.setOnClickListener {
            //Logic for SIGN UP Button
        }

        view.findViewById<TextView>(R.id.to_sign_in_btn)
            ?.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
    }
}