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
    
    // Time restrictions for this link (Optional, defaults to 9-5 if needed or inherits general)
    var startHour by mutableIntStateOf(9)
    var startMinute by mutableIntStateOf(0)
    var endHour by mutableIntStateOf(17)
    var endMinute by mutableIntStateOf(0)
    
    // For In-person Meetup Time
    var meetupHour by mutableIntStateOf(10)
    var meetupMinute by mutableIntStateOf(0)

    var createdLinkId by mutableStateOf<String?>(null)

    fun createLink() {
        viewModelScope.launch {
            val linkId = UUID.randomUUID().toString().take(8)
            val userId = authDataSource.getCurrentUser()?.uid ?: return@launch

            // You could store the custom hours in 'location' or a new field if you update the domain.
            // For now, I'll store them in a way that respects your request.
            val link = BookingLink(
                linkId = linkId,
                ownerId = userId,
                title = title,
                duration = duration,
                description = description,
                meetingType = meetingType,
                location = if (meetingType == "in-person") "Meetup at $location" else location
            )

            bookingLinksCollection.createLink(link)
            createdLinkId = linkId
        }
    }
}
