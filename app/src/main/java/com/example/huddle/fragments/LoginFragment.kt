@file:Suppress("DEPRECATION")

package com.example.huddle.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.huddle.R
import com.example.huddle.activities.BaseHomeActivity
import com.example.huddle.activities.ForgotPasswordActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.database

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setView(R.layout.dialog_progress)
            .setCancelable(false).create()

        progressDialog = dialogBuilder.create()

        val email_edt = view.findViewById<TextInputEditText>(R.id.sign_in_email_edt)
        val password_edt = view.findViewById<TextInputEditText>(R.id.sign_in_password_edt)

        view.findViewById<MaterialButton>(R.id.sign_in_btn)?.setOnClickListener {
            if (email_edt.text?.isEmpty() == true || password_edt.text?.isEmpty() == true) {
                Toast.makeText(
                    context,
                    "Something went wrong. Please check your details",
                    Toast.LENGTH_LONG).show()
            } else {
                progressDialog.show()

                auth.signInWithEmailAndPassword(email_edt.text.toString(), password_edt.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(activity, BaseHomeActivity::class.java))
                        activity?.finish()
                        progressDialog.dismiss()
                    } else {
                        Toast.makeText(
                            activity?.baseContext,
                            task.exception.toString(),
                            Toast.LENGTH_SHORT,
                        ).show()
                        progressDialog.dismiss()
                    }
                }
            }
        }

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    Log.w("GoogleSignIn", "Google sign in failed", e)
                }
            } else {
                Log.w("GoogleSignIn", "Sign in canceled or failed")
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = activity?.baseContext?.let { GoogleSignIn.getClient(it, gso) }!!

        view.findViewById<MaterialCardView>(R.id.login_google_card).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        view.findViewById<MaterialCardView>(R.id.login_github_card).setOnClickListener {
            githubSignIn()
        }

        view.findViewById<TextView>(R.id.forgot_password_tv)
            ?.setOnClickListener {
//                if (email_edt.text?.isEmpty() == true) {
//                    Toast.makeText(view.context, "Enter your email", Toast.LENGTH_SHORT)
//                        .show()
//                } else {
//                    auth.sendPasswordResetEmail(email_edt.text.toString())
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                val resetPassDialog = MaterialAlertDialogBuilder(view.context)
//                                    .setTitle("Reset Password")
//                                    .setMessage("An email has been sent to the entered email address with a password reset link. You can create a new password and login again.")
//                                    .setPositiveButton("OK") { dialog, _ ->
//                                        dialog.dismiss()
//                                    }
//                                    .create()
//
//                                resetPassDialog.show()
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    task.exception.toString() + "",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                }
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

    private fun githubSignIn() {
        progressDialog.show()
        val provider = OAuthProvider.newBuilder("github.com")

        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    val database = Firebase.database
                    val myRef = database.getReference("user").child(user?.uid.toString())

                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = user?.uid.toString()
                    hashMap["email"] = user?.email.toString()
                    hashMap["name"] = user?.displayName.toString()
                    hashMap["profile"] = user?.photoUrl.toString()

                    myRef.setValue(hashMap)
                        .addOnCompleteListener({ task1 ->
                            if (task1.isSuccessful) {
                                startActivity(Intent(activity, BaseHomeActivity::class.java))
                                activity?.finish()
                                progressDialog.dismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Sign up unsuccessful. " + task1.exception + "",
                                    Toast.LENGTH_LONG
                                ).show()
                                user?.delete()
                                progressDialog.dismiss()
                            }
                        })
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
        } else {
            auth.startActivityForSignInWithProvider(requireActivity(), provider.build())
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    val database = Firebase.database
                    val myRef = database.getReference("user").child(user?.uid.toString())

                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = user?.uid.toString()
                    hashMap["email"] = user?.email.toString()
                    hashMap["name"] = user?.displayName.toString()
                    hashMap["profile"] = user?.photoUrl.toString()

                    myRef.setValue(hashMap)
                        .addOnCompleteListener({ task1 ->
                            if (task1.isSuccessful) {
                                startActivity(Intent(activity, BaseHomeActivity::class.java))
                                activity?.finish()
                                progressDialog.dismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Sign up unsuccessful. " + task1.exception + "",
                                    Toast.LENGTH_LONG
                                ).show()
                                user?.delete()
                                progressDialog.dismiss()
                            }
                        })
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        progressDialog.show()
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val database = Firebase.database
                    val myRef = database.getReference("user").child(user?.uid.toString())

                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = user?.uid.toString()
                    hashMap["email"] = user?.email.toString()
                    hashMap["name"] = user?.displayName.toString()
                    hashMap["profile"] = user?.photoUrl.toString()

                    myRef.setValue(hashMap)
                        .addOnCompleteListener({ task1 ->
                            if (task1.isSuccessful) {
                                startActivity(Intent(activity, BaseHomeActivity::class.java))
                                activity?.finish()
                                progressDialog.dismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Sign up unsuccessful. " + task1.exception + "",
                                    Toast.LENGTH_LONG
                                ).show()
                                user?.delete()
                                progressDialog.dismiss()
                            }
                        })
                } else {
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
    }

}