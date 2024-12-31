package com.example.huddle.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.activities.ProjectStatusActivity
import com.example.huddle.data.Project
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlin.math.min

class ProjectAdapter(private val projectList: List<Project>) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val projectNameTv: TextView = view.findViewById(R.id.project_name_tv)
        val projectDescTv: TextView = view.findViewById(R.id.project_desc_tv)
        val projectProgressTv: TextView = view.findViewById(R.id.project_progress_tv)
        val projectProgressPi: LinearProgressIndicator = view.findViewById(R.id.project_progress_pi)
        val projectCardParent: MaterialCardView = view.findViewById(R.id.project_card_parent)
        val projectProgressParent: TextView = view.findViewById(R.id.progress_tv)
        val projectMemberRv: RecyclerView = view.findViewById(R.id.member_project_rv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectList[position]

        holder.projectMemberRv.isNestedScrollingEnabled = false
        holder.projectMemberRv.layoutManager =
            object : LinearLayoutManager(holder.itemView.context, HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        val memberList = mutableListOf<String>()
        val memberAdapter = ProjectMemberAdapter(memberList)

        holder.projectMemberRv.adapter = memberAdapter
        val memberCount = min(project.users.size, 3)
        memberList.addAll(project.users.subList(0, memberCount))
        memberAdapter.notifyItemRangeInserted(memberList.size - memberCount, memberCount)

        if (position == 0) {
            val layoutParams = holder.projectCardParent.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = 60.dp.value.toInt()
            holder.projectCardParent.layoutParams = layoutParams
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProjectStatusActivity::class.java)
            intent.putExtra("completed", project.taskDetails["completed"])
            intent.putExtra("upcoming", project.taskDetails["upcoming"])
            intent.putExtra("onGoing", project.taskDetails["onGoing"])
            holder.itemView.context.startActivity(intent)
        }

        val color = Color.parseColor(project.color)
        if(project.color == "#0a0c16") {
            holder.projectCardParent.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.background
                )
            )
        } else {
            holder.projectCardParent.setCardBackgroundColor(color)
            holder.projectNameTv.setTextColor(Color.WHITE)
            holder.projectDescTv.setTextColor(Color.WHITE)
            holder.projectProgressTv.setTextColor(Color.WHITE)
            holder.projectProgressParent.setTextColor(Color.WHITE)
            holder.projectProgressPi.setIndicatorColor(Color.WHITE)
        }

        if (project.totalTask == 0) {
            holder.projectProgressPi.visibility = View.GONE
            holder.projectProgressTv.text = "No Tasks"
            holder.projectProgressParent.text = "Coasts"
            holder.projectProgressParent.visibility = View.INVISIBLE
        } else {
            holder.projectProgressTv.text = "${project.projectProgress}/${project.totalTask}"
            val final = (project.projectProgress.toFloat() / project.totalTask.toFloat()) * 100.0
            holder.projectProgressPi.progress = final.toInt()
        }
        holder.projectNameTv.text = project.projectName
        holder.projectDescTv.text = project.projectDesc
    }

    override fun getItemCount(): Int = projectList.size
}
