package com.example.huddle.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.adapters.ProjectScreenAdapter
import com.example.huddle.data.Project
import com.example.huddle.dialogs.AddProjectDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Suppress("NAME_SHADOWING")
class ProjectFragment : Fragment() {
    private lateinit var projectRecyclerView: RecyclerView
    private lateinit var projectShimmerLayout: ShimmerFrameLayout
    private lateinit var projectAdapter: ProjectScreenAdapter
    private val projectList = mutableListOf<Project>()
    private var chipViews = listOf<MaterialCardView>()
    private var selectedView = view?.findViewById<MaterialCardView>(R.id.all_cv)
    private lateinit var noResultsTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Shimmer Layout for Project Fragment
        projectShimmerLayout = view.findViewById(R.id.project_shimmer_layout)
        projectShimmerLayout.startShimmer()

        projectRecyclerView = view.findViewById(R.id.project_rv)
        projectRecyclerView.isNestedScrollingEnabled = false
        projectRecyclerView.layoutManager =
            object : LinearLayoutManager(view.context) {
                override fun canScrollVertically() = false
            }
        projectAdapter = ProjectScreenAdapter(projectList)
        projectRecyclerView.adapter = projectAdapter

        view.findViewById<MaterialCardView>(R.id.fragment_project_add).setOnClickListener {
            val addProjectDialog: DialogFragment = AddProjectDialog()
            addProjectDialog.show(parentFragmentManager, "AddProjectDialog")
        }

        val db = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser?.uid.toString()

        chipViews = listOf(
            view.findViewById(R.id.all_cv),
            view.findViewById(R.id.recents_cv),
            view.findViewById(R.id.favourites_cv)
        )
        selectedView = chipViews[0]

        chipViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                updateProjectList()
                selectedView = chipViews[index]
                chipViews.forEach {
                    it.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.transparent))
                }
                view.setStrokeColor(ContextCompat.getColorStateList(requireContext(), R.color.accent))
            }
        }

        updateProjectList()

        noResultsTv = view.findViewById(R.id.no_results_tv)

        view.findViewById<TextInputEditText>(R.id.project_search_edt).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    db.collection("Project")
                        .orderBy("projectName", Query.Direction.ASCENDING)
                        .startAt(query)
                        .endAt(query + "\uf8ff")
                        .addSnapshotListener { snapshots, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }

                            if (snapshots != null) {
                                projectList.clear()
                                for (document in snapshots) {
                                    val projectData = document.toObject(Project::class.java)
                                    if (selectedView == chipViews[0] && projectData.users.contains(user)) projectList.add(projectData)
                                    else if (selectedView == chipViews[2] && projectData.users.contains(user) && projectData.favourite.contains(user)) projectList.add(projectData)
                                }

                                if (projectList.isEmpty()) {
                                    noResultsTv.visibility = View.VISIBLE
                                } else {
                                    noResultsTv.visibility = GONE
                                }

                                projectAdapter.notifyDataSetChanged()
                            }
                        }
                } else {
                    updateProjectList()
                }
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProjectList() {
        val db = FirebaseFirestore.getInstance().collection("Project")
        val user = Firebase.auth.currentUser?.uid.toString()

        db.addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshots != null) {
                projectList.clear()
                for (document in snapshots) {
                    val projectData = document.toObject(Project::class.java)
                    if (selectedView == chipViews[0] && projectData.users.contains(user)) projectList.add(projectData)
                    else if (selectedView == chipViews[2] && projectData.users.contains(user) && projectData.favourite.contains(user)) projectList.add(projectData)
                }

                projectShimmerLayout.stopShimmer()
                projectShimmerLayout.visibility = GONE
                projectRecyclerView.visibility = View.VISIBLE
                projectAdapter.notifyDataSetChanged()

                if (projectList.isEmpty()) {
                    noResultsTv.visibility = View.VISIBLE
                } else {
                    noResultsTv.visibility = GONE
                }
            }
        }
    }
}
