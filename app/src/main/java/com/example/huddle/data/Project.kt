package com.example.huddle.data

data class Project(
    val projectName: String = "",
    val projectDesc: String = "",
    val totalTask: Int = 0,
    val projectProgress: Int = 0,
    val color: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val users: List<String> = emptyList()
)
