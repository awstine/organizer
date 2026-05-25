package com.organizethem.organizethem.ui.screens.booker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.AppointmentsCollection
import com.organizethem.organizethem.data.remote.BookingResult
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

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("Confirm Appointment", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow("Date", selectedDate)
                    DetailRow("Time", "$selectedTime (${duration} min)")
                    
                    viewModel.meetingType?.let { type ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (type == "online") Icons.Default.VideoCall else Icons.Default.LocationOn,
                                null,
                                tint = Color(0xFF334D4D),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (type == "online") "Google Meet Online" else "In-Person: ${viewModel.location}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Your Details", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.bookerName,
                onValueChange = { viewModel.bookerName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.bookerEmail,
                onValueChange = { viewModel.bookerEmail = it },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.confirmBooking(linkId, selectedDate, selectedTime, duration) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334D4D)),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Confirm and Sync to Calendar", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (viewModel.bookingSuccess) {
        AlertDialog(
            onDismissRequest = onBookingComplete,
            title = { Text("Confirmed!") },
            text = { Text("Your meeting is scheduled and pinned to the organizer's calendar. Reminders have been enabled.") },
            confirmButton = {
                Button(onClick = onBookingComplete, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334D4D))) {
                    Text("Great!")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@HiltViewModel
class BookingConfirmationViewModel @Inject constructor(
    private val appointmentsCollection: AppointmentsCollection,
    private val bookingLinksCollection: BookingLinksCollection
) : ViewModel() {
    var bookerName by mutableStateOf("")
    var bookerEmail by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var bookingSuccess by mutableStateOf(false)
    
    var meetingType by mutableStateOf<String?>(null)
    var location by mutableStateOf("")

    fun loadDetails(linkId: String) {
        viewModelScope.launch {
            val link = bookingLinksCollection.getLink(linkId)
            meetingType = link?.meetingType
            location = link?.location ?: ""
        }
    }

    fun confirmBooking(linkId: String, date: String, startTime: String, duration: Int) {
        if (bookerName.isBlank() || bookerEmail.isBlank()) return
        
        viewModelScope.launch {
            isLoading = true
            val link = bookingLinksCollection.getLink(linkId) ?: return@launch
            
            val (h, m) = startTime.split(":").map { it.toInt() }
            val totalMin = h * 60 + m + duration
            val endTime = String.format("%02d:%02d", totalMin / 60, totalMin % 60)

            val appointment = Appointment(
                appointmentId = "${date}_${link.ownerId}_${startTime}",
                ownerId = link.ownerId,
                linkId = linkId,
                date = date,
                startTime = startTime,
                endTime = endTime,
                bookerName = bookerName,
                bookerEmail = bookerEmail,
                duration = duration,
                meetingType = link.meetingType,
                location = link.location
            )

            when (appointmentsCollection.bookAppointment(appointment)) {
                is BookingResult.Success -> {
                    bookingSuccess = true
                    // Note: In a real-world Blaze-enabled app, a Cloud Function 
                    // would detect this DB write and automatically create the Google Meet
                }
                else -> { /* Handle Error */ }
            }
            isLoading = false
        }
    }
}
