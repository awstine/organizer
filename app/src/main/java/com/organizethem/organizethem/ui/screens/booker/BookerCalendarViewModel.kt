package com.organizethem.organizethem.ui.screens.booker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.functions.FirebaseFunctions
import com.organizethem.organizethem.data.remote.AppointmentsCollection
import com.organizethem.organizethem.data.remote.AvailabilityCollection
import com.organizethem.organizethem.domain.BookingLink
import com.organizethem.organizethem.domain.BookingLinksCollection
import com.organizethem.organizethem.util.DateUtils
import com.organizethem.organizethem.util.TimeSlotGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class BookerCalendarViewModel @Inject constructor(
    private val bookingLinksCollection: BookingLinksCollection,
    private val availabilityCollection: AvailabilityCollection,
    private val appointmentsCollection: AppointmentsCollection,
    private val functions: FirebaseFunctions
) : ViewModel() {
    var link by mutableStateOf<BookingLink?>(null)
    var availableDates by mutableStateOf<List<String>>(emptyList())
    var selectedDate by mutableStateOf<String?>(null)
    var availableSlots by mutableStateOf<List<String>>(emptyList())
    var isLoadingSlots by mutableStateOf(false)
    var selectedMeetingType by mutableStateOf("online") // Guest's choice

    suspend fun loadLink(linkId: String) {
        link = bookingLinksCollection.getLink(linkId) ?: return
        selectedMeetingType = if (link?.meetingType == "in-person") "in-person" else "online"

        // Generate next 30 days
        val dates = (0..30).map { daysToAdd ->
            LocalDate.now().plusDays(daysToAdd.toLong())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        // Filter to available days
        link?.let { link ->
            val availability = availabilityCollection.getAvailability(link.ownerId)
            availableDates = dates.filter { date ->
                val dayOfWeek = DateUtils.getDayOfWeek(date)
                val schedule = when (dayOfWeek) {
                    "monday" -> availability.monday
                    "tuesday" -> availability.tuesday
                    "wednesday" -> availability.wednesday
                    "thursday" -> availability.thursday
                    "friday" -> availability.friday
                    "saturday" -> availability.saturday
                    "sunday" -> availability.sunday
                    else -> null
                }
                schedule?.enabled == true && !DateUtils.isDateInPast(date)
            }
        }
    }

    fun selectDate(date: String) {
        selectedDate = date
        viewModelScope.launch {
            val link = link ?: return@launch
            isLoadingSlots = true
            try {
                // Call Firebase Function to get available slots (which checks Google Calendar)
                val data = hashMapOf(
                    "linkId" to link.linkId,
                    "date" to date,
                    "duration" to link.duration
                )
                
                val result = functions
                    .getHttpsCallable("getAvailableTimeSlots")
                    .call(data)
                    .await()
                
                @Suppress("UNCHECKED_CAST")
                availableSlots = result.data as? List<String> ?: emptyList()
            } catch (e: Exception) {
                // Fallback to local calculation if function fails
                val availability = availabilityCollection.getAvailability(link.ownerId)
                val dayOfWeek = DateUtils.getDayOfWeek(date)
                val schedule = when (dayOfWeek) {
                    "monday" -> availability.monday
                    "tuesday" -> availability.tuesday
                    "wednesday" -> availability.wednesday
                    "thursday" -> availability.thursday
                    "friday" -> availability.friday
                    "saturday" -> availability.saturday
                    "sunday" -> availability.sunday
                    else -> return@launch
                }

                val bookedSlots = appointmentsCollection.getBookedSlots(link.ownerId, date)
                
                // Use custom hours if provided by the link, otherwise fallback to general availability
                val effectiveSchedule = if (link.customStartHour != null && link.customEndHour != null) {
                    schedule.copy(
                        startHour = link.customStartHour,
                        startMinute = link.customStartMinute ?: 0,
                        endHour = link.customEndHour,
                        endMinute = link.customEndMinute ?: 0
                    )
                } else if (link.meetingType == "in-person" && link.customStartHour != null) {
                    // For in-person, we might just have a specific meetup time
                    schedule.copy(
                        startHour = link.customStartHour,
                        startMinute = link.customStartMinute ?: 0,
                        endHour = link.customStartHour,
                        endMinute = (link.customStartMinute ?: 0) + link.duration
                    )
                } else {
                    schedule
                }

                availableSlots = TimeSlotGenerator.generateSlots(effectiveSchedule, link.duration, bookedSlots)
            } finally {
                isLoadingSlots = false
            }
        }
    }
}