package com.organizethem.organizethem.ui.screens.setAvailability

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.AvailabilityCollection
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.DaySchedule
import com.organizethem.organizethem.domain.WeeklyAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun SetAvailabilityScreen(
    viewModel: SetAvailabilityViewModel = hiltViewModel(),
    onSaved: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Section
            Text(
                text = "Availability",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Set your standard working hours",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Days List
            listOf(
                "Monday" to viewModel.monday,
                "Tuesday" to viewModel.tuesday,
                "Wednesday" to viewModel.wednesday,
                "Thursday" to viewModel.thursday,
                "Friday" to viewModel.friday,
                "Saturday" to viewModel.saturday,
                "Sunday" to viewModel.sunday
            ).forEach { (dayName, schedule) ->
                DayScheduleCard(
                    dayName = dayName,
                    schedule = schedule,
                    onScheduleChange = { updatedSchedule ->
                        viewModel.updateDay(dayName.lowercase(), updatedSchedule)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Save Button
            Button(
                onClick = {
                    viewModel.save()
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF334D4D)
                )
            ) {
                Text(
                    text = "Save Availability",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DayScheduleCard(
    dayName: String,
    schedule: DayScheduleState,
    onScheduleChange: (DayScheduleState) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (schedule.enabled) Color(0xFF1E1E1E) else Color.Gray
                )
                Switch(
                    checked = schedule.enabled,
                    onCheckedChange = { checked ->
                        onScheduleChange(schedule.copy(enabled = checked))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF334D4D), // Brand Color
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE0E0E0),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            if (schedule.enabled) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeChip(
                        time = String.format("%02d:%02d", schedule.startHour, schedule.startMinute),
                        modifier = Modifier.weight(1f),
                        onClick = { /* Open Time Picker */ }
                    )

                    Text(
                        text = "to",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    TimeChip(
                        time = String.format("%02d:%02d", schedule.endHour, schedule.endMinute),
                        modifier = Modifier.weight(1f),
                        onClick = { /* Open Time Picker */ }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeChip(
    time: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF334D4D)
            )
        }
    }
}

data class DayScheduleState(
    val enabled: Boolean = false,
    val startHour: Int = 9,
    val startMinute: Int = 0,
    val endHour: Int = 17,
    val endMinute: Int = 0
)

@HiltViewModel
class SetAvailabilityViewModel @Inject constructor(
    private val availabilityCollection: AvailabilityCollection,
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {
    var monday by mutableStateOf(DayScheduleState())
    var tuesday by mutableStateOf(DayScheduleState())
    var wednesday by mutableStateOf(DayScheduleState())
    var thursday by mutableStateOf(DayScheduleState())
    var friday by mutableStateOf(DayScheduleState())
    var saturday by mutableStateOf(DayScheduleState())
    var sunday by mutableStateOf(DayScheduleState())

    init {
        loadAvailability()
    }

    private fun loadAvailability() {
        viewModelScope.launch {
            val userId = authDataSource.getCurrentUser()?.uid ?: return@launch
            val availability = availabilityCollection.getAvailability(userId)

            monday = availability.monday.toState()
            tuesday = availability.tuesday.toState()
            wednesday = availability.wednesday.toState()
            thursday = availability.thursday.toState()
            friday = availability.friday.toState()
            saturday = availability.saturday.toState()
            sunday = availability.sunday.toState()
        }
    }

    fun updateDay(day: String, schedule: DayScheduleState) {
        when (day) {
            "monday" -> monday = schedule
            "tuesday" -> tuesday = schedule
            "wednesday" -> wednesday = schedule
            "thursday" -> thursday = schedule
            "friday" -> friday = schedule
            "saturday" -> saturday = schedule
            "sunday" -> sunday = schedule
        }
    }

    fun save() {
        viewModelScope.launch {
            val userId = authDataSource.getCurrentUser()?.uid ?: return@launch
            val availability = WeeklyAvailability(
                monday = monday.toDomain(),
                tuesday = tuesday.toDomain(),
                wednesday = wednesday.toDomain(),
                thursday = thursday.toDomain(),
                friday = friday.toDomain(),
                saturday = saturday.toDomain(),
                sunday = sunday.toDomain()
            )
            availabilityCollection.saveAvailability(userId, availability)
        }
    }

    private fun DaySchedule.toState() = DayScheduleState(enabled, startHour, startMinute, endHour, endMinute)
    private fun DayScheduleState.toDomain() = DaySchedule(enabled, startHour, startMinute, endHour, endMinute)
}