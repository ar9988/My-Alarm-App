package com.example.myalarmapp.data.model.alarm

import java.time.DayOfWeek
import java.time.LocalTime

data class Alarm(
    val id: Long = 0,
    val time: LocalTime,
    val isEnabled: Boolean = true,
    val days: Set<DayOfWeek> = emptySet(),
    val label: String? = null,
    val ringtone: String? = null,
    val vibrate: Boolean = true
)