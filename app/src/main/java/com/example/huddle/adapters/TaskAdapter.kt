package com.example.huddle.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.data.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TaskAdapter(private val tasks: List<Task>, private val flag: Int) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.task_title)
        val taskCategory: TextView = itemView.findViewById(R.id.task_category)
        val taskTime: TextView = itemView.findViewById(R.id.task_time)
        val progressBar: ProgressBar = itemView.findViewById(R.id.task_progress_bar)
        val progressText: TextView = itemView.findViewById(R.id.task_progress_tv)
        val checkBox: CheckBox = itemView.findViewById(R.id.check_box_task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.taskName
        Firebase.firestore.collection("Project").document(task.projectId).get().addOnSuccessListener {
            holder.taskCategory.text = it["projectName"].toString()
        }
        holder.taskTime.text = task.taskDate

        holder.progressBar.progress = task.taskProgress
        holder.progressText.text = "${task.taskProgress}%"

        if(flag == 1 || flag == 2) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.setOnClickListener {
                if (flag == 1) {
                    Firebase.firestore.collection("Task").document(task.taskId).update("taskProgress", 50)
                    Firebase.firestore.collection("Task").document(task.taskId).update("status", 1)
                }
                else if (flag == 2) {
                    Firebase.firestore.collection("Task").document(task.taskId).update("taskProgress", 100)
                    Firebase.firestore.collection("Task").document(task.taskId).update("status", 2)
                }
            }
        }
    }

    override fun getItemCount(): Int = tasks.size
}
