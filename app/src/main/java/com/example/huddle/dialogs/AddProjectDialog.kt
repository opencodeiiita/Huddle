package com.example.huddle.dialogs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.activities.BaseHomeActivity
import com.example.huddle.adapters.MemberAdapter
import com.example.huddle.adapters.ProjectAdapter
import com.example.huddle.adapters.TaskAdapter
import com.example.huddle.data.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
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

class AddProjectDialog : DialogFragment() {
    private lateinit var member_rv: RecyclerView
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
        return inflater.inflate(R.layout.dialog_add_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.dialog_add_project_back).setOnClickListener {
            dialog?.dismiss()
        }

        member_rv = view.findViewById(R.id.project_member_rv)
        member_rv.isNestedScrollingEnabled = false
        member_rv.layoutManager =
            object : LinearLayoutManager(view.context, HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        memberAdapter = MemberAdapter(memberList)
        val user_id = Firebase.auth.currentUser?.uid
        if (user_id != null) {
            memberList.add(user_id)
        }
        member_rv.adapter = memberAdapter

        view.findViewById<MaterialCardView>(R.id.add_member_project).setOnClickListener {
            val dialog = SearchUserDialog.newInstance(ArrayList(memberList))
            dialog.setOnUsersSelectedListener { selectedUserIds ->
                memberList.clear()
                memberList.addAll(selectedUserIds)
                memberAdapter.notifyDataSetChanged()
            }
            dialog.show(parentFragmentManager, "UserSelectionDialog")
        }

        val colors = listOf("#0a0c16","#03A1B6", "#4CAF50", "#9C27B0", "#3580ff", "#FF5722")
        var selectedColor: String? = "#0a0c16"

        val colorViews = listOf(
            view.findViewById<ImageView>(R.id.label_default_iv),
            view.findViewById(R.id.label_cyan_iv),
            view.findViewById(R.id.label_green_iv),
            view.findViewById(R.id.label_magenta_iv),
            view.findViewById(R.id.label_accent_iv),
            view.findViewById(R.id.label_orange_iv)
        )

        colorViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                selectedColor = colors[index]
                colorViews.forEach {
                    it.setImageResource(R.color.transparent)
                    it.background = null
                }
                view.background = context?.getDrawable(R.drawable.label_selected_bg)
                view.setImageResource(R.drawable.tick_square)
            }
        }

        val name_edt = view.findViewById<TextInputEditText>(R.id.add_project_name)
        val desc_edt = view.findViewById<TextInputEditText>(R.id.add_project_desc)

        val start_time_edt = view.findViewById<TextInputEditText>(R.id.start_time_edt)
        start_time_edt.setOnClickListener {
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
                start_time_edt.setText(timeString)
            }
        }

        val end_time_edt = view.findViewById<TextInputEditText>(R.id.end_time_edt)
        end_time_edt.setOnClickListener {
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
                end_time_edt.setText(timeString)
            }
        }

        val start_date_edt = view.findViewById<TextInputEditText>(R.id.start_date_edt)
        start_date_edt.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

            val dialogs: MaterialDatePicker<*> = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraintsBuilder.build()).setTitleText("Select Date").build()

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
                    start_date_edt.setText(dateTime.format(formatter1))
                } else {
                    start_date_edt.setText(myDate)
                }
            }
        }

        val end_date_edt = view.findViewById<TextInputEditText>(R.id.end_date_edt)
        end_date_edt.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
            val dialogs: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraintsBuilder.build()).setTitleText("Select Date").build()
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
                    end_date_edt.setText(dateTime.format(formatter1))
                } else {
                    end_date_edt.setText(myDate)
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.save_project_btn).setOnClickListener {
            if(!(name_edt.text.toString().isEmpty() || desc_edt.text.toString().isEmpty() || start_date_edt.text.toString().isEmpty()
                || end_date_edt.text.toString().isEmpty() || start_time_edt.text.toString().isEmpty()
                || end_time_edt.text.toString().isEmpty() )) {
                val firestore = FirebaseFirestore.getInstance()
                val userDocument = firestore.collection("Project").document()

                val projectMap = hashMapOf(
                    "projectDesc" to desc_edt.text.toString(),
                    "projectName" to name_edt.text.toString(),
                    "startDate" to start_date_edt.text.toString(),
                    "endDate" to end_date_edt.text.toString(),
                    "startTime" to start_time_edt.text.toString(),
                    "endTime" to end_time_edt.text.toString(),
                    "projectProgress" to 0,
                    "totalTask" to 0,
                    "color" to selectedColor,
                    "users" to memberList
                )

                userDocument.set(projectMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dialog?.dismiss()
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