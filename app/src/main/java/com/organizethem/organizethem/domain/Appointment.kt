package com.organizethem.organizethem.domain

data class Appointment(
    val appointmentId: String,
    val ownerId: String,
    val linkId: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val bookerName: String,
    val bookerEmail: String,
    val duration: Int,
    val status: String = "scheduled",
    val meetingType: String = "online", // "online" or "in-person"
    val location: String = "",
    val meetLink: String? = null,
    val googleEventId: String? = null,
    val createdAt: Long? = null // Timestamp in milliseconds
)
