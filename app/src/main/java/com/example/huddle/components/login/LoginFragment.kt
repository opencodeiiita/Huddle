package com.example.huddle.components.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.huddle.MainActivity
import com.example.huddle.R
import com.example.huddle.activities.BaseHomeActivity
import com.example.huddle.components.forgotPassword.ForgotPasswordActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.fragment_login_back)
            ?.setOnClickListener {
                activity?.finish()
            }

        view.findViewById<MaterialButton>(R.id.sign_in_btn)?.setOnClickListener {
            // Logic for SIGN IN Button
            val context = requireContext()
            val intent = Intent(context, BaseHomeActivity::class.java)
            activity?.startActivity(intent)
        }


        view.findViewById<TextView>(R.id.forgot_password_tv)
            ?.setOnClickListener {
                activity?.startActivity(Intent(requireContext(), ForgotPasswordActivity::class.java))
            }

        view.findViewById<TextView>(R.id.to_sign_up_btn)
            ?.setOnClickListener {
                fragmentManager?.beginTransaction()
                    ?.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    ?.replace(R.id.login_fragment_container, SignUpFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }
    }
}