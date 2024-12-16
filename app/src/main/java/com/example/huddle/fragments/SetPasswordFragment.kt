package com.example.huddle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.huddle.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class SetPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.fragment_set_password_back)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<MaterialButton>(R.id.update_pass_btn)?.setOnClickListener {
            //Logic for UPDATE PASSWORD Button
        }
    }
}