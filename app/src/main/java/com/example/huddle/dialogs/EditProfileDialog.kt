package com.example.huddle.dialogs

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.huddle.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class EditProfileDialog : DialogFragment() {
    private lateinit var imageUri: String
    private var trueUri: Uri = Uri.EMPTY

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            view?.findViewById<ImageView>(R.id.edt_profile_picture)?.setImageURI(uri)
            trueUri = uri
            imageUri = uri.toString()
        } else {
            Log.d("PhotoPicker", "No media selected")
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
        return inflater.inflate(R.layout.dialog_edit_profile, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name")
        val phone = arguments?.getString("phone")
        val photo = arguments?.getString("photo")

        imageUri = photo ?: "null"

        val nameTv = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edt_profile_name)
        nameTv.setText(name)
        val phoneTv = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edt_profile_phone)
        phoneTv.setText(phone)

        val user = Firebase.auth.currentUser

        if (!photo.isNullOrEmpty() && photo != "null") {
            try {
                Glide.with(requireContext())
                    .load(photo)
                    .into(view.findViewById(R.id.edt_profile_picture))
            } catch(_: Exception) {}
        }

        view.findViewById<MaterialCardView>(R.id.edit_profile_picture_cv).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        view.findViewById<MaterialCardView>(R.id.dialog_edit_profile_back).setOnClickListener {
            dialog?.dismiss()
        }

        view.findViewById<MaterialButton>(R.id.save_profile_btn).setOnClickListener {
            val updatePath = user?.uid?.let { it1 ->
                Firebase.firestore.collection("users").document(
                    it1
                )
            }

            val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            dialogBuilder.setView(R.layout.dialog_progress)
                .setCancelable(false).create()

            val progressDialog = dialogBuilder.create()
            progressDialog.show()

            if (imageUri != "null") {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(imageUri))
                    .build()
                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            updatePath?.update("profile", user.photoUrl.toString())
                        }
                    }
                if (nameTv.text.toString().isEmpty()) {
                    nameTv.error = "Name cannot be empty"
                    progressDialog.dismiss()
                } else {
                    updatePath?.update("name", nameTv.text.toString())
                    if (phoneTv.text.toString().isNotEmpty()) updatePath?.update(
                        "phone",
                        phoneTv.text.toString()
                    )
                    progressDialog.dismiss()
                    dialog?.dismiss()
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.cancel_profile_btn).setOnClickListener {
            dialog?.dismiss()
        }
    }
}