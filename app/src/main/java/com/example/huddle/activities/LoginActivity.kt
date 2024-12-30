package com.example.huddle.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.huddle.R
import com.example.huddle.fragments.LoginFragment

class LoginActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        if (!sharedPreferences.contains("isNightMode")) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("isNightMode", true)
            editor.apply()
        }

        val isNightMode = sharedPreferences.getBoolean("isNightMode", true)

        val window = window
        if (isNightMode) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val fragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.login_fragment_container, fragment)
            .commit()
    }
}