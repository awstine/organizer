package com.organizethem.organizethem.ui.screens.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.organizethem.organizethem.data.remote.AppointmentsCollection
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.data.remote.GoogleCalendarService
import com.organizethem.organizethem.domain.Appointment
import com.organizethem.organizethem.domain.BookingLinksCollection
import com.organizethem.organizethem.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appointmentsCollection: AppointmentsCollection,
    private val bookingLinksCollection: BookingLinksCollection,
    private val authDataSource: FirebaseAuthDataSource,
    private val googleCalendarService: GoogleCalendarService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    var isSyncing by mutableStateOf(false)
    var upcomingCount by mutableStateOf(0)
    var userName by mutableStateOf("User")
    var recentActivities by mutableStateOf<List<RecentActivity>>(emptyList())

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val user = authDataSource.getCurrentUser() ?: return@launch
            userName = user.displayName?.split(" ")?.firstOrNull() ?: "User"
            
            val appointments = appointmentsCollection.getOwnerAppointments(user.uid)
            upcomingCount = appointments.count { it.status == "scheduled" }

            // Load activities
            loadRecentActivities(user.uid, appointments)

            performUnderTheHoodSync(user.uid)
        }
    }

    private suspend fun loadRecentActivities(userId: String, appointments: List<Appointment>) {
        val links = bookingLinksCollection.getOwnerLinks(userId).associateBy { it.linkId }
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        recentActivities = appointments
            .sortedByDescending { it.createdAt ?: 0L }
            .take(5)
            .map { appointment ->
                val linkTitle = links[appointment.linkId]?.title ?: "Meeting"
                val relativeTime = appointment.createdAt?.let { getRelativeTime(it) } ?: ""
                
                val apptDate = try { LocalDate.parse(appointment.date, dateFormatter) } catch(e: Exception) { null }
                val dateLabel = when (apptDate) {
                    today -> "Today,"
                    tomorrow -> "Tomorrow,"
                    null -> ""
                    else -> DateUtils.formatDisplayDate(appointment.date) + ","
                }

                when (appointment.status) {
                    "completed" -> RecentActivity(
                        type = ActivityType.COMPLETED,
                        title = "Completed: $linkTitle",
                        subtitle = "With ${appointment.bookerName} ${appointment.startTime}",
                        timeLabel = "$dateLabel ${appointment.startTime}",
                        relativeTime = relativeTime
                    )
                    "canceled" -> RecentActivity(
                        type = ActivityType.CANCELED,
                        title = "Canceled: $linkTitle",
                        subtitle = "By ${appointment.bookerName}",
                        timeLabel = "Canceled",
                        relativeTime = relativeTime
                    )
                    else -> RecentActivity(
                        type = ActivityType.NEW_BOOKING,
                        title = "New Booking: $linkTitle",
                        subtitle = "With ${appointment.bookerName} ${appointment.startTime}",
                        timeLabel = "$dateLabel ${appointment.startTime}",
                        relativeTime = relativeTime
                    )
                }
            }
    }

    private fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
            else -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        }
    }

    private fun performUnderTheHoodSync(userId: String) {
        viewModelScope.launch {
            val user = authDataSource.getCurrentUser() ?: return@launch
            val appointments = appointmentsCollection.getOwnerAppointments(userId)
            
            // Find appointments that are not yet on Google Calendar
            val unsynced = appointments.filter { it.googleEventId == null && it.status == "scheduled" }
            
            if (unsynced.isNotEmpty()) {
                isSyncing = true
                
                try {
                    // Get token for Google Calendar access
                    val scope = "oauth2:https://www.googleapis.com/auth/calendar"
                    val accessToken = withContext(Dispatchers.IO) {
                        GoogleAuthUtil.getToken(context, user.email ?: "", scope)
                    }

                    unsynced.forEach { appointment ->
                        val event = googleCalendarService.createCalendarEvent(appointment, accessToken)
                        if (event != null) {
                            val meetLink = event.conferenceData?.entryPoints?.find { it.entryPointType == "video" }?.uri
                            appointmentsCollection.updateSyncDetails(
                                appointment.appointmentId,
                                event.id,
                                meetLink
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isSyncing = false
                    // Refresh count after sync
                    val updatedAppointments = appointmentsCollection.getOwnerAppointments(userId)
                    upcomingCount = updatedAppointments.count { it.status == "scheduled" }
                }
            }
        }
    }
}

enum class ActivityType {
    NEW_BOOKING, COMPLETED, CANCELED
}

data class RecentActivity(
    val type: ActivityType,
    val title: String,
    val subtitle: String,
    val timeLabel: String,
    val relativeTime: String
)
