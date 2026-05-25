package com.organizethem.organizethem.ui.screens.manageLinks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.BookingLink
import com.organizethem.organizethem.domain.BookingLinksCollection
import com.organizethem.organizethem.ui.screens.home.BottomNavItem // Reusing your existing nav component
import com.organizethem.organizethem.ui.screens.home.CustomBottomNavigation
import com.organizethem.organizethem.ui.screens.home.BottomNavTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- Design System Colors & Fonts ---
val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)
val LightSurfaceColor = Color(0xFFFAFAFA)
val ActiveIconBg = Color(0xFFE0F7FA) // Light blue for video call icon
val ActiveIconColor = Color(0xFF006064)

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLinksScreen(
    viewModel: ManageLinksViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToAvailability: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = LightSurfaceColor,
        bottomBar = {
            // Bottom Nav with "Links" selected
                CustomBottomNavigation(
                    selectedTab = BottomNavTab.Links,
                    onDashboard = onNavigateToDashboard,
                    onAppointments = onNavigateToAppointments,
                    onAvailability = onNavigateToAvailability,
                    onLinks = {}
                )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Top App Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryColor,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Manage Links",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = ManropeFont,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Profile Image Placeholder
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp),
                        color = PrimaryColor
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Main Content Area ---
            if (viewModel.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else if (viewModel.links.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SecondaryColor,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LinkOff,
                            contentDescription = "No Links",
                            tint = Color.LightGray,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No links created yet",
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
                        color = NeutralColor
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp)
                ) {
                    items(viewModel.links) { link ->
                        // Mocking isActive for UI demonstration.
                        // You will need to add an `isActive` boolean to your BookingLink domain model.
                        var isActive by remember { mutableStateOf(true) }

                        LinkCard(
                            link = link,
                            isActive = isActive,
                            onToggleActive = { isActive = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- Bottom Info State ---
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SecondaryColor,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LinkOff,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Only active links can be booked by clients.",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFont),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LinkCard(
    link: BookingLink,
    isActive: Boolean,
    onToggleActive: (Boolean) -> Unit
) {
    // Styling adjustments based on active state
    val cardAlpha = if (isActive) 1f else 0.6f
    val iconBgColor = if (isActive) ActiveIconBg else SecondaryColor
    val iconColor = if (isActive) ActiveIconColor else Color.Gray

    // Determine Icon based on title/type (Mock logic, adjust to your data)
    val displayIcon = when {
        link.title.contains("Discovery", ignoreCase = true) -> Icons.Outlined.Videocam
        link.title.contains("Coffee", ignoreCase = true) -> Icons.Outlined.LocalCafe
        else -> Icons.Outlined.Person
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .shadow(
                elevation = if (isActive) 8.dp else 2.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // --- Row 1: Icon, Title, Switch ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = iconBgColor,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = displayIcon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = link.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
                            color = NeutralColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${link.duration} mins",
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont),
                                color = Color.Gray
                            )
                        }
                    }
                }

                Switch(
                    checked = isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryColor,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE0E0E0),
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.scale(0.85f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Row 2: Link URL Box ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SecondaryColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val slug = link.title.lowercase().replace(" ", "-").take(8)
                    Text(
                        text = "organize.com/alexm/$slug...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Copy Action */ }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy",
                            tint = PrimaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Copy",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                            color = PrimaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Row 3: Action Buttons ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable { /* Edit Action */ },
                    shape = RoundedCornerShape(22.dp),
                    color = SecondaryColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit Link",
                            style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                            color = PrimaryColor
                        )
                    }
                }

                // More Options Button
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { /* More Actions */ },
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = "More Options",
                        tint = Color.Gray,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}