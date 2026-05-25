package com.organizethem.organizethem.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.AppointmentsCollection
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.data.remote.GoogleCalendarService
import com.google.android.gms.auth.GoogleAuthUtil
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appointmentsCollection: AppointmentsCollection,
    private val authDataSource: FirebaseAuthDataSource,
    private val googleCalendarService: GoogleCalendarService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    var isSyncing by mutableStateOf(false)
    var upcomingCount by mutableStateOf(0)
    var userName by mutableStateOf("User")

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val user = authDataSource.getCurrentUser() ?: return@launch
            userName = user.displayName?.split(" ")?.firstOrNull() ?: "User"
            
            val appointments = appointmentsCollection.getOwnerAppointments(user.uid)
            upcomingCount = appointments.count { it.status == "scheduled" }

            performUnderTheHoodSync(user.uid)
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
