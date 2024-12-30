package com.example.huddle.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.activities.ProjectStatusActivity
import com.example.huddle.data.Project
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.math.min

class ProjectScreenAdapter(private val projectList: List<Project>) : RecyclerView.Adapter<ProjectScreenAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val project_name_tv = view.findViewById<TextView>(R.id.project_name_tv)
        val project_desc_tv = view.findViewById<TextView>(R.id.project_desc_tv)
        val project_progress_tv = view.findViewById<TextView>(R.id.project_progress_tv)
        val project_progress_pi = view.findViewById<LinearProgressIndicator>(R.id.project_progress_pi)
        val project_member_rv = view.findViewById<RecyclerView>(R.id.member_project_rv)
        val project_progress_parent = view.findViewById<MaterialCardView>(R.id.project_progress_parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_page_item, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectList[position]

        holder.project_member_rv.isNestedScrollingEnabled = false
        holder.project_member_rv.layoutManager =
            object : LinearLayoutManager(holder.itemView.context, HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        val memberList = mutableListOf<String>()
        val memberAdapter = ProjectMemberAdapter(memberList)

        val color = Color.parseColor(project.color)

        if (project.color == "#0a0c16") {
            holder.project_progress_parent.strokeColor = ContextCompat.getColor(holder.itemView.context, R.color.text_main)
            holder.project_progress_pi.setIndicatorColor(ContextCompat.getColor(holder.itemView.context, R.color.text_main))
        } else {
            holder.project_progress_parent.strokeColor = color
            holder.project_progress_pi.setIndicatorColor(color)
        }

        holder.project_member_rv.adapter = memberAdapter
        val memberCount = min(project.users.size, 3)
        memberList.addAll(project.users.subList(0, memberCount))
        memberAdapter.notifyItemRangeInserted(memberList.size - memberCount, memberCount)

        if (project.totalTask == 0) {
            holder.project_progress_pi.visibility = View.GONE
            holder.project_progress_tv.text = "No Tasks"
        } else {
            holder.project_progress_tv.text = "${project.projectProgress}/${project.totalTask}"
            val final = (project.projectProgress.toFloat() / project.totalTask.toFloat()) * 100.0
            holder.project_progress_pi.progress = final.toInt()
        }
        holder.project_name_tv.text = project.projectName
        holder.project_desc_tv.text = project.projectDesc

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProjectStatusActivity::class.java)
            intent.putExtra("completed", project.taskDetails["completed"])
            intent.putExtra("upcoming", project.taskDetails["upcoming"])
            intent.putExtra("onGoing", project.taskDetails["onGoing"])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = projectList.size
}
