package com.example.huddle.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.huddle.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

@Suppress("DEPRECATION")
class CheckEmailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.fragment_check_email_back)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val otpField1: EditText = view.findViewById(R.id.otpField1)
        val otpField2: EditText = view.findViewById(R.id.otpField2)
        val otpField3: EditText = view.findViewById(R.id.otpField3)
        val otpField4: EditText = view.findViewById(R.id.otpField4)
        val otpField5: EditText = view.findViewById(R.id.otpField5)

        moveToNextField(otpField1, otpField2)
        moveToNextField(otpField2, otpField3)
        moveToNextField(otpField3, otpField4)
        moveToNextField(otpField4, otpField5)

        view.findViewById<MaterialButton>(R.id.verify_code_btn)?.setOnClickListener {
            val fr = fragmentManager?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            //Logic for VERIFY CODE Button
            fr?.replace(R.id.forgot_password_fragment_container, SetPasswordFragment())
                ?.addToBackStack(null)
            fr?.commit()
        }
    }
}

private fun moveToNextField(currentField: EditText, nextField: EditText) {
    currentField.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.length == 1) {
                nextField.requestFocus()
            } else if (s.isNullOrEmpty() && start == 0) {
                currentField.focusSearch(View.FOCUS_LEFT)?.requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}