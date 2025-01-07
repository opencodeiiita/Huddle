package com.example.huddle.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.adapters.ProjectAdapter
import com.example.huddle.adapters.TaskAdapter
import com.example.huddle.data.Task
import com.example.huddle.dialogs.AddTaskDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var taskShimmerLayout: ShimmerFrameLayout
    private lateinit var taskRecyclerView: RecyclerView
    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("name")

        taskShimmerLayout = findViewById(R.id.task_shimmer_layout)
        taskShimmerLayout.startShimmer()
        val noResultsTask = findViewById<TextView>(R.id.no_results_task)

        taskRecyclerView = findViewById(R.id.home_task_rv)
        taskRecyclerView.isNestedScrollingEnabled = false
        taskRecyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically() = false
        }

        taskAdapter = when (name) {
            "Completed" -> {
                TaskAdapter(taskList, 3)
            }
            "In Progress" -> {
                TaskAdapter(taskList, 2)
            }
            else -> TaskAdapter(taskList, 1)
        }

        taskRecyclerView.adapter = taskAdapter


        findViewById<MaterialCardView>(R.id.task_details_close).setOnClickListener {
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

        val user = Firebase.auth.currentUser?.uid
        val list = intent.getStringArrayListExtra("tasks")

        Firebase.firestore.collection("Task")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    taskList.clear()
                    for (document in snapshots) {
                        val task = document.toObject(Task::class.java)
                        if(task.users.contains(user) && list?.contains(task.taskId) == true) {
                            if (name.equals("Completed") && task.status == 2) taskList.add(task)
                            else if(name.equals("In Progress") && task.status == 1) taskList.add(task)
                            else if(name.equals("To Do") && task.status == 0) taskList.add(task)

                        }
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        taskShimmerLayout.stopShimmer()
                        taskShimmerLayout.visibility = GONE
                        taskAdapter.notifyDataSetChanged()
                        taskRecyclerView.visibility = View.VISIBLE

                        if (taskList.isEmpty()) {
                            noResultsTask.visibility = View.VISIBLE
                        } else {
                            noResultsTask.visibility = GONE
                        }
                    }, 1000)
                }
            }

        findViewById<TextView>(R.id.task_type_tv).text = name
    }
}