package com.example.huddle.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.huddle.R
import com.example.huddle.dialogs.AddProjectDialog
import com.example.huddle.dialogs.AddTaskDialog
import com.example.huddle.dialogs.AddTeamDialog
import com.example.huddle.fragments.CommunityFragment
import com.example.huddle.fragments.HomeFragment
import com.example.huddle.fragments.ProfileFragment
import com.example.huddle.fragments.ProjectFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class BaseHomeActivity : AppCompatActivity() {
    private lateinit var currentFragment: Fragment
    private var previousItemId: Int = 0

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_home)

        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("isNightMode", false)

        val nav_sp = getSharedPreferences("navigation", MODE_PRIVATE)
        val nav_item = nav_sp.getString("nav_item", "Home")

        val window = window
        if (isNightMode) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val addButton: MaterialCardView = findViewById(R.id.AddButton)

        if(nav_item == "Profile") {
            bottomNavigationView.selectedItemId = R.id.navigation_item4
            previousItemId = R.id.navigation_item4
        } else if(nav_item == "Project") {
            bottomNavigationView.selectedItemId = R.id.navigation_item2
            previousItemId = R.id.navigation_item2
        } else if(nav_item == "Community") {
            bottomNavigationView.selectedItemId = R.id.navigation_item3
            previousItemId = R.id.navigation_item3
        } else {
            bottomNavigationView.selectedItemId = R.id.navigation_item1
            previousItemId = R.id.navigation_item1
        }

        currentFragment = if(nav_item == "Profile") ProfileFragment() else if(nav_item == "Project") ProjectFragment() else if(nav_item == "Community") CommunityFragment() else HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, currentFragment, nav_item)
            .commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == previousItemId) return@setOnItemSelectedListener false

            val editor = nav_sp.edit()
            editor.putString("nav_item", item.title.toString())
            editor.apply()

            val newFragment = when (item.itemId) {
                R.id.navigation_item1 -> getOrCreateFragment("HOME", HomeFragment())
                R.id.navigation_item2 -> getOrCreateFragment("PROJECT", ProjectFragment())
                R.id.navigation_item3 -> getOrCreateFragment("COMMUNITY", CommunityFragment())
                R.id.navigation_item4 -> getOrCreateFragment("PROFILE", ProfileFragment())
                else -> currentFragment
            }

            val isForward = item.itemId > previousItemId
            switchFragment(newFragment, isForward)

            previousItemId = item.itemId
            true
        }

        addButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun getOrCreateFragment(tag: String, fragment: Fragment): Fragment {
        val fragmentManager = supportFragmentManager
        return fragmentManager.findFragmentByTag(tag) ?: fragment
    }

    private fun switchFragment(fragment: Fragment, isForward: Boolean) {
        if (currentFragment != fragment) {
            val transaction = supportFragmentManager.beginTransaction()

            transaction.setCustomAnimations(
                if (isForward) R.anim.slide_in_right else R.anim.slide_in_left,
                if (isForward) R.anim.slide_out_left else R.anim.slide_out_right
            )

            currentFragment?.let { transaction.hide(it) }

            if (fragment.isAdded) {
                transaction.show(fragment)
            } else {
                transaction.add(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)
            }

            transaction.commit()
            currentFragment = fragment
        }
    }

    private fun showBottomSheet() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.bottom_sheet_layout)

        val closeButton: MaterialCardView = dialog.findViewById(R.id.imageViewClose)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<RelativeLayout?>(R.id.relativeLayoutCreateTask).setOnClickListener {
            val addTaskDialog: DialogFragment = AddTaskDialog()
            addTaskDialog.show(supportFragmentManager, "AddTaskDialog")
            dialog.dismiss()
        }

        dialog.findViewById<RelativeLayout?>(R.id.relativeLayoutCreateTeam).setOnClickListener {
            val addTaskDialog: DialogFragment = AddTeamDialog()
            addTaskDialog.show(supportFragmentManager, "AddTeamDialog")
            dialog.dismiss()
        }

        dialog.findViewById<RelativeLayout?>(R.id.relativeLayoutCreateProject)?.setOnClickListener {
            val addProjectDialog: DialogFragment = AddProjectDialog()
            addProjectDialog.show(supportFragmentManager, "AddProjectDialog")
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }
}
