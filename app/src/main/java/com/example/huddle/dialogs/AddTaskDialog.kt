package com.example.huddle.dialogs

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.huddle.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

class AddTaskDialog : DialogFragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.dialog_add_task_back).setOnClickListener {
            dialog?.dismiss()
        }

        val date_edt = view.findViewById<TextInputEditText>(R.id.date_edt)
        date_edt.setOnClickListener {
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
                    date_edt.setText(dateTime.format(formatter1))
                } else {
                    date_edt.setText(myDate)
                }
            }
        }

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

                val hourIn12HrFormat = if (selectedHour > 12) selectedHour - 12 else selectedHour
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

                val hourIn12HrFormat = if (selectedHour > 12) selectedHour - 12 else selectedHour
                val timeString = String.format("%02d:%02d %s", hourIn12HrFormat, selectedMinute, amPm)
                end_time_edt.setText(timeString)
            }
        }

        view.findViewById<MaterialButton>(R.id.save_task_btn).setOnClickListener {
            //Logic for SAVE Button
            dialog?.dismiss()
        }
    }
}