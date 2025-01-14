package com.example.huddle.fragments

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.huddle.R
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var calendar = Calendar.getInstance()
    private var markedDates = HashSet<String>()
    private var selectedDates = HashSet<String>()
    private lateinit var monthYearText: TextView
    private lateinit var daysGrid: GridView
    private var calendarAdapter: CalendarAdapter? = null

    var onDateSelectedListener: ((Date) -> Unit)? = null

    private fun notifyDateSelected(date: Date) {
        onDateSelectedListener?.invoke(date)
    }


    init {
        orientation = VERTICAL
        setupView()
    }

    private fun setupView() {
        LayoutInflater.from(context).inflate(R.layout.calendar_view, this, true)

        monthYearText = findViewById(R.id.monthYearTV)
        daysGrid = findViewById(R.id.daysGridView)

        findViewById<View>(R.id.previousButton).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }
        findViewById<View>(R.id.nextButton).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        updateCalendar()
    }

    private fun dateToString(date: Date): String {
        val cal = Calendar.getInstance().apply { time = date }
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
    }

    fun getSelectedDates(): List<Date> {
        return selectedDates.map { dateString ->
            val parts = dateString.split("-")
            Calendar.getInstance().apply {
                set(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            }.time
        }
    }

    fun getLastSelectedDate(): Date? {
        return getSelectedDates().lastOrNull()
    }

    fun getFormattedLastSelectedDate(): String? {
        return getLastSelectedDate()?.let { formatDate(it) }
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMMM, d", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun markDate(date: Date) {
        markedDates.add(dateToString(date))
        updateCalendar()
    }

    private fun updateCalendar() {
        val cells = ArrayList<Date>()
        val monthCalendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val monthBeginningCell = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1

        monthCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        val date = Calendar.getInstance().time
        selectedDates.add(dateToString(date))

        while (cells.size < DAYS_COUNT) {
            cells.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val dateFormat = android.text.format.DateFormat.format("MMMM yyyy", calendar)
        monthYearText.text = dateFormat.toString()

        calendarAdapter = CalendarAdapter(
            context,
            cells,
            markedDates,
            selectedDates,
            ::dateToString
        ) { date ->
            notifyDateSelected(date)
        }
        daysGrid.adapter = calendarAdapter
    }

    companion object {
        private const val DAYS_COUNT = 42
    }
}

class CalendarAdapter(
    private val context: Context,
    private val dates: ArrayList<Date>,
    private val markedDates: Set<String>,
    private val selectedDates: MutableSet<String>,
    private val dateToString: (Date) -> String,
    private val onDateSelected: (Date) -> Unit
) : android.widget.BaseAdapter() {

    override fun getCount(): Int = dates.size

    override fun getItem(position: Int): Any = dates[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val date = dates[position]
        val day = android.text.format.DateFormat.format("d", date) as String

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.calendar_day_layout, parent, false)

        val dayView = view.findViewById<TextView>(R.id.dayText)
        val markerView = view.findViewById<MaterialCardView>(R.id.date_marker)

        dayView.text = day

        markerView.strokeWidth = if (markedDates.contains(dateToString(date))) 3 else 0
        markerView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        dayView.setTextColor(ContextCompat.getColor(context, R.color.text_main))

        if (selectedDates.contains(dateToString(date))) {
            markerView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent))
            dayView.setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        view.setOnClickListener {
            selectedDates.clear()
            selectedDates.add(dateToString(date))
            notifyDataSetChanged()
            onDateSelected(date)
        }

        return view
    }

}