package com.organizethem.organizethem.ui.screens.createLink

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.ui.components.TimeBox
import com.organizethem.organizethem.ui.components.TimePickerWrapper


val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)
val LightSurfaceColor = Color(0xFFFAFAFA)

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLinkScreen(
    viewModel: CreateLinkViewModel = hiltViewModel(),
    onLinkCreated: (String) -> Unit,
    onClose: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    // Trigger Share Sheet when link is created
    LaunchedEffect(viewModel.createdLinkId) {
        viewModel.createdLinkId?.let { linkId ->
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Schedule a meeting with me: https://ecotrack-846b1.web.app/book/$linkId")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Booking Link"))
            onLinkCreated(linkId)
            viewModel.createdLinkId = null // Reset state
        }
    }
    
    // Time Picker UI States
    var showTimePicker by remember { mutableStateOf(false) }
    var pickingFor by remember { mutableStateOf("") } // "start", "end", "meetup", "meetup_end"

    Scaffold(
        containerColor = LightSurfaceColor,
        bottomBar = {
            Surface(
                color = LightSurfaceColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.createLink() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    enabled = viewModel.title.isNotBlank()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Generate & Share",
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(18.dp))
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
            Spacer(modifier = Modifier.height(16.dp))

            // --- Top App Bar ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp).clickable { onClose() }
                )

                Text(
                    text = "New Link",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                )

                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp),
                    color = SecondaryColor
                ) {
                    Icon(imageVector = Icons.Outlined.Person, contentDescription = "Profile", tint = PrimaryColor, modifier = Modifier.padding(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Hero Image Section ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF234438))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
                )

                Text(
                    text = "Let's create something simple.",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Meeting Details Section ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SectionHeader(icon = Icons.Outlined.Info, title = "Meeting Details")
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
                    label = "Meeting Name",
                    placeholder = "e.g., Discovery Call"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- Location / Platform Cards ---
                Text(
                    text = "Location/Platform",
                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, color = Color.Gray),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LocationTypeCard(
                        title = "Online",
                        icon = Icons.Outlined.Videocam,
                        isSelected = viewModel.meetingType == "online",
                        onClick = { viewModel.meetingType = "online" },
                        modifier = Modifier.weight(1f)
                    )
                    LocationTypeCard(
                        title = "In-person",
                        icon = Icons.Outlined.LocationOn,
                        isSelected = viewModel.meetingType == "in-person",
                        onClick = { viewModel.meetingType = "in-person" },
                        modifier = Modifier.weight(1f)
                    )
                    LocationTypeCard(
                        title = "Flexible",
                        icon = Icons.Outlined.SyncAlt,
                        isSelected = viewModel.meetingType == "both",
                        onClick = { viewModel.meetingType = "both" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Conditional Content ---
                if (viewModel.meetingType == "online" || viewModel.meetingType == "both") {

                    Spacer(modifier = Modifier.height(24.dp))

                    // Time Window Section
                    SectionHeader(icon = Icons.Outlined.Timer, title = "Availability Window")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val calculatedDuration = (viewModel.endHour * 60 + viewModel.endMinute) - (viewModel.startHour * 60 + viewModel.startMinute)
                    Text(
                        text = "Meeting Duration: ${if (calculatedDuration > 0) calculatedDuration else 0} minutes",
                        style = MaterialTheme.typography.labelMedium.copy(color = PrimaryColor, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TimeBox(hour = viewModel.startHour, minute = viewModel.startMinute, onClick = { pickingFor = "start"; showTimePicker = true })
                        Text(" — ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
                        TimeBox(hour = viewModel.endHour, minute = viewModel.endMinute, onClick = { pickingFor = "end"; showTimePicker = true })
                    }
                } else {
                    // In-person Details
                    CustomTextField(
                        value = viewModel.location,
                        onValueChange = { viewModel.location = it },
                        label = "Meetup Address",
                        placeholder = "e.g. 123 Business St, Studio A"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(icon = Icons.Outlined.WatchLater, title = "Meetup Window")
                    Spacer(modifier = Modifier.height(8.dp))

                    val calculatedDuration = (viewModel.meetupEndHour * 60 + viewModel.meetupEndMinute) - (viewModel.meetupHour * 60 + viewModel.meetupMinute)
                    Text(
                        text = "Meeting Duration: ${if (calculatedDuration > 0) calculatedDuration else 0} minutes",
                        style = MaterialTheme.typography.labelMedium.copy(color = PrimaryColor, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TimeBox(hour = viewModel.meetupHour, minute = viewModel.meetupMinute, onClick = { pickingFor = "meetup"; showTimePicker = true })
                        Text(" — ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
                        TimeBox(hour = viewModel.meetupEndHour, minute = viewModel.meetupEndMinute, onClick = { pickingFor = "meetup_end"; showTimePicker = true })
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Description ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SectionHeader(icon = Icons.Outlined.Description, title = "Description (Optional)")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)),
                    placeholder = { Text("Tell your guests what this meeting is about...", color = Color.Gray.copy(alpha = 0.5f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SecondaryColor,
                        unfocusedContainerColor = SecondaryColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showTimePicker) {
        TimePickerWrapper(
            initialHour = when(pickingFor) {
                "start" -> viewModel.startHour
                "end" -> viewModel.endHour
                "meetup" -> viewModel.meetupHour
                else -> viewModel.meetupEndHour
            },
            initialMinute = when(pickingFor) {
                "start" -> viewModel.startMinute
                "end" -> viewModel.endMinute
                "meetup" -> viewModel.meetupMinute
                else -> viewModel.meetupEndMinute
            },
            onTimeSelected = { h, m ->
                when(pickingFor) {
                    "start" -> { viewModel.startHour = h; viewModel.startMinute = m }
                    "end" -> { viewModel.endHour = h; viewModel.endMinute = m }
                    "meetup" -> { viewModel.meetupHour = h; viewModel.meetupMinute = m }
                    "meetup_end" -> { viewModel.meetupEndHour = h; viewModel.meetupEndMinute = m }
                }
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
fun LocationTypeCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PrimaryColor else SecondaryColor,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else PrimaryColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.White else PrimaryColor
            )
        }
    }
}

@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
            color = PrimaryColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, color = Color.Gray),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)),
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SecondaryColor,
                unfocusedContainerColor = SecondaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}
