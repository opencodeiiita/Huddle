package com.example.huddle.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.data.Task

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.task_title)
        val taskCategory: TextView = itemView.findViewById(R.id.task_category)
        val taskTime: TextView = itemView.findViewById(R.id.task_time)
        val progressBar: ProgressBar = itemView.findViewById(R.id.task_progress_bar)
        val progressText: TextView = itemView.findViewById(R.id.task_progress_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.desc
        holder.taskCategory.text = task.name
        holder.taskTime.text = task.taskDate
        holder.progressBar.progress = task.taskProgress
        holder.progressText.text = "${task.taskProgress}%"
    }

    override fun getItemCount(): Int = tasks.size
}
