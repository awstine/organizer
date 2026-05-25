package com.organizethem.organizethem.ui.screens.setAvailability

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.AvailabilityCollection
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.DaySchedule
import com.organizethem.organizethem.domain.WeeklyAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


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

data class DayScheduleState(
    val enabled: Boolean = false,
    val startHour: Int = 9,
    val startMinute: Int = 0,
    val endHour: Int = 17,
    val endMinute: Int = 0
)