package com.example.huddle.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.activities.CalendarActivity
import com.example.huddle.adapters.ProjectAdapter
import com.example.huddle.adapters.TaskAdapter
import com.example.huddle.data.Project
import com.example.huddle.data.Task
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var taskShimmerLayout: ShimmerFrameLayout
    private lateinit var taskRecyclerView: RecyclerView
    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var projectRecyclerView: RecyclerView
    private lateinit var projectShimmerLayout: ShimmerFrameLayout
    private lateinit var projectAdapter: ProjectAdapter
    private val projectList = mutableListOf<Project>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = LocalDate.now()
        val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val dayOfMonth = currentDate.dayOfMonth
        view.findViewById<TextView>(R.id.home_time_tv).text = "$dayOfWeek, $dayOfMonth"

        val user = Firebase.auth.currentUser?.uid.toString()

        taskShimmerLayout = view.findViewById(R.id.task_shimmer_layout)
        taskShimmerLayout.startShimmer()
        val noResultsTask = view.findViewById<TextView>(R.id.no_results_task)

        view.findViewById<MaterialCardView>(R.id.fragment_home_menu).setOnClickListener {
            startActivity(Intent(view.context, CalendarActivity::class.java))
        }

        projectShimmerLayout = view.findViewById(R.id.project_shimmer_layout)
        projectShimmerLayout.startShimmer()
        val noResultsProject = view.findViewById<TextView>(R.id.no_results_project)

        taskRecyclerView = view.findViewById(R.id.home_task_rv)
        taskRecyclerView.isNestedScrollingEnabled = false
        taskRecyclerView.layoutManager = object : LinearLayoutManager(view.context) {
            override fun canScrollVertically() = false
        }
        taskAdapter = TaskAdapter(taskList, 0)
        taskRecyclerView.adapter = taskAdapter

        projectRecyclerView = view.findViewById(R.id.home_project_rv)
        projectRecyclerView.isNestedScrollingEnabled = false
        projectRecyclerView.layoutManager =
            object : LinearLayoutManager(view.context, HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }
        projectAdapter = ProjectAdapter(projectList)
        projectRecyclerView.adapter = projectAdapter

        val db = FirebaseFirestore.getInstance()

        db.collection("Task")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    taskList.clear()
                    for (document in snapshots) {
                        val task = document.toObject(Task::class.java)
                        if(task.users.contains(user) && task.status == 1) taskList.add(task)
                    }

                    taskShimmerLayout.stopShimmer()
                    taskShimmerLayout.visibility = GONE
                    taskAdapter.notifyDataSetChanged()
                    taskRecyclerView.visibility = View.VISIBLE

                    if (taskList.isEmpty()) {
                        noResultsTask.visibility = View.VISIBLE
                    } else {
                        noResultsTask.visibility = GONE
                    }
                }
            }

        db.collection("Project").limit(8)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    projectList.clear()
                    for (document in snapshots) {
                        val task = document.toObject(Project::class.java)
                        if(task.users.contains(user)) projectList.add(task)
                    }

                    projectShimmerLayout.stopShimmer()
                    projectShimmerLayout.visibility = GONE
                    projectAdapter.notifyDataSetChanged()
                    projectRecyclerView.visibility = View.VISIBLE

                    if (projectList.isEmpty()) {
                        noResultsProject.visibility = View.VISIBLE
                    } else {
                        noResultsProject.visibility = GONE
                    }
                }
            }
    }
}