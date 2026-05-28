package com.organizethem.organizethem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.organizethem.organizethem.ui.navigation.Screen
import com.organizethem.organizethem.ui.screens.AppNavGraph
import com.organizethem.organizethem.ui.screens.auth.signIn.AuthState
import com.organizethem.organizethem.ui.screens.auth.signIn.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var deepLinkState by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLinkState = extractDeepLink(intent)

        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()

            when (authState) {
                is AuthState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is AuthState.NotAuthenticated -> {
                    AppNavGraph(startDestination = Screen.SignIn.route)
                }
                is AuthState.Authenticated -> {
                    AppNavGraph(
                        startDestination = Screen.Home.route,
                        deepLink = deepLinkState
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        deepLinkState = extractDeepLink(intent)
    }

    private fun extractDeepLink(intent: Intent?): String? {
        if (intent?.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            // Handle both: organizing://book/abc123 and https://ecotrack-846b1.web.app/book/abc123
            return uri?.lastPathSegment
        }
        return null
    }
}