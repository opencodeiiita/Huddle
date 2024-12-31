package com.example.huddle.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.activities.ProjectStatusActivity
import com.example.huddle.data.Project
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.math.min

class ProjectScreenAdapter(private val projectList: List<Project>) : RecyclerView.Adapter<ProjectScreenAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val projectNameTv: TextView = view.findViewById(R.id.project_name_tv)
        val projectDescTv: TextView = view.findViewById(R.id.project_desc_tv)
        val projectProgressTv: TextView = view.findViewById(R.id.project_progress_tv)
        val projectProgressPi: LinearProgressIndicator = view.findViewById(R.id.project_progress_pi)
        val projectProgressParent: MaterialCardView = view.findViewById(R.id.project_progress_parent)
        val projectMemberRv: RecyclerView = view.findViewById(R.id.member_project_rv)
        val projectFavIv: ImageView = view.findViewById(R.id.project_fav_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_page_item, parent, false)
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

        val color = Color.parseColor(project.color)

        if (project.color == "#0a0c16") {
            holder.projectProgressParent.strokeColor = ContextCompat.getColor(holder.itemView.context, R.color.text_main)
            holder.projectProgressPi.setIndicatorColor(ContextCompat.getColor(holder.itemView.context, R.color.text_main))
        } else {
            holder.projectProgressParent.strokeColor = color
            holder.projectProgressPi.setIndicatorColor(color)
        }

        holder.projectMemberRv.adapter = memberAdapter
        val memberCount = min(project.users.size, 3)
        memberList.addAll(project.users.subList(0, memberCount))
        memberAdapter.notifyItemRangeInserted(memberList.size - memberCount, memberCount)

        if(project.favourite) {
            holder.projectFavIv.setImageResource(R.drawable.round_star_24)
            holder.projectFavIv.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.circle_yellow))
        } else {
            holder.projectFavIv.setImageResource(R.drawable.round_star_border_24)
            holder.projectFavIv.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.stroke))
        }

        holder.projectFavIv.setOnClickListener {
            if(project.favourite) {
                project.favourite = false
                holder.projectFavIv.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.stroke))
                holder.projectFavIv.setImageResource(R.drawable.round_star_border_24)
                Firebase.firestore.collection("Project").document(project.projectId).update("favourite", false)
            } else {
                project.favourite = true
                holder.projectFavIv.setImageResource(R.drawable.round_star_24)
                holder.projectFavIv.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.circle_yellow))
                Firebase.firestore.collection("Project").document(project.projectId).update("favourite", true)
            }
        }

        if (project.totalTask == 0) {
            holder.projectProgressPi.visibility = View.GONE
            holder.projectProgressTv.text = "No Tasks"
        } else {
            holder.projectProgressTv.text = "${project.projectProgress}/${project.totalTask}"
            val final = (project.projectProgress.toFloat() / project.totalTask.toFloat()) * 100.0
            holder.projectProgressPi.progress = final.toInt()
        }
        holder.projectNameTv.text = project.projectName
        holder.projectDescTv.text = project.projectDesc

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
