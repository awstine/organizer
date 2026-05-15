package com.organizethem.organizethem.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    onNavigateToCreateLink: () -> Unit,
    onNavigateToAvailability: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToManageLinks: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Section matching SignInScreen typography
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Manage your schedule and booking links",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Cards
            DashboardCard(
                title = "Create Booking Link",
                subtitle = "Share your availability with others",
                icon = Icons.Default.AddLink,
                onClick = onNavigateToCreateLink
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardCard(
                title = "My Appointments",
                subtitle = "View upcoming bookings",
                icon = Icons.Default.CalendarMonth,
                onClick = onNavigateToAppointments
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardCard(
                title = "Set Availability",
                subtitle = "Manage your weekly schedule",
                icon = Icons.Default.Schedule,
                onClick = onNavigateToAvailability
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardCard(
                title = "Manage Links",
                subtitle = "Edit or deactivate booking links",
                icon = Icons.Default.Link,
                onClick = onNavigateToManageLinks
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // Sign Out Button matching SignInScreen button style but as an outlined error button
            OutlinedButton(
                onClick = { FirebaseAuth.getInstance().signOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFD32F2F) // Soft Red
                )
            ) {
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Matching the AuthTextField background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container matching the theme's brand color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334D4D).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF334D4D), // Brand color
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E1E1E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}