package com.organizethem.organizethem.domain

data class BookingLink(
    val linkId: String,
    val ownerId: String,
    val title: String,
    val duration: Int,
    val description: String = "",
    val active: Boolean = true,
    val meetingType: String = "online", // "online", "in-person", "choice"
    val location: String = "" // Physical address if in-person is forced
)
