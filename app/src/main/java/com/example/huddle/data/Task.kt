package com.example.huddle.data

data class Task(
    val projectId: String = "",
    val taskId: String = "",
    val status: Int = 0,
    val startTime: String = "",
    val endTime: String = "",
    val taskName: String = "",
    val taskDate: String = "",
    val taskProgress: Int = 0,
    val users: List<String> = emptyList()
)