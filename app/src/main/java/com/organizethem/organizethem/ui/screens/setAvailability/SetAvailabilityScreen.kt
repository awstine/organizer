package com.organizethem.organizethem.ui.screens.setAvailability

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.ui.components.TimeBox
import com.organizethem.organizethem.ui.components.TimePickerWrapper
import com.organizethem.organizethem.ui.screens.home.BottomNavTab
import com.organizethem.organizethem.ui.screens.home.CustomBottomNavigation

// --- Design System Colors & Fonts ---
val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)

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
    val scrollState = rememberScrollState()
    
    // Time Picker State
    var showTimePicker by remember { mutableStateOf(false) }
    var pickingForDay by remember { mutableStateOf("") }
    var pickingStartTime by remember { mutableStateOf(true) }
    var initialHour by remember { mutableIntStateOf(9) }
    var initialMinute by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
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
                onClick = { /* New Slot Logic */ },
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
                .verticalScroll(scrollState)
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
            val daysList = listOf(
                "MON" to "monday", "TUE" to "tuesday", "WED" to "wednesday",
                "THU" to "thursday", "FRI" to "friday", "SAT" to "saturday", "SUN" to "sunday"
            )

            daysList.forEach { (abbrev, full) ->
                val schedule = when(full) {
                    "monday" -> viewModel.monday
                    "tuesday" -> viewModel.tuesday
                    "wednesday" -> viewModel.wednesday
                    "thursday" -> viewModel.thursday
                    "friday" -> viewModel.friday
                    "saturday" -> viewModel.saturday
                    else -> viewModel.sunday
                }

                VisualDayScheduleCard(
                    dayAbbrev = abbrev,
                    schedule = schedule,
                    onEnabledChange = { enabled ->
                        viewModel.updateDay(full, schedule.copy(enabled = enabled))
                    },
                    onStartTimeClick = {
                        pickingForDay = full
                        pickingStartTime = true
                        initialHour = schedule.startHour
                        initialMinute = schedule.startMinute
                        showTimePicker = true
                    },
                    onEndTimeClick = {
                        pickingForDay = full
                        pickingStartTime = false
                        initialHour = schedule.endHour
                        initialMinute = schedule.endMinute
                        showTimePicker = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Save Button ---
            Button(
                onClick = { 
                    viewModel.save()
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text(
                    text = "Save Availability",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showTimePicker) {
        TimePickerWrapper(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onTimeSelected = { hour, minute ->
                val currentSchedule = when(pickingForDay) {
                    "monday" -> viewModel.monday
                    "tuesday" -> viewModel.tuesday
                    "wednesday" -> viewModel.wednesday
                    "thursday" -> viewModel.thursday
                    "friday" -> viewModel.friday
                    "saturday" -> viewModel.saturday
                    else -> viewModel.sunday
                }
                
                val updatedSchedule = if (pickingStartTime) {
                    currentSchedule.copy(startHour = hour, startMinute = minute)
                } else {
                    currentSchedule.copy(endHour = hour, endMinute = minute)
                }
                
                viewModel.updateDay(pickingForDay, updatedSchedule)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
fun VisualDayScheduleCard(
    dayAbbrev: String,
    schedule: DayScheduleState,
    onEnabledChange: (Boolean) -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dayAbbrev,
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont, fontWeight = FontWeight.ExtraBold),
                        color = if (schedule.enabled) PrimaryColor else Color.Gray,
                        modifier = Modifier.width(48.dp)
                    )
                    Switch(
                        checked = schedule.enabled,
                        onCheckedChange = onEnabledChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryColor,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0),
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                }

                // Time Pickers
                if (schedule.enabled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TimeBox(
                            hour = schedule.startHour,
                            minute = schedule.startMinute,
                            onClick = onStartTimeClick
                        )
                        Text(
                            text = " — ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        TimeBox(
                            hour = schedule.endHour,
                            minute = schedule.endMinute,
                            onClick = onEndTimeClick
                        )
                    }
                } else {
                    Text(
                        text = "Unavailable",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }

            // Visual Time Bar
            if (schedule.enabled) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(start = 48.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color(0xFFEEEEEE)))
                    
                    val startPos = (schedule.startHour + schedule.startMinute / 60f) / 24f
                    val endPos = (schedule.endHour + schedule.endMinute / 60f) / 24f
                    val widthFraction = (endPos - startPos).coerceIn(0f, 1f)
                    
                    Box(modifier = Modifier
                        .fillMaxWidth(widthFraction)
                        .padding(start = (startPos * 100).dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor)
                    )
                }
            }
        }
    }
}
