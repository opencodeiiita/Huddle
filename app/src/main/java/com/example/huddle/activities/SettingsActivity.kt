
package com.example.huddle.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.huddle.R
import com.google.android.material.card.MaterialCardView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<MaterialCardView>(R.id.settings_back_button).setOnClickListener {
            // Finish the current activity to go back to the previous one (ProfileFragment or another activity)
            finish()
        }

        // Add your settings-related logic here
    }
}
