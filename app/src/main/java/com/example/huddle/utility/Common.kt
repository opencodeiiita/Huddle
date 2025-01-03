package com.example.huddle.utility

import java.text.SimpleDateFormat
import java.util.Locale

fun getTimeAgo(timeInMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timeInMillis

    return when {
        diff < 60 * 1000 -> "Active Recently"
        diff < 60 * 60 * 1000 -> "Last seen ${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "Last seen ${diff / (60 * 60 * 1000)}h ago"
        diff < 7 * 24 * 60 * 60 * 1000 -> "Last seen ${diff / (24 * 60 * 60 * 1000)}d ago"
        else -> {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            "Last seen on ${dateFormat.format(timeInMillis)}"
        }
    }
}