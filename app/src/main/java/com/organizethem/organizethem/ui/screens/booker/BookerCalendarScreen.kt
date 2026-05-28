@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.organizethem.organizethem.ui.screens.booker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.util.DateUtils
import java.util.Locale

// --- Design System Colors & Fonts ---
val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)
val LightSurfaceColor = Color(0xFFFAFAFA)

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookerCalendarScreen(
    linkId: String,
    onTimeSlotSelected: (String, String, Int, String) -> Unit
) {
    val viewModel: BookerCalendarViewModel = hiltViewModel()
    val scrollState = rememberScrollState()

    // Confirmation logic states
    var bookerName by remember { mutableStateOf("") }
    var bookerEmail by remember { mutableStateOf("") }
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(linkId) {
        viewModel.loadLink(linkId)
    }

    Scaffold(
        containerColor = LightSurfaceColor,
        topBar = {
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Organize",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryColor,
                            fontSize = 20.sp
                        )
                    )
                    Surface(shape = CircleShape, modifier = Modifier.size(32.dp), color = SecondaryColor) {
                        Icon(Icons.Filled.Person, null, tint = PrimaryColor, modifier = Modifier.padding(6.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- Header Info (Tag + Title + Description) ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = SecondaryColor) {
                        Text(
                            text = viewModel.link?.title?.uppercase() ?: "LINK",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${viewModel.link?.duration ?: 30} min", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = viewModel.link?.title ?: "Meeting Title",
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
                    color = PrimaryColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = viewModel.link?.description ?: "A brief introductory call to discuss your goals and how we can work together.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Meeting Type Choice (Flexible links) ---
            if (viewModel.link?.meetingType == "both") {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("HOW SHOULD WE MEET?", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MeetingChoiceCard(
                            title = "Online",
                            icon = Icons.Outlined.Videocam,
                            isSelected = viewModel.selectedMeetingType == "online",
                            onClick = { viewModel.selectedMeetingType = "online" },
                            modifier = Modifier.weight(1f)
                        )
                        MeetingChoiceCard(
                            title = "In-person",
                            icon = Icons.Outlined.LocationOn,
                            isSelected = viewModel.selectedMeetingType == "in-person",
                            onClick = { viewModel.selectedMeetingType = "in-person" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // --- Date Selection ---
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SELECT DATE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Gray)
                    Text(
                        text = "October 2023", // Replace with dynamic logic
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    items(viewModel.availableDates) { date ->
                        val isSelected = viewModel.selectedDate == date
                        DateSelectorBubble(
                            date = date,
                            isSelected = isSelected,
                            onClick = { viewModel.selectDate(date) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Time Slots ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Timer, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AVAILABLE TIMES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (viewModel.isLoadingSlots) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally), color = PrimaryColor)
                } else if (viewModel.availableSlots.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        viewModel.availableSlots.forEach { slot ->
                            val isSelected = selectedTimeSlot == slot
                            TimeSlotPill(
                                slot = slot,
                                isSelected = isSelected,
                                onClick = { selectedTimeSlot = slot },
                                modifier = Modifier.weight(1f).fillMaxWidth(0.48f)
                            )
                        }
                    }
                } else if (viewModel.selectedDate != null) {
                    Text("No availability on this date.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Booker Info Form ---
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Your Information", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = NeutralColor)
                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Full Name", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = bookerName,
                        onValueChange = { bookerName = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp)),
                        placeholder = { Text("Jane Doe", color = Color.Gray.copy(alpha = 0.5f)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SecondaryColor,
                            unfocusedContainerColor = SecondaryColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Email Address", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = bookerEmail,
                        onValueChange = { bookerEmail = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp)),
                        placeholder = { Text("jane@example.com", color = Color.Gray.copy(alpha = 0.5f)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SecondaryColor,
                            unfocusedContainerColor = SecondaryColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (viewModel.selectedDate != null && selectedTimeSlot != null && bookerName.isNotBlank() && bookerEmail.isNotBlank()) {
                                onTimeSlotSelected(
                                    viewModel.selectedDate!!,
                                    selectedTimeSlot!!,
                                    viewModel.link?.duration ?: 30,
                                    viewModel.selectedMeetingType
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        enabled = selectedTimeSlot != null && bookerName.isNotBlank() && bookerEmail.isNotBlank()
                    ) {
                        Text("Confirm Booking", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun MeetingChoiceCard(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(56.dp).clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        color = if (isSelected) PrimaryColor else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) Color.White else PrimaryColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = if (isSelected) Color.White else PrimaryColor)
        }
    }
}

@Composable
fun DateSelectorBubble(date: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(32.dp), // Pill shape matching screenshot
        color = if (isSelected) PrimaryColor else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier.width(70.dp).height(90.dp).clickable { onClick() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(DateUtils.getDayOfWeek(date).take(3).capitalize(), style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(DateUtils.formatDay(date), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = if (isSelected) Color.White else NeutralColor)
        }
    }
}

@Composable
fun TimeSlotPill(slot: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) PrimaryColor else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = modifier.height(52.dp).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(slot, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = if (isSelected) Color.White else NeutralColor)
        }
    }
}
