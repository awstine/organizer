package com.organizethem.organizethem.domain

data class WeeklyAvailability(
    val monday: DaySchedule = DaySchedule(),
    val tuesday: DaySchedule = DaySchedule(),
    val wednesday: DaySchedule = DaySchedule(),
    val thursday: DaySchedule = DaySchedule(),
    val friday: DaySchedule = DaySchedule(),
    val saturday: DaySchedule = DaySchedule(),
    val sunday: DaySchedule = DaySchedule(),
)

data class DaySchedule(
    val enabled: Boolean = false,
    val startHour: Int = 9,
    val startMinute:Int = 0,
    val endHour: Int = 17,
    val endMinute:Int = 0
)