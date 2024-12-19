package com.example.huddle.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.huddle.R
import com.example.huddle.fragments.CommunityFragment
import com.example.huddle.fragments.HomeFragment
import com.example.huddle.fragments.ProfileFragment
import com.example.huddle.fragments.ProjectFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseHomeActivity : AppCompatActivity() {

    private val fragment1 = HomeFragment()
    private val fragment2 = ProjectFragment()
    private val fragment3 = CommunityFragment()
    private val fragment4 = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val addButton: ImageButton = findViewById(R.id.AddButton)

        // Load the default fragment on app start
        loadFragment(fragment1)

        // Set up bottom navigation listener (Updated method)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_item1 -> {
                    loadFragment(fragment1)
                    true
                }
                R.id.navigation_item2 -> {
                    loadFragment(fragment2)
                    true
                }
                R.id.navigation_item3 -> {
                    loadFragment(fragment3)
                    true
                }
                R.id.navigation_item4 -> {
                    loadFragment(fragment4)
                    true
                }
                else -> false
            }
        }

        // Show bottom sheet on AddButton click
        addButton.setOnClickListener {
            showBottomSheet()
        }
    }

    // Function to load a fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    // Function to show the bottom sheet
    private fun showBottomSheet() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.bottom_sheet_layout)

        // Close button in bottom sheet
        val closeButton: ImageView = dialog.findViewById(R.id.imageViewClose)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        // Configure dialog properties
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }
}
