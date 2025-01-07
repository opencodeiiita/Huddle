package com.example.huddle.data

data class Project(
    val projectId: String = "",
    val projectName: String = "",
    val projectDesc: String = "",
    val color: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val users: List<String> = emptyList(),
    val tasks: List<String> = emptyList(),
    var favourite: MutableList<String> = mutableListOf()
)
