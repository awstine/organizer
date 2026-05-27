@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.organizethem.organizethem.ui.screens.booker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.WbCloudy // For morning
import androidx.compose.material.icons.outlined.WbSunny // For afternoon
import androidx.compose.material.icons.outlined.NightsStay // For evening
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.util.DateUtils

// --- Design System Colors & Fonts ---
val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)
val LightSurfaceColor = Color(0xFFFAFAFA) // The off-white background

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookerCalendarScreen(
    linkId: String,
    onTimeSlotSelected: (String, String, Int, String) -> Unit
) {
    val viewModel: BookerCalendarViewModel = hiltViewModel()

    // Local state to hold the selected time before confirming
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }

    // Reset selected time if the user picks a different date
    LaunchedEffect(viewModel.selectedDate) {
        selectedTimeSlot = null
    }

    LaunchedEffect(linkId) {
        viewModel.loadLink(linkId)
    }

    Scaffold(
        containerColor = LightSurfaceColor,
        bottomBar = {
            // Sticky Bottom Bar - Only shows when a time is selected
            if (selectedTimeSlot != null && viewModel.selectedDate != null) {
                Surface(
                    color = Color.White,
                    modifier = Modifier.shadow(elevation = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selected Slot",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont),
                                color = Color.Gray
                            )
                            Text(
                                // Format properly based on your utils
                                text = "${DateUtils.formatDay(viewModel.selectedDate!!)} • $selectedTimeSlot",
                                style = MaterialTheme.typography.titleSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                                color = NeutralColor
                            )
                        }

                        Button(
                            onClick = {
                                onTimeSlotSelected(
                                    viewModel.selectedDate!!,
                                    selectedTimeSlot!!,
                                    viewModel.link?.duration ?: 30,
                                    viewModel.selectedMeetingType
                                )
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text("Next Step", style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont))
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- Header Section ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Organize",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryColor,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0E0E0).copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "External Booking",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select a date and time",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = viewModel.link?.description ?: "Choose a slot that fits your schedule for the premium consultation.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Guest Choice: Online vs In-person ---
                if (viewModel.link?.meetingType == "both") {
                    Text(
                        text = "How should we meet?",
                        style = MaterialTheme.typography.titleSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                        color = PrimaryColor,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MeetingChoiceCard(
                            title = "Online",
                            icon = Icons.Outlined.Videocam,
                            isSelected = viewModel.selectedMeetingType == "online",
                            onClick = { viewModel.selectedMeetingType = "online" },
                            modifier = Modifier.weight(1f)
                        )
                        MeetingChoiceCard(
                            title = "In-person",
                            icon = Icons.Outlined.Public,
                            isSelected = viewModel.selectedMeetingType == "in-person",
                            onClick = { viewModel.selectedMeetingType = "in-person" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Timezone Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SecondaryColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Public, contentDescription = null, tint = NeutralColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GMT +01:00 London", style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont), color = NeutralColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Calendar / Date Selector ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "October 2023", // Replace with dynamic month parsing from viewModel.availableDates
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold, color = PrimaryColor)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = CircleShape, color = SecondaryColor, modifier = Modifier.size(32.dp).clickable { }) {
                        Icon(Icons.Outlined.ChevronLeft, null, modifier = Modifier.padding(6.dp), tint = NeutralColor)
                    }
                    Surface(shape = CircleShape, color = SecondaryColor, modifier = Modifier.size(32.dp).clickable { }) {
                        Icon(Icons.Outlined.ChevronRight, null, modifier = Modifier.padding(6.dp), tint = NeutralColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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

            Spacer(modifier = Modifier.height(32.dp))

            // --- Time Slots Section ---
            if (viewModel.isLoadingSlots) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else if (viewModel.selectedDate != null && viewModel.availableSlots.isNotEmpty()) {

                // Grouping logic (You may need to adjust this depending on how your backend formats strings)
                val morningSlots = viewModel.availableSlots.filter { it.contains("AM") }
                val afternoonSlots = viewModel.availableSlots.filter { it.contains("PM") && (it.startsWith("12") || it.startsWith("01") || it.startsWith("02") || it.startsWith("03") || it.startsWith("04")) }
                val eveningSlots = viewModel.availableSlots.filter { it.contains("PM") && !afternoonSlots.contains(it) }

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    if (morningSlots.isNotEmpty()) {
                        TimeSlotGroup("MORNING", Icons.Outlined.WbCloudy, morningSlots, selectedTimeSlot) { selectedTimeSlot = it }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    if (afternoonSlots.isNotEmpty()) {
                        TimeSlotGroup("AFTERNOON", Icons.Outlined.WbSunny, afternoonSlots, selectedTimeSlot) { selectedTimeSlot = it }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    if (eveningSlots.isNotEmpty()) {
                        TimeSlotGroup("EVENING", Icons.Outlined.NightsStay, eveningSlots, selectedTimeSlot) { selectedTimeSlot = it }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else if (viewModel.selectedDate != null) {
                Text("No slots available for this date.", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(24.dp))
            }

            // --- Details Cards (Consultation Focus & Platform) ---
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                // Consultation Focus Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = SecondaryColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Consultation Focus",
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
                            color = PrimaryColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "This session covers strategic workflow optimization, calendar management audits, and personalized productivity coaching.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Tag Row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Workflow", "Strategy", "Coaching").forEach { tag ->
                                Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                                        color = PrimaryColor,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Video Meeting Card (Mocked visual)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFD9E0DF), // Soft gray-green placeholder for the image
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Videocam, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Video Meeting (Zoom)", style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold), color = PrimaryColor)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Extra space for the sticky bottom bar
        }
    }
}

@Composable
fun MeetingChoiceCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryColor else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else PrimaryColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.White else PrimaryColor
            )
        }
    }
}

@Composable
fun DateSelectorBubble(date: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) PrimaryColor else SecondaryColor
    val textColor = if (isSelected) Color.White else NeutralColor
    val monthTextColor = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = Modifier
            .size(72.dp) // Large circular bubbles
            .clip(CircleShape)
            .clickable { onClick() },
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = DateUtils.getDayOfWeek(date).take(3), // e.g. "Wed"
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont),
                color = monthTextColor
            )
            Text(
                text = DateUtils.formatDay(date), // e.g. "18"
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
                color = textColor
            )
            // Tiny dot indicator for selected
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeSlotGroup(
    title: String,
    icon: ImageVector,
    slots: List<String>,
    selectedSlot: String?,
    onSlotSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        // Using FlowRow so pills wrap nicely without strict grid constraints
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            slots.forEach { slot ->
                val isSelected = slot == selectedSlot

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) PrimaryColor else Color.White,
                    border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier
                        .fillMaxWidth(0.48f) // Roughly two columns
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onSlotSelect(slot) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = slot,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = InterFont,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            ),
                            color = if (isSelected) Color.White else PrimaryColor
                        )
                    }
                }
            }
        }
    }
}