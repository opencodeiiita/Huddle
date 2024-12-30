package com.example.huddle.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.data.Project
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.math.min

class ProjectAdapter(private val projectList: List<Project>) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val project_name_tv = view.findViewById<TextView>(R.id.project_name_tv)
        val project_desc_tv = view.findViewById<TextView>(R.id.project_desc_tv)
        val project_progress_tv = view.findViewById<TextView>(R.id.project_progress_tv)
        val project_progress_pi = view.findViewById<LinearProgressIndicator>(R.id.project_progress_pi)
        val project_card_parent = view.findViewById<MaterialCardView>(R.id.project_card_parent)
        val project_progress_parent = view.findViewById<TextView>(R.id.progress_tv)
        val project_member_rv = view.findViewById<RecyclerView>(R.id.member_project_rv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
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

        holder.project_member_rv.adapter = memberAdapter
        val memberCount = min(project.users.size, 3)
        memberList.addAll(project.users.subList(0, memberCount))
        memberAdapter.notifyItemRangeInserted(memberList.size - memberCount, memberCount)

        if (position == 0) {
            val layoutParams = holder.project_card_parent.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = 60.dp.value.toInt()
            holder.project_card_parent.layoutParams = layoutParams
        }

        val color = Color.parseColor(project.color)
        if(project.color == "#0a0c16") holder.project_card_parent.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.background))
        else holder.project_card_parent.setCardBackgroundColor(color)

        if (project.totalTask == 0) {
            holder.project_progress_pi.visibility = View.GONE
            holder.project_progress_tv.text = "No Tasks"
            holder.project_progress_parent.text = "Thisis"
            holder.project_progress_parent.visibility = View.INVISIBLE
        } else {
            holder.project_progress_tv.text = "${project.projectProgress}/${project.totalTask}"
            val final = (project.projectProgress.toFloat() / project.totalTask.toFloat()) * 100.0
            holder.project_progress_pi.progress = final.toInt()
        }
        holder.project_name_tv.text = project.projectName
        holder.project_desc_tv.text = project.projectDesc
    }

    override fun getItemCount(): Int = projectList.size
}
