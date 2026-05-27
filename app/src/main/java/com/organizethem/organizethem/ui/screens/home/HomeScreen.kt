package com.organizethem.organizethem.ui.screens.home

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
import androidx.compose.runtime.Composable
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

// --- Design System Colors & Fonts ---
val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)
val TertiaryMint = Color(0xFFE0F9F1) 

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit,
    onNavigateToCreateLink: () -> Unit,
    onNavigateToAvailability: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToManageLinks: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        bottomBar = { 
            CustomBottomNavigation(
                selectedTab = BottomNavTab.Dashboard,
                onDashboard = onNavigateToDashboard,
                onAppointments = onNavigateToAppointments,
                onAvailability = onNavigateToAvailability,
                onLinks = onNavigateToManageLinks
            ) 
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateLink,
                containerColor = PrimaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create New Link",
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
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

            // --- Greeting & Sync Status ---
            Text(
                text = "Good morning,",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    color = NeutralColor
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = viewModel.userName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralColor
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (viewModel.isSyncing) Color.Gray else Color(0xFF4ADE80)) 
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (viewModel.isSyncing) "Syncing with Google..." else "Calendars Synced",
                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont),
                    color = Color.Gray
                )
                if (viewModel.isSyncing) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = PrimaryColor)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Hero Stats Layout ---
            Card(
                onClick = onNavigateToAppointments,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(modifier = Modifier.padding(24.dp)) {
                    Column {
                        Text(
                            text = "Upcoming Week",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.SemiBold),
                            color = PrimaryColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.upcomingCount.toString(),
                            style = MaterialTheme.typography.displayMedium.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
                            color = PrimaryColor,
                            lineHeight = 40.sp
                        )
                        Text(
                            text = "Appointments",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = ManropeFont, fontWeight = FontWeight.SemiBold),
                            color = PrimaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link Performance Card (Workable Placeholder)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.BarChart,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Active Links",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "System Online",
                        style = MaterialTheme.typography.headlineLarge.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold, fontSize = 24.sp),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Recent Activity
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = Color.Gray
                )
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, color = Color.Gray),
                    modifier = Modifier.clickable { /* View All */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (viewModel.recentActivities.isEmpty()) {
                        Text(
                            text = "No recent activity",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                            color = Color.LightGray,
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        viewModel.recentActivities.forEachIndexed { index, activity ->
                            RecentActivityItem(activity = activity)
                            if (index < viewModel.recentActivities.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFF5F5F5)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) 
        }
    }
}

@Composable
fun RecentActivityItem(activity: RecentActivity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        val (icon, color, bgColor) = when (activity.type) {
            ActivityType.NEW_BOOKING -> Triple(Icons.Outlined.EventAvailable, PrimaryColor, Color(0xFFF0F4F4))
            ActivityType.COMPLETED -> Triple(Icons.Outlined.CheckCircle, Color(0xFF4ADE80), Color(0xFFF0FDF4))
            ActivityType.CANCELED -> Triple(Icons.Outlined.Cancel, Color(0xFFF87171), Color(0xFFFEF2F2))
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp
                        ),
                        color = NeutralColor
                    )
                    Text(
                        text = activity.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFont),
                        color = Color.Gray
                    )
                    if (activity.relativeTime.isNotEmpty()) {
                        Text(
                            text = "• ${activity.relativeTime}",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont),
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = activity.timeLabel,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = InterFont,
                        fontWeight = if (activity.type == ActivityType.CANCELED) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (activity.type == ActivityType.CANCELED) Color(0xFFAA4444) else Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}


enum class BottomNavTab {
    Dashboard, Bookings, Availability, Links
}

@Composable
fun CustomBottomNavigation(
    selectedTab: BottomNavTab,
    onDashboard: () -> Unit,
    onAppointments: () -> Unit,
    onAvailability: () -> Unit,
    onLinks: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.shadow(elevation = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Outlined.GridView,
                label = "Dashboard",
                isSelected = selectedTab == BottomNavTab.Dashboard,
                onClick = onDashboard
            )
            BottomNavItem(
                icon = Icons.Outlined.EventAvailable,
                label = "Bookings",
                isSelected = selectedTab == BottomNavTab.Bookings,
                onClick = onAppointments
            )
            BottomNavItem(
                icon = Icons.Outlined.Schedule,
                label = "Availability",
                isSelected = selectedTab == BottomNavTab.Availability,
                onClick = onAvailability
            )
            BottomNavItem(
                icon = Icons.Outlined.Link,
                label = "Links",
                isSelected = selectedTab == BottomNavTab.Links,
                onClick = onLinks
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = InterFont,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}
