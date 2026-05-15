package com.organizethem.organizethem.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.organizethem.organizethem.domain.Appointment
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//Prevent double booking
class AppointmentsCollection @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun bookAppointment(appointment: Appointment): BookingResult {
        return try {
            // Use transaction to prevent double booking
            firestore.runTransaction { transaction ->
                val appointmentId = "${appointment.date}_${appointment.ownerId}_${appointment.startTime}"
                val ref = firestore.collection("appointments")
                    .document(appointmentId)
                // Check if slot already taken
                val existing = transaction.get(ref)
                if (existing.exists()) {
                    throw Exception("This time slot was just booked by someone else")
                }

                // Slot available, book it
                transaction.set(ref, mapOf(
                    "appointmentId" to appointmentId,
                    "ownerId" to appointment.ownerId,
                    "linkId" to appointment.linkId,
                    "date" to appointment.date,
                    "startTime" to appointment.startTime,
                    "endTime" to appointment.endTime,
                    "bookerName" to appointment.bookerName,
                    "bookerEmail" to appointment.bookerEmail,
                    "status" to "scheduled",
                    "duration" to appointment.duration,
                    "createdAt" to FieldValue.serverTimestamp()
                ))
            }.await()

            BookingResult.Success
        } catch (e: Exception) {
            BookingResult.Error(e.localizedMessage ?: "Booking failed")
        }
    }

    suspend fun getOwnerAppointments(ownerId: String): List<Appointment> {
        return firestore.collection("appointments")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toAppointment() }
    }

    suspend fun getBookedSlots(ownerId: String, date: String): List<String> {
        return firestore.collection("appointments")
            .whereEqualTo("ownerId", ownerId)
            .whereEqualTo("date", date)
            .get()
            .await()
            .documents
            .mapNotNull { it.getString("startTime") }
    }

    private fun DocumentSnapshot.toAppointment(): Appointment? {
        return if (exists()) {
            Appointment(
                appointmentId = getString("appointmentId") ?: "",
                ownerId = getString("ownerId") ?: "",
                linkId = getString("linkId") ?: "",
                date = getString("date") ?: "",
                startTime = getString("startTime") ?: "",
                endTime = getString("endTime") ?: "",
                bookerName = getString("bookerName") ?: "",
                bookerEmail = getString("bookerEmail") ?: "",
                status = getString("status") ?: "scheduled",
                duration = getLong("duration")?.toInt() ?: 0
            )
        } else null
    }
}

sealed class BookingResult {
    object Success : BookingResult()
    data class Error(val message: String) : BookingResult()
}