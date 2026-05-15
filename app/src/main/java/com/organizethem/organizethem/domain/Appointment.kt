package com.organizethem.organizethem.domain

import kotlin.time.Duration


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
    val status: String = "scheduled"
)