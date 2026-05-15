package com.organizethem.organizethem.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.organizethem.organizethem.domain.DaySchedule
import com.organizethem.organizethem.domain.WeeklyAvailability
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//Saving the availability in firestore
class AvailabilityCollection @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveAvailability(userId: String, availability: WeeklyAvailability) {
        val batch = firestore.batch()

        listOf(
            "monday" to availability.monday,
            "tuesday" to availability.tuesday,
            "wednesday" to availability.wednesday,
            "thursday" to availability.thursday,
            "friday" to availability.friday,
            "saturday" to availability.saturday,
            "sunday" to availability.sunday
        ).forEach { (day, schedule) ->
            val ref = firestore.collection("availability")
                .document(userId)
                .collection("days")
                .document(day)
            batch.set(ref, mapOf(
                "enabled" to schedule.enabled,
                "startHour" to schedule.startHour,
                "startMinute" to schedule.startMinute,
                "endHour" to schedule.endHour,
                "endMinute" to schedule.endMinute
            ))
        }

        batch.commit().await()
    }

    suspend fun getAvailability(userId: String): WeeklyAvailability {
        val days = firestore.collection("availability")
            .document(userId)
            .collection("days")
            .get()
            .await()

        var availability = WeeklyAvailability()
        days.documents.forEach { doc ->
            val schedule = DaySchedule(
                enabled = doc.getBoolean("enabled") ?: false,
                startHour = doc.getLong("startHour")?.toInt() ?: 9,
                startMinute = doc.getLong("startMinute")?.toInt() ?: 0,
                endHour = doc.getLong("endHour")?.toInt() ?: 17,
                endMinute = doc.getLong("endMinute")?.toInt() ?: 0
            )
            when (doc.id) {
                "monday" -> availability = availability.copy(monday = schedule)
                "tuesday" -> availability = availability.copy(tuesday = schedule)
                "wednesday" -> availability = availability.copy(wednesday = schedule)
                "thursday" -> availability = availability.copy(thursday = schedule)
                "friday" -> availability = availability.copy(friday = schedule)
                "saturday" -> availability = availability.copy(saturday = schedule)
                "sunday" -> availability = availability.copy(sunday = schedule)
            }
        }
        return availability
    }
}