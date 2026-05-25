package com.organizethem.organizethem.data.remote

import android.content.Context
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.ConferenceData
import com.google.api.services.calendar.model.ConferenceSolutionKey
import com.google.api.services.calendar.model.CreateConferenceRequest
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import com.google.firebase.auth.FirebaseAuth
import com.organizethem.organizethem.domain.Appointment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleCalendarService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth
) {
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    suspend fun createCalendarEvent(appointment: Appointment, accessToken: String): Event? = withContext(Dispatchers.IO) {
        try {
            val service = Calendar.Builder(httpTransport, jsonFactory) { request ->
                request.headers.authorization = "Bearer $accessToken"
            }
                .setApplicationName("Organize Them")
                .build()

            val event = Event().apply {
                summary = appointment.bookerName + " / " + appointment.meetingType
                description = "Booking from Organize Them app."
                location = if (appointment.meetingType == "in-person") appointment.location else "Google Meet"
                
                start = EventDateTime().apply {
                    dateTime = com.google.api.client.util.DateTime("${appointment.date}T${appointment.startTime}:00Z")
                }
                end = EventDateTime().apply {
                    dateTime = com.google.api.client.util.DateTime("${appointment.date}T${appointment.endTime}:00Z")
                }

                attendees = listOf(EventAttendee().setEmail(appointment.bookerEmail))
                
                reminders = Event.Reminders().apply {
                    useDefault = false
                    overrides = listOf(
                        EventReminder().setMethod("popup").setMinutes(30),
                        EventReminder().setMethod("email").setMinutes(60)
                    )
                }

                if (appointment.meetingType == "online") {
                    conferenceData = ConferenceData().apply {
                        createRequest = CreateConferenceRequest().apply {
                            requestId = UUID.randomUUID().toString()
                            conferenceSolutionKey = ConferenceSolutionKey().setType("hangoutsMeet")
                        }
                    }
                }
            }

            service.events().insert("primary", event)
                .setSendUpdates("all")
                .setConferenceDataVersion(1)
                .execute()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
