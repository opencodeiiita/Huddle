package com.example.huddle.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.huddle.R
import com.example.huddle.data.Task
import com.example.huddle.dialogs.AddTaskDialog
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProjectStatusActivity : AppCompatActivity() {
    private var memberList: List<String> = emptyList()
    private var taskList: List<String> = emptyList()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_status)
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

        val progress1 = findViewById<com.google.android.material.progressindicator.CircularProgressIndicator>(R.id.progressCircle)
        val progress2 = findViewById<com.google.android.material.progressindicator.CircularProgressIndicator>(R.id.progressCircle2)
        val progress3 = findViewById<com.google.android.material.progressindicator.CircularProgressIndicator>(R.id.progressCircle3)

        var completed: Int
        var onGoing: Int
        var upcoming: Int

        val id = intent.getStringExtra("id")

        Firebase.firestore.collection("Project").document(id!!).get().addOnSuccessListener {
            memberList = it["users"] as List<String>
            taskList = it["tasks"] as List<String>

            Firebase.firestore.collection("Task")
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        completed = 0
                        onGoing = 0
                        upcoming = 0

                        for (document in snapshots) {
                            val task = document.toObject(Task::class.java)
                            if (taskList.contains(task.taskId)) {
                                when (task.status) {
                                    2 -> completed++
                                    1 -> onGoing++
                                    else -> upcoming++
                                }
                            }
                        }

                        findViewById<TextView>(R.id.completed_tv).text = "$completed Tasks were completed successfully."
                        findViewById<TextView>(R.id.onGoing_tv).text = "$onGoing Tasks are currently in progress."
                        findViewById<TextView>(R.id.upcoming_tv).text = "$upcoming Tasks are scheduled to start."

                        val percentUpcoming = if(completed + onGoing + upcoming != 0) {
                            (upcoming.toDouble() / (completed.toDouble() + onGoing.toDouble() + upcoming.toDouble())*100).toInt()
                        } else 0

                        val percentOnGoing = if(completed + onGoing + upcoming != 0) {
                            percentUpcoming + (onGoing.toDouble() / (completed.toDouble() + onGoing.toDouble() + upcoming.toDouble())*100).toInt()
                        } else 0

                        val percentCompleted = if(completed + onGoing + upcoming != 0) {
                            100 - percentOnGoing
                        } else 0

                        progress1.progress = if (percentCompleted == 0) 0 else 100
                        progress2.progress = percentOnGoing
                        progress3.progress = percentUpcoming

                        findViewById<TextView>(R.id.percentageText).text = "${percentCompleted}%"
                    }
                }

            findViewById<LinearLayout>(R.id.linearLayout4).setOnClickListener {
                val intent = Intent(baseContext, TaskDetailsActivity::class.java)
                intent.putStringArrayListExtra("tasks", ArrayList(taskList))
                intent.putExtra("name", "Completed")
                startActivity(intent)
            }

            findViewById<LinearLayout>(R.id.linearLayout5).setOnClickListener {
                val intent = Intent(baseContext, TaskDetailsActivity::class.java)
                intent.putStringArrayListExtra("tasks", ArrayList(taskList))
                intent.putExtra("name", "In Progress")
                startActivity(intent)
            }

            findViewById<LinearLayout>(R.id.linearLayout6).setOnClickListener {
                val intent = Intent(baseContext, TaskDetailsActivity::class.java)
                intent.putStringArrayListExtra("tasks", ArrayList(taskList))
                intent.putExtra("name", "To Do")
                startActivity(intent)
            }
        }

        findViewById<MaterialCardView>(R.id.project_status_add_btn).setOnClickListener {
            val args = Bundle()
            args.putStringArrayList("memberList", ArrayList(memberList))
            args.putString("selectedProjectId", id)
            val addTaskDialog = AddTaskDialog()
            addTaskDialog.arguments = args
            addTaskDialog.show(supportFragmentManager, "AddTaskDialog")

        }
    }
}