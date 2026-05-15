package com.organizethem.organizethem.ui.screens.booker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.AppointmentsCollection
import com.organizethem.organizethem.data.remote.BookingResult
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.Appointment
import com.organizethem.organizethem.domain.BookingLinksCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun BookingConfirmationScreen(
    linkId: String,
    selectedDate: String,
    selectedTime: String,
    duration: Int,
    onBookingComplete: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: BookingConfirmationViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Text(
            "Confirm Your Booking",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Booking Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow("Date", selectedDate)
                SummaryRow("Time", selectedTime)
                SummaryRow("Duration", "$duration minutes")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Booker Info Form
        Text("Your Information", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.bookerName,
            onValueChange = { viewModel.bookerName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.bookerEmail,
            onValueChange = { viewModel.bookerEmail = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.message,
            onValueChange = { viewModel.message = it },
            label = { Text("Message (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Error message
        viewModel.error?.let { error ->
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    viewModel.confirmBooking(
                        linkId = linkId,
                        date = selectedDate,
                        startTime = selectedTime,
                        duration = duration
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirm Booking")
                }
            }
        }

        // Success Dialog
        if (viewModel.bookingSuccess) {
            AlertDialog(
                onDismissRequest = onBookingComplete,
                title = { Text("Booking Confirmed!") },
                text = {
                    Text("Your appointment has been scheduled successfully.")
                },
                confirmButton = {
                    Button(onClick = onBookingComplete) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@HiltViewModel
class BookingConfirmationViewModel @Inject constructor(
    private val appointmentsCollection: AppointmentsCollection,
    private val bookingLinksCollection: BookingLinksCollection,
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {
    var bookerName by mutableStateOf("")
    var bookerEmail by mutableStateOf("")
    var message by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var bookingSuccess by mutableStateOf(false)

    fun confirmBooking(
        linkId: String,
        date: String,
        startTime: String,
        duration: Int
    ) {
        // Validate inputs
        if (bookerName.isBlank() || bookerEmail.isBlank()) {
            error = "Please fill in all required fields"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            // Get link to know owner
            val link = bookingLinksCollection.getLink(linkId)
            if (link == null) {
                error = "This booking link is no longer valid"
                isLoading = false
                return@launch
            }

            // Calculate end time
            val parts = startTime.split(":")
            val startMinutes = parts[0].toInt() * 60 + parts[1].toInt()
            val endMinutes = startMinutes + duration
            val endTime = String.format("%02d:%02d", endMinutes / 60, endMinutes % 60)

            val appointment = Appointment(
                appointmentId = "${date}_${link.ownerId}_${startTime}",
                ownerId = link.ownerId,
                linkId = linkId,
                date = date,
                startTime = startTime,
                endTime = endTime,
                bookerName = bookerName,
                bookerEmail = bookerEmail,
                status = "scheduled",
                duration = duration
            )

            when (val result = appointmentsCollection.bookAppointment(appointment)) {
                is BookingResult.Success -> {
                    bookingSuccess = true
                }
                is BookingResult.Error -> {
                    error = result.message
                }
            }

            isLoading = false
        }
    }
}