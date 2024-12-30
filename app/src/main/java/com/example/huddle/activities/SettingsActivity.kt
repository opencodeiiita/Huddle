
package com.example.huddle.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.huddle.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch

class SettingsActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("isNightMode", true)

        val window = window
        if (isNightMode) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        findViewById<MaterialCardView>(R.id.fragment_settings_back).setOnClickListener {
            finish()
        }

        val themeToggle = findViewById<MaterialSwitch>(R.id.theme_switch)

        themeToggle.isChecked = isNightMode

        themeToggle.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("isNightMode", isChecked)
            editor.apply()

            setThemeMode(isChecked)
        }
    }

    private fun setThemeMode(isNightMode: Boolean) {
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}