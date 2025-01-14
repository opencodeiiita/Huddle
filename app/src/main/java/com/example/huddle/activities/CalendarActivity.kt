package com.example.huddle.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.huddle.R
import com.example.huddle.data.Task
import com.example.huddle.fragments.CustomCalendarView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {
    @SuppressLint("NewApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialCardView>(R.id.activity_project_status_back).setOnClickListener {
            finish()
        }

        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("isNightMode", false)

        val window = window
        if (isNightMode) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        val calendarView = findViewById<CustomCalendarView>(R.id.calendarView)

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
        val newDateFormat = SimpleDateFormat("MMMM, d", Locale.ENGLISH)

        val user = Firebase.auth.currentUser?.uid.toString()

        val dateDayTv = findViewById<TextView>(R.id.date_day_tv)
        dateDayTv.text = "${calendarView.getFormattedLastSelectedDate()} ✍️"

        var taskCount: Int

        calendarView.onDateSelectedListener = { selectedDate ->
            val selectedDateString = newDateFormat.format(selectedDate)
            dateDayTv.text = "$selectedDateString ✍️"

            if (selectedDateString == newDateFormat.format(currentDate)) {
                val todayCard = findViewById<MaterialCardView>(R.id.today_btn)
                todayCard.setCardBackgroundColor(resources.getColor(R.color.accent))
                todayCard.radius = 100F
            } else {
                val todayCard = findViewById<MaterialCardView>(R.id.today_btn)
                todayCard.setCardBackgroundColor(resources.getColor(R.color.background))
                todayCard.radius = 100F
            }

            Firebase.firestore.collection("Task")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        taskCount = 0
                        for (document in snapshots) {
                            val task = document.toObject(Task::class.java)
                            if(task.users.contains(user) && task.status != 2 && task.taskDate == dateFormat.format(selectedDate)) {
                                taskCount++
                            }
                        }

                        findViewById<TextView>(R.id.task_count_tv).text = "$taskCount tasks due on this day."
                    }
                }
        }

        Firebase.firestore.collection("Task")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    taskCount = 0
                    for (document in snapshots) {
                        val task = document.toObject(Task::class.java)
                        if(task.users.contains(user) && task.status != 2) {
                            if (task.taskDate == dateFormat.format(currentDate)) {
                                taskCount++
                            }
                            findViewById<TextView>(R.id.task_count_tv).text = "$taskCount tasks due today."
                            dateFormat.parse(task.taskDate)
                                ?.let { calendarView.markDate(it) }
                        }
                    }
                }
            }
    }
}