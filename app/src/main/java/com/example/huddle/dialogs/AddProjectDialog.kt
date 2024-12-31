package com.example.huddle.dialogs

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.huddle.R
import com.example.huddle.adapters.MemberAdapter
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

@Suppress("NAME_SHADOWING")
class AddProjectDialog : DialogFragment() {
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
        return inflater.inflate(R.layout.dialog_add_project, container, false)
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables", "DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.dialog_add_project_back).setOnClickListener {
            dialog?.dismiss()
        }

        memberRv = view.findViewById(R.id.project_member_rv)
        memberRv.isNestedScrollingEnabled = false
        memberRv.layoutManager =
            object : LinearLayoutManager(view.context, HORIZONTAL, false) {
                override fun canScrollVertically() = false
            }

        memberAdapter = MemberAdapter(memberList)
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            memberList.add(userId)
        }
        memberRv.adapter = memberAdapter

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

        val nameEdt = view.findViewById<TextInputEditText>(R.id.add_project_name)
        val descEdt = view.findViewById<TextInputEditText>(R.id.add_project_desc)

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

        val startDateEdt = view.findViewById<TextInputEditText>(R.id.start_date_edt)
        startDateEdt.setOnClickListener {
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
                    startDateEdt.setText(dateTime.format(formatter1))
                } else {
                    startDateEdt.setText(myDate)
                }
            }
        }

        val endDateEdt = view.findViewById<TextInputEditText>(R.id.end_date_edt)
        endDateEdt.setOnClickListener {
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
                    endDateEdt.setText(dateTime.format(formatter1))
                } else {
                    endDateEdt.setText(myDate)
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.save_project_btn).setOnClickListener {
            if(!(nameEdt.text.toString().isEmpty() || descEdt.text.toString().isEmpty() || startDateEdt.text.toString().isEmpty()
                || endDateEdt.text.toString().isEmpty() || startTimeEdt.text.toString().isEmpty()
                || endTimeEdt.text.toString().isEmpty() )) {
                val firestore = FirebaseFirestore.getInstance()
                val userDocument = firestore.collection("Project").document()

                val projectMap = hashMapOf(
                    "projectId" to userDocument.id,
                    "projectDesc" to descEdt.text.toString(),
                    "projectName" to nameEdt.text.toString(),
                    "startDate" to startDateEdt.text.toString(),
                    "endDate" to endDateEdt.text.toString(),
                    "startTime" to startTimeEdt.text.toString(),
                    "endTime" to endTimeEdt.text.toString(),
                    "projectProgress" to 0,
                    "totalTask" to 0,
                    "color" to selectedColor,
                    "users" to memberList,
                    "taskDetails" to mapOf(
                        "completed" to 0,
                        "onGoing" to 0,
                        "upcoming" to 0
                    ),
                    "favourite" to false
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