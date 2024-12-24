package com.example.huddle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.data.Project
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

class ProjectAdapter(private val projectList: List<Project>) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val project_name_tv = view.findViewById<TextView>(R.id.project_name_tv)
        val project_desc_tv = view.findViewById<TextView>(R.id.project_desc_tv)
        val project_progress_tv = view.findViewById<TextView>(R.id.project_progress_tv)
        val project_progress_pi = view.findViewById<LinearProgressIndicator>(R.id.project_progress_pi)
        val project_card_parent = view.findViewById<MaterialCardView>(R.id.project_card_parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectList[position]
        if (position == 0) {
            val color = ContextCompat.getColor(holder.itemView.context, R.color.accent)
            holder.project_card_parent.setCardBackgroundColor(color)
            val layoutParams = holder.project_card_parent.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = 60.dp.value.toInt()
            holder.project_card_parent.layoutParams = layoutParams
        }
        holder.project_name_tv.text = project.projectName
        holder.project_desc_tv.text = project.projectDesc
        holder.project_progress_tv.text = "${project.projectProgress}/${project.totalTask}"
        val final = (project.projectProgress.toFloat() / project.totalTask.toFloat()) * 100.0
        holder.project_progress_pi.progress = final.toInt()
    }

    override fun getItemCount(): Int = projectList.size
}
