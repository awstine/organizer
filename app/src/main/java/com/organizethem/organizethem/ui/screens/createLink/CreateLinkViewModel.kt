package com.organizethem.organizethem.ui.screens.createLink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.BookingLink
import com.organizethem.organizethem.domain.BookingLinksCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateLinkViewModel @Inject constructor(
    private val bookingLinksCollection: BookingLinksCollection,
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var duration by mutableIntStateOf(30)
    var meetingType by mutableStateOf("online") // "online", "in-person", "both"
    var location by mutableStateOf("")
    
    // Time restrictions for this link
    var startHour by mutableIntStateOf(9)
    var startMinute by mutableIntStateOf(0)
    var endHour by mutableIntStateOf(10) // Default to 1 hour window
    var endMinute by mutableIntStateOf(0)
    
    // For In-person Meetup Time
    var meetupHour by mutableIntStateOf(10)
    var meetupMinute by mutableIntStateOf(0)
    var meetupEndHour by mutableIntStateOf(11)
    var meetupEndMinute by mutableIntStateOf(0)

    var createdLinkId by mutableStateOf<String?>(null)

    fun createLink() {
        viewModelScope.launch {
            val linkId = UUID.randomUUID().toString().replace("-", "").take(8)
            val userId = authDataSource.getCurrentUser()?.uid ?: return@launch

            val startTimeTotal = if (meetingType == "in-person") meetupHour * 60 + meetupMinute else startHour * 60 + startMinute
            val endTimeTotal = if (meetingType == "in-person") meetupEndHour * 60 + meetupEndMinute else endHour * 60 + endMinute
            
            // Calculate duration based on the window
            val calculatedDuration = (endTimeTotal - startTimeTotal).coerceAtLeast(5)

            val link = BookingLink(
                linkId = linkId,
                ownerId = userId,
                title = title,
                duration = calculatedDuration,
                description = description,
                meetingType = meetingType,
                location = location,
                customStartHour = if (meetingType != "in-person") startHour else meetupHour,
                customStartMinute = if (meetingType != "in-person") startMinute else meetupMinute,
                customEndHour = if (meetingType != "in-person") endHour else meetupEndHour,
                customEndMinute = if (meetingType != "in-person") endMinute else meetupEndMinute
            )

            bookingLinksCollection.createLink(link)
            createdLinkId = linkId
        }
    }
}
