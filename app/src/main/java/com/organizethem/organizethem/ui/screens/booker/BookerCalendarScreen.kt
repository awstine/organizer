package com.organizethem.organizethem.ui.screens.booker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BookerCalendarScreen(
    linkId: String,
    onTimeSlotSelected: (String, String, Int) -> Unit  // date, time, duration
) {
    val viewModel: BookerCalendarViewModel = hiltViewModel()

    LaunchedEffect(linkId) {
        viewModel.loadLink(linkId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Link info header
        viewModel.link?.let { link ->
            Text(link.title, style = MaterialTheme.typography.headlineMedium)
            Text("${link.duration} minutes", style = MaterialTheme.typography.bodyMedium)
            link.description?.let { Text(it) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simple date selector
        viewModel.availableDates.take(7).forEach { date ->
            Button(
                onClick = { viewModel.selectDate(date) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(date)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time slots for selected date
        viewModel.selectedDate?.let { date ->
            Text("Available times for $date:", style = MaterialTheme.typography.titleMedium)

            if (viewModel.isLoadingSlots) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                viewModel.availableSlots.forEach { slot ->
                    Button(
                        onClick = { onTimeSlotSelected(date, slot, viewModel.link?.duration ?: 30) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(slot)
                    }
                }
                
                if (viewModel.availableSlots.isEmpty()) {
                    Text(
                        "No slots available for this date.",
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}