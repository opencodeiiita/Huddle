package com.example.huddle.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
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
    private var previousItemId: Int = R.id.navigation_item1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_home)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val addButton: MaterialCardView = findViewById(R.id.AddButton)

        currentFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, currentFragment!!, "HOME")
            .commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == previousItemId) return@setOnItemSelectedListener false

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
