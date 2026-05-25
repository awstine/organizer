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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
val LightSurfaceColor = Color(0xFFFAFAFA)
val BrandMint = Color(0xFFE0F9F1)

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLinkScreen(
    viewModel: CreateLinkViewModel = hiltViewModel(),
    onLinkCreated: (String) -> Unit,
    onClose: () -> Unit = {}
) {
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        containerColor = LightSurfaceColor,
        bottomBar = {
            // Sticky Bottom Bar for Generate & Share
            Surface(
                color = LightSurfaceColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        viewModel.createLink()
                        // In a real flow, you'd wait for creation, then share/navigate
                    },
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
                .verticalScroll(rememberScrollState())
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

                // Profile Image Placeholder
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp),
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

            Spacer(modifier = Modifier.height(24.dp))

            // --- Hero Image Section ---
            // Note: Replace the background color with an Image() using ContentScale.Crop if you have the asset.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF234438)) // Placeholder for the green plant image
            ) {
                // Gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 100f
                            )
                        )
                )

                Text(
                    text = "Let's create something simple.",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Location / Platform Dropdown Mock
                Text(
                    text = "Location/Platform",
                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, color = Color.Gray)
                )
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
                        Icon(imageVector = Icons.Outlined.ExpandMore, contentDescription = "Select", tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Duration Section ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SectionHeader(icon = Icons.Outlined.Schedule, title = "Duration")
                Spacer(modifier = Modifier.height(16.dp))

                // Segmented Control for Duration
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = SecondaryColor,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(15, 30, 60).forEach { mins ->
                            val isSelected = viewModel.duration == mins
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (isSelected) PrimaryColor else Color.Transparent)
                                    .clickable { viewModel.duration = mins },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mins}m",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = InterFont,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) Color.White else PrimaryColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Description Section ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Description, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Description ",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = ManropeFont, fontWeight = FontWeight.Bold),
                        color = PrimaryColor
                    )
                    Text(
                        text = "(Optional)",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = ManropeFont),
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)),
                    placeholder = {
                        Text(
                            "Tell your guests what this meeting is about...",
                            color = Color.Gray.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SecondaryColor,
                        unfocusedContainerColor = SecondaryColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Link Preview Card ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = BrandMint,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Outlined.Link, contentDescription = null, tint = PrimaryColor, modifier = Modifier.padding(10.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Link Preview", style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont), color = Color.Gray)

                                // Dynamic URL preview based on the title
                                val slug = viewModel.title.lowercase().replace(" ", "-").ifEmpty { "discovery" }
                                Text(
                                    text = "organize.com/alexm/$slug",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.SemiBold),
                                    color = PrimaryColor,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Copy Link",
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold),
                                color = PrimaryColor,
                                modifier = Modifier.clickable { /* Handle Copy */ }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp)) // Extra space for the sticky bottom bar
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
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
            placeholder = {
                Text(
                    placeholder,
                    color = Color.Gray.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = InterFont)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SecondaryColor,
                unfocusedContainerColor = SecondaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = NeutralColor,
                unfocusedTextColor = NeutralColor
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = InterFont)
        )
    }
}
