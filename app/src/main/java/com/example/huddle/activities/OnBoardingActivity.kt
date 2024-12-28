package com.example.huddle.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.huddle.R
import com.example.huddle.adapters.OnBoardingAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnSkip: TextView
    private lateinit var btnNext: ImageView

    private val layouts = listOf(
        R.layout.on_board_screen1,
        R.layout.on_board_screen2,
        R.layout.on_board_screen3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)

        viewPager.adapter = OnBoardingAdapter(layouts)

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        btnSkip.setOnClickListener { finishOnboarding() }
        btnNext.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem + 1 < layouts.size) {
                viewPager.currentItem = currentItem + 1
            } else {
                finishOnboarding()
            }
        }
    }

    private fun finishOnboarding() {
        startActivity(Intent(this, BaseHomeActivity::class.java))
        finish()
    }
}