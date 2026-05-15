package com.organizethem.organizethem.ui.navigation

sealed class Screen(val route: String) {
    object SignIn : Screen("signin")
    object Home : Screen("home")
    object SetAvailability : Screen("set_availability")
    object CreateLink : Screen("create_link")
    object MyAppointments : Screen("my_appointments")
    object ManageLinks : Screen("manage_links")

    // Booker screens with arguments
    object BookerCalendar : Screen("book/{linkId}") {
        fun createRoute(linkId: String) = "book/$linkId"
    }
    object BookingConfirmation : Screen("book/{linkId}/confirm?date={date}&time={time}&duration={duration}") {
        fun createRoute(linkId: String, date: String, time: String, duration: Int) =
            "book/$linkId/confirm?date=$date&time=$time&duration=$duration"
    }
}