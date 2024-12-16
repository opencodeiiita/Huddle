package com.example.huddle.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.huddle.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

@Suppress("DEPRECATION")
class ForgotPasswordFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.fragment_forgot_password_back)
            ?.setOnClickListener {
                activity?.finish()
            }

        view.findViewById<MaterialButton>(R.id.send_otp_btn)?.setOnClickListener {
            val fr = fragmentManager?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )

            //Logic for SEND OTP Button
            fr?.replace(R.id.fragment_container, CheckEmailFragment())
                ?.addToBackStack(null)
            fr?.commit()
        }
    }
}