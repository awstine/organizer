package com.organizethem.organizethem.util

import com.organizethem.organizethem.domain.DaySchedule

object TimeSlotGenerator {
    fun generateSlots(
        schedule: DaySchedule,
        duration: Int,
        bookedSlots: List<String>
    ): List<String> {
        if (!schedule.enabled) return emptyList()

        val slots = mutableListOf<String>()
        var currentHour = schedule.startHour
        var currentMinute = schedule.startMinute

        val endTotalMinutes = schedule.endHour * 60 + schedule.endMinute

        while (currentHour * 60 + currentMinute + duration <= endTotalMinutes) {
            val timeString = String.format("%02d:%02d", currentHour, currentMinute)

            // Only add if not already booked
            if (timeString !in bookedSlots) {
                slots.add(timeString)
            }

            // Move to next slot (current time + duration)
            currentMinute += duration
            if (currentMinute >= 60) {
                currentHour += currentMinute / 60
                currentMinute = currentMinute % 60
            }
        }

        return slots
    }
}