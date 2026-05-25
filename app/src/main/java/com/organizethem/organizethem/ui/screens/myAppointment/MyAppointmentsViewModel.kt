package com.organizethem.organizethem.ui.screens.myAppointment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.AppointmentsCollection
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.Appointment
import com.organizethem.organizethem.domain.BookingLink
import com.organizethem.organizethem.domain.BookingLinksCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyAppointmentsViewModel @Inject constructor(
    private val appointmentsCollection: AppointmentsCollection,
    private val bookingLinksCollection: BookingLinksCollection,
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {
    var appointments by mutableStateOf<List<Appointment>>(emptyList())
    var bookingLinks by mutableStateOf<Map<String, BookingLink>>(emptyMap())
    var isLoading by mutableStateOf(true)

    init {
        loadAppointments()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            val userId = authDataSource.getCurrentUser()?.uid ?: return@launch
            val fetchedAppointments = appointmentsCollection.getOwnerAppointments(userId)
            
            // Fetch unique booking links
            val linkIds = fetchedAppointments.map { it.linkId }.distinct()
            val linksMap = linkIds.map { linkId ->
                async { linkId to bookingLinksCollection.getLink(linkId) }
            }.awaitAll().mapNotNull { (id, link) -> link?.let { id to it } }.toMap()

            appointments = fetchedAppointments
            bookingLinks = linksMap
            isLoading = false
        }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            isLoading = true
            appointmentsCollection.cancelAppointment(appointmentId)
            // Refresh the list after deletion
            loadAppointments()
        }
    }
}
