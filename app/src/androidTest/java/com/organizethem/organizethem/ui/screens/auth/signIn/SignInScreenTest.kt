package com.organizethem.organizethem.ui.screens.auth.signIn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SignInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingSignInButton_showsLoadingIndicator() {
        // We use a mutable state to simulate the loading trigger in the test
        var isLoading by mutableStateOf(false)

        composeTestRule.setContent {
            SignInContent(
                isSignIn = true,
                isLoading = isLoading,
                error = null,
                email = "",
                password = "",
                name = "",
                onEmailChange = {},
                onPasswordChange = {},
                onNameChange = {},
                onToggleMode = {},
                onMainActionClick = { /* No-op */ },
                onGoogleSignInClick = {
                    isLoading = true // Simulate starting loading
                }
            )
        }

        // 1. Act: Find the button by its text content and click it
        composeTestRule.onNodeWithText("Sign in with Google").performClick()

        // 2. Assert: Check if the UI updated and the loader shows
        // Note: I added .testTag("loading_indicator") to the CircularProgressIndicator in SignInScreen.kt
        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
    }
}