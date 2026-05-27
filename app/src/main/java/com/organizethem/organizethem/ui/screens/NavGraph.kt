package com.organizethem.organizethem.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.organizethem.organizethem.ui.navigation.Screen
import com.organizethem.organizethem.ui.screens.auth.signIn.SignInScreen
import com.organizethem.organizethem.ui.screens.booker.BookerCalendarScreen
import com.organizethem.organizethem.ui.screens.booker.BookingConfirmationScreen
import com.organizethem.organizethem.ui.screens.createLink.CreateLinkScreen
import com.organizethem.organizethem.ui.screens.home.HomeScreen
import com.organizethem.organizethem.ui.screens.manageLinks.ManageLinksScreen
import com.organizethem.organizethem.ui.screens.myAppointment.MyAppointmentsScreen
import com.organizethem.organizethem.ui.screens.setAvailability.SetAvailabilityScreen

@Composable
fun AppNavGraph(
    startDestination: String,
    deepLink: String? = null
) {
    val navController = rememberNavController()

    // Handle deep link
    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            // Check if we are already on the calendar for this link to avoid loops
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val targetRoute = Screen.BookerCalendar.createRoute(deepLink)
            if (currentRoute != targetRoute) {
                navController.navigate(targetRoute)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignedIn = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDashboard = {
                    // Already on Dashboard
                },
                onNavigateToCreateLink = {
                    navController.navigate(Screen.CreateLink.route)
                },
                onNavigateToAvailability = {
                    navController.navigate(Screen.SetAvailability.route)
                },
                onNavigateToAppointments = {
                    navController.navigate(Screen.MyAppointments.route)
                },
                onNavigateToManageLinks = {
                    navController.navigate(Screen.ManageLinks.route)
                }
            )
        }

        composable(Screen.ManageLinks.route) {
            ManageLinksScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToAppointments = {
                    navController.navigate(Screen.MyAppointments.route)
                },
                onNavigateToAvailability = {
                    navController.navigate(Screen.SetAvailability.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SetAvailability.route) {
            SetAvailabilityScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToAppointments = {
                    navController.navigate(Screen.MyAppointments.route)
                },
                onNavigateToLinks = {
                    navController.navigate(Screen.ManageLinks.route)
                },
                onSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CreateLink.route) {
            CreateLinkScreen(
                onLinkCreated = { linkId ->
                    navController.popBackStack()
                    // Could navigate to share screen
                }
            )
        }

        composable(Screen.MyAppointments.route) {
            MyAppointmentsScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToAvailability = {
                    navController.navigate(Screen.SetAvailability.route)
                },
                onNavigateToLinks = {
                    navController.navigate(Screen.ManageLinks.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.BookerCalendar.route,
            arguments = listOf(navArgument("linkId") { type = NavType.StringType })
        ) { backStackEntry ->
            val linkId = backStackEntry.arguments?.getString("linkId") ?: return@composable

            BookerCalendarScreen(
                linkId = linkId,
                onTimeSlotSelected = { date, time, duration, type ->
                    navController.navigate(
                        Screen.BookingConfirmation.createRoute(linkId, date, time, duration, type)
                    )
                }
            )
        }

        composable(
            route = Screen.BookingConfirmation.route,
            arguments = listOf(
                navArgument("linkId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("duration") { type = NavType.IntType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val linkId = backStackEntry.arguments?.getString("linkId") ?: return@composable
            val date = backStackEntry.arguments?.getString("date") ?: return@composable
            val time = backStackEntry.arguments?.getString("time") ?: return@composable
            val duration = backStackEntry.arguments?.getInt("duration") ?: 30
            val type = backStackEntry.arguments?.getString("type") ?: "online"

            BookingConfirmationScreen(
                linkId = linkId,
                selectedDate = date,
                selectedTime = time,
                duration = duration,
                meetingType = type,
                onBookingComplete = {
                    navController.popBackStack(Screen.BookerCalendar.createRoute(linkId), true)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}