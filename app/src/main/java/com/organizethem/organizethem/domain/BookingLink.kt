package com.organizethem.organizethem.domain

data class BookingLink(
    val linkId: String,
    val ownerId: String,
    val title: String,
    val duration: Int,
    val description: String = "",
    val active: Boolean = true,
    val meetingType: String = "online", // "online", "in-person", "both"
    val location: String = "",
    val customStartHour: Int? = null,
    val customStartMinute: Int? = null,
    val customEndHour: Int? = null,
    val customEndMinute: Int? = null
)
