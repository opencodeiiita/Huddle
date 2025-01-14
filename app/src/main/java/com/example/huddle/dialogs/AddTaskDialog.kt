package com.example.huddle.dialogs

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.adapters.MemberAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

class AddTaskDialog : DialogFragment() {
    private lateinit var memberRv: RecyclerView
    private val memberList = mutableListOf<String>()
    private lateinit var memberAdapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_task, container, false)
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.dialog_add_task_back).setOnClickListener {
            dialog?.dismiss()
        }

        val dropdownOptions = mutableListOf<Pair<String, String>>()

        val selectProjectEdt = view.findViewById<MaterialAutoCompleteTextView>(R.id.select_project_edt)
        var selectedProjectId = arguments?.getString("selectedProjectId")
        var listF: MutableList<String> = mutableListOf()

        val addMemberBtn = view.findViewById<MaterialCardView>(R.id.add_member_task)

        val dateEdt = view.findViewById<TextInputEditText>(R.id.date_edt)
        dateEdt.setOnClickListener {
            val dialogs: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()
            dialogs.show(requireActivity().supportFragmentManager, "tag")
            dialogs.addOnPositiveButtonClickListener { selection ->
                @SuppressLint("SimpleDateFormat") val format =
                    SimpleDateFormat("dd/MM/yyyy")
                val calendar = Calendar.getInstance(TimeZone.getDefault())
                calendar.timeInMillis = selection.toString().toLong()
                var myDate = format.format(calendar.time)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val formatter =
                        DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss")
                    myDate = myDate.replace("/", "-") + "T00:00:00"
                    val dateTime = LocalDateTime.parse(myDate, formatter)
                    val formatter1 =
                        DateTimeFormatter.ofPattern("MMMM d, yyyy")
                    dateEdt.setText(dateTime.format(formatter1))
                } else {
                    dateEdt.setText(myDate)
                }
            }
        }

        val startTimeEdt = view.findViewById<TextInputEditText>(R.id.start_time_edt)
        startTimeEdt.setOnClickListener {
            val dialogs = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select Time")
                .build()
            dialogs.show(requireActivity().supportFragmentManager, "tag")
            dialogs.addOnPositiveButtonClickListener {
                val selectedHour = dialogs.hour
                val selectedMinute = dialogs.minute
                val amPm = if (selectedHour >= 12) {
                    "PM"
                } else {
                    "AM"
                }

                val hourIn12HrFormat = if (selectedHour > 12) selectedHour - 12 else if (selectedHour == 0) 12 else selectedHour
                val timeString = String.format("%02d:%02d %s", hourIn12HrFormat, selectedMinute, amPm)
                startTimeEdt.setText(timeString)
            }
        }

        val endTimeEdt = view.findViewById<TextInputEditText>(R.id.end_time_edt)
        endTimeEdt.setOnClickListener {
            val dialogs = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select Time")
                .build()
            dialogs.show(requireActivity().supportFragmentManager, "tag")
            dialogs.addOnPositiveButtonClickListener {
                val selectedHour = dialogs.hour
                val selectedMinute = dialogs.minute
                val amPm = if (selectedHour >= 12) {
                    "PM"
                } else {
                    "AM"
                }

                val hourIn12HrFormat = if (selectedHour > 12) selectedHour - 12 else if (selectedHour == 0) 12 else selectedHour
                val timeString = String.format("%02d:%02d %s", hourIn12HrFormat, selectedMinute, amPm)
                endTimeEdt.setText(timeString)
            }
        }

        FirebaseFirestore.getInstance().collection("Project")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val option = document.getString("projectName")
                    val documentId = document.id
                    if(!dropdownOptions.contains(Pair(option, documentId))) option?.let { dropdownOptions.add(Pair(it, documentId)) }
                }

                if(selectedProjectId != null) {
                    selectProjectEdt.setText(dropdownOptions.find { it.second == selectedProjectId }?.first.toString())
                    Firebase.firestore.collection("Project").document(selectedProjectId!!).get().addOnSuccessListener {
                        listF = it["users"] as MutableList<String>
                    }
                } else {
                    addMemberBtn.isEnabled = false
                    dateEdt.isEnabled = false
                    startTimeEdt.isEnabled = false
                    endTimeEdt.isEnabled = false
                }

                val projectNames = dropdownOptions.map { it.first }

                val adapter = ArrayAdapter(
                    view.context,
                    R.layout.dropdown_item,
                    R.id.textViewDropdownItem,
                    projectNames
                )

                selectProjectEdt.setAdapter(adapter)
            }
            .addOnFailureListener { exception ->
                Log.e("FireStore", "Error fetching data: ", exception)
            }

        selectProjectEdt.setOnItemClickListener { parent, _, position, _ ->
            val selectedOption = parent.getItemAtPosition(position).toString()
            selectedProjectId = dropdownOptions.find { it.first == selectedOption }?.second.toString()
            addMemberBtn.isEnabled = true
            dateEdt.isEnabled = true
            startTimeEdt.isEnabled = true
            endTimeEdt.isEnabled = true
            Firebase.firestore.collection("Project").document(selectedProjectId!!).get().addOnSuccessListener {
                listF = it["users"] as MutableList<String>
            }
        }

        val nameEdt = view.findViewById<TextInputEditText>(R.id.add_task_name)
        memberRv = view.findViewById(R.id.project_member_rv)
        memberRv.isNestedScrollingEnabled = false
        memberRv.layoutManager =
            object : LinearLayoutManager(view.context, HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        memberAdapter = MemberAdapter(memberList)
        memberRv.adapter = memberAdapter

        addMemberBtn.setOnClickListener {
            val dialog = SearchUserDialog.newInstance(ArrayList(memberList))
            val args = Bundle()
            if(listF.isNotEmpty()) args.putStringArrayList("memberList", ArrayList(listF))
            dialog.arguments = args
            dialog.setOnUsersSelectedListener { selectedUserIds ->
                memberList.clear()
                memberList.addAll(selectedUserIds)
                memberAdapter.notifyDataSetChanged()
            }
            dialog.show(parentFragmentManager, "UserSelectionDialog")
        }

        view.findViewById<MaterialButton>(R.id.save_task_btn).setOnClickListener {
            if(!(nameEdt.text.toString().isEmpty() || dateEdt.text.toString().isEmpty()
                        || startTimeEdt.text.toString().isEmpty()
                        || endTimeEdt.text.toString().isEmpty() || memberList.isEmpty() || selectedProjectId == null)) {
                val firestore = FirebaseFirestore.getInstance()
                val userDocument = firestore.collection("Task").document()

                val taskMap = hashMapOf(
                    "taskId" to userDocument.id,
                    "taskName" to nameEdt.text.toString(),
                    "taskDate" to dateEdt.text.toString(),
                    "taskProgress" to 0,
                    "startTime" to startTimeEdt.text.toString(),
                    "endTime" to endTimeEdt.text.toString(),
                    "users" to memberList,
                    "projectId" to selectedProjectId,
                    "status" to 0
                )

                userDocument.set(taskMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firestore.collection("Project").document(selectedProjectId!!).get().addOnSuccessListener {
                                val list = it["tasks"] as MutableList<String>
                                list.add(userDocument.id)
                                firestore.collection("Project").document(selectedProjectId!!).update("tasks", list).addOnSuccessListener {
                                    dialog?.dismiss()
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Upload Failed. ${task.exception}",
                                Toast.LENGTH_LONG
                            ).show()
                            dialog?.dismiss()
                        }
                    }
                dialog?.dismiss()
            } else {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }
}