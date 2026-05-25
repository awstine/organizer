package com.organizethem.organizethem.ui.screens.myAppointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.domain.Appointment
import com.organizethem.organizethem.domain.BookingLink
import com.organizethem.organizethem.ui.screens.home.BottomNavItem // Reusing from previous screen
import com.organizethem.organizethem.ui.screens.home.BottomNavTab
import com.organizethem.organizethem.ui.screens.home.CustomBottomNavigation // Reusing from previous screen

// --- Design System Colors & Fonts ---
val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)
val MintGreen = Color(0xFF98FFD9)
val DangerRed = Color(0xFFE57373)

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(
    viewModel: MyAppointmentsViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit,
    onNavigateToAvailability: () -> Unit,
    onNavigateToLinks: () -> Unit,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Upcoming") }
    val filters = listOf("Upcoming", "Past", "Cancelled")

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = BottomNavTab.Bookings,
                onDashboard = onNavigateToDashboard,
                onAppointments = {},
                onAvailability = onNavigateToAvailability,
                onLinks = onNavigateToLinks
            )
        },
        floatingActionButton = {
            // Remove the add new slot button as per user request
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
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

                // Profile Image Placeholder
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

            // --- Header & Subtitle ---
            Text(
                text = "My Appointments",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    color = NeutralColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Review and manage your upcoming schedule with clarity.",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Filter Pills ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                filters.forEach { filter ->
                    FilterPill(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Main Content Area ---
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                ) {
                    // Actual Appointments
                    items(viewModel.appointments) { appointment ->
                        var showDialog by remember { mutableStateOf(false) }
                        val bookingLink = viewModel.bookingLinks[appointment.linkId]

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Cancel Appointment") },
                                text = { Text("Are you sure you want to cancel this appointment with ${appointment.bookerName}?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            viewModel.cancelAppointment(appointment.appointmentId)
                                            showDialog = false
                                        },
                                        colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                                    ) {
                                        Text("Yes, Cancel")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("No, Keep it")
                                    }
                                }
                            )
                        }

                        AppointmentCard(
                            appointment = appointment,
                            bookingLink = bookingLink,
                            onCancel = { showDialog = true }
                        )
                    }

                    // --- Empty State / Add New Slot Card removed as per request ---
                }
            }
        }
    }
}

@Composable
fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) PrimaryColor else SecondaryColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = InterFont,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    bookingLink: BookingLink?,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Top Row: Status Badge and Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isScheduled = appointment.status.lowercase() == "scheduled"

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isScheduled) MintGreen else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = appointment.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (isScheduled) PrimaryColor else Color(0xFFC62828)
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Options",
                    tint = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Appointment Title (Pulled from BookingLink title)
            Text(
                text = bookingLink?.title ?: "Meeting",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold
                ),
                color = NeutralColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date/Time Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    // Format date/time based on your utils. Example: "Today, 2:30 PM — 3:45 PM"
                    text = "${appointment.date}, ${appointment.startTime} — ${appointment.endTime}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Booker Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appointment.bookerName, // e.g., "Sarah Jenkins, Lead Designer"
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                    color = Color.Gray
                )
            }

            if (appointment.meetLink != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Videocam,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Join Online Meeting",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, color = PrimaryColor, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { /* Handle link click */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* Edit Logic */ },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Edit Event", style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont))
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, DangerRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont))
                }
            }
        }
    }
}
