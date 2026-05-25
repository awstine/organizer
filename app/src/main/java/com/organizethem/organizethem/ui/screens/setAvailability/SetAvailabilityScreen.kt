package com.organizethem.organizethem.ui.screens.setAvailability

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.ui.screens.home.BottomNavItem // Reusing your existing nav
import com.organizethem.organizethem.ui.screens.home.BottomNavTab
import com.organizethem.organizethem.ui.screens.home.CustomBottomNavigation
import java.util.Locale

// --- Design System Colors & Fonts ---
val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)
val MintGreen = Color(0xFF98FFD9)
val DarkCardGreen = Color(0xFF14362E) // The dark green for the link preview
val CardInnerGreen = Color(0xFF1F4A40) // The lighter green inside the link preview

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetAvailabilityScreen(
    viewModel: SetAvailabilityViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToLinks: () -> Unit,
    onSaved: () -> Unit
) {
    // Local states for the new link form
    var meetingName by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(15) }

    Scaffold(
        containerColor = Color(0xFFFAFAFA), // Off-white background
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = BottomNavTab.Availability,
                onDashboard = onNavigateToDashboard,
                onAppointments = onNavigateToAppointments,
                onAvailability = {},
                onLinks = onNavigateToLinks
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Handle FAB click */ },
                containerColor = PrimaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Slot",
                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- Top App Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp).clickable { /* Open Drawer */ }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Organize",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryColor,
                            fontSize = 20.sp
                        )
                    )
                }

                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp),
                    color = SecondaryColor
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = PrimaryColor,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Availability Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Availability",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0E0E0).copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Timezone: GMT-5",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Days List ---
            listOf(
                "MON" to viewModel.monday,
                "TUE" to viewModel.tuesday,
                "SUN" to viewModel.sunday // Mocking the exact screenshot
            ).forEach { (dayName, schedule) ->
                VisualDayScheduleCard(
                    dayName = dayName,
                    schedule = schedule,
                    onScheduleChange = { updatedSchedule ->
                        // Pass full lowercase name to viewmodel
                        val fullDay = when(dayName) { "MON" -> "monday"; "TUE" -> "tuesday"; else -> "sunday" }
                        viewModel.updateDay(fullDay, updatedSchedule)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- New Scheduling Link Section ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "New Scheduling Link",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                )
                // Small decorative underline
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Box(modifier = Modifier.width(24.dp).height(3.dp).clip(RoundedCornerShape(1.5.dp)).background(PrimaryColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.width(16.dp).height(3.dp).clip(RoundedCornerShape(1.5.dp)).background(Color.LightGray.copy(alpha = 0.5f)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.width(16.dp).height(3.dp).clip(RoundedCornerShape(1.5.dp)).background(Color.LightGray.copy(alpha = 0.5f)))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Meeting Name
            Text("Meeting Name", style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, color = PrimaryColor))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = meetingName,
                onValueChange = { meetingName = it },
                modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)),
                placeholder = { Text("e.g. Discovery Consultation", color = Color.Gray.copy(alpha = 0.8f)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SecondaryColor,
                    unfocusedContainerColor = SecondaryColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = InterFont)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Duration Selector
            Text("Duration", style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, color = PrimaryColor))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(15, 30, 60).forEach { duration ->
                    DurationPill(
                        duration = duration,
                        isSelected = selectedDuration == duration,
                        onClick = { selectedDuration = duration },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Platform Dropdown Mock
            Text("Platform", style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, color = PrimaryColor))
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = SecondaryColor
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Google Meet", style = MaterialTheme.typography.bodyLarge.copy(fontFamily = InterFont, color = NeutralColor))
                    Icon(imageVector = Icons.Outlined.ExpandMore, contentDescription = "Select", tint = NeutralColor)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Generate Link Button
            Button(
                onClick = { /* Handle logic */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Generate Link", style = MaterialTheme.typography.titleMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Link Preview Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardGreen)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Link, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Link Preview", style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont), color = PrimaryColor)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardInnerGreen
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "organize.com/alexm/discove...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                shape = CircleShape,
                                color = MintGreen,
                                modifier = Modifier.size(40.dp).clickable { /* Copy Link */ }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = DarkCardGreen,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Buffer for FAB
        }
    }
}

@Composable
fun VisualDayScheduleCard(
    dayName: String,
    schedule: DayScheduleState,
    onScheduleChange: (DayScheduleState) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day Label & Switch
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont, fontWeight = FontWeight.ExtraBold),
                        color = if (schedule.enabled) PrimaryColor else Color.Gray,
                        modifier = Modifier.width(48.dp)
                    )
                    Switch(
                        checked = schedule.enabled,
                        onCheckedChange = { checked -> onScheduleChange(schedule.copy(enabled = checked)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryColor,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0),
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.scale(0.8f) // Slightly smaller switch to match mockup
                    )
                }

                // Time Text
                if (schedule.enabled) {
                    Text(
                        text = "${formatTime(schedule.startHour, schedule.startMinute)} — ${formatTime(schedule.endHour, schedule.endMinute)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Medium),
                        color = NeutralColor
                    )
                } else {
                    Text(
                        text = "Unavailable",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                        color = Color.Gray
                    )
                }
            }

            // Visual Time Bar (Only shown if enabled)
            if (schedule.enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(start = 48.dp)) {
                    // Background track (Gray)
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color(0xFFEEEEEE)))

                    // Active track (Green)
                    // Note: In a real app, calculate exact width/offset based on startHour and endHour out of 24h.
                    // For mockup accuracy, we represent it roughly in the middle.
                    Box(modifier = Modifier
                        .fillMaxWidth(0.5f) // Representing 8 hours out of roughly workable hours
                        .offset(x = 30.dp)  // Offset to represent starting at 9 AM
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor)
                    )
                }
            }
        }
    }
}

@Composable
fun DurationPill(
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(56.dp).clickable { onClick() },
        shape = CircleShape,
        color = if (isSelected) PrimaryColor else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "${duration}m",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = InterFont,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) Color.White else NeutralColor
            )
        }
    }
}

// Helper function to format 24h time to 12h AM/PM format matching the mockup
fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)
}