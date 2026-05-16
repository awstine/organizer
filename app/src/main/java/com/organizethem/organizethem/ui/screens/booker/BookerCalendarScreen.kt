package com.organizethem.organizethem.ui.screens.booker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.util.DateUtils

@Composable
fun BookerCalendarScreen(
    linkId: String,
    onTimeSlotSelected: (String, String, Int) -> Unit  // date, time, duration
) {
    val viewModel: BookerCalendarViewModel = hiltViewModel()

    LaunchedEffect(linkId) {
        viewModel.loadLink(linkId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Section
            viewModel.link?.let { link ->
                Text(
                    text = link.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = Color(0xFF1E1E1E)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${link.duration} min session",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334D4D),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                link.description?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Date Selection Label
            Text(
                text = "Select a Date",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Date Picker
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(viewModel.availableDates) { date ->
                    val isSelected = viewModel.selectedDate == date
                    DateChip(
                        date = date,
                        isSelected = isSelected,
                        onClick = { viewModel.selectDate(date) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Time Slots Section
            viewModel.selectedDate?.let { date ->
                Text(
                    text = "Available Times",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E1E1E)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (viewModel.isLoadingSlots) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF334D4D))
                    }
                } else {
                    if (viewModel.availableSlots.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "No slots available for this date.",
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(viewModel.availableSlots) { slot ->
                                TimeSlotCard(
                                    time = slot,
                                    onClick = { onTimeSlotSelected(date, slot, viewModel.link?.duration ?: 30) }
                                )
                            }
                        }
                    }
                }
            } ?: run {
                // Placeholder when no date is selected
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Pick a date to see available times",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun DateChip(
    date: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF334D4D) else Color(0xFFF5F5F5)
    val textColor = if (isSelected) Color.White else Color.Black
    val borderColor = if (isSelected) Color(0xFF334D4D) else Color(0xFFEEEEEE)

    Column(
        modifier = Modifier
            .width(65.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = DateUtils.formatMonth(date),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
        )
        Text(
            text = DateUtils.formatDay(date),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            text = DateUtils.getDayOfWeek(date).take(3).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
        )
    }
}

@Composable
fun TimeSlotCard(
    time: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color(0xFFF5F5F5),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Text(
            text = time,
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E1E1E)
            )
        )
    }
}
