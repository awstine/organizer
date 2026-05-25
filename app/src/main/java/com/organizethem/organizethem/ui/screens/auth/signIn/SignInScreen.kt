package com.organizethem.organizethem.ui.screens.auth.signIn

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

val PrimaryColor = Color(0xFF334D4D)
val SecondaryColor = Color(0xFFF5F5F5)
val NeutralColor = Color(0xFF1E1E1E)

val ManropeFont = FontFamily.Default
val InterFont = FontFamily.Default

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onSignedIn: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val webClientId = "110585987108-l89q93i338ss3qfa4n27c41m6ahlhjmd.apps.googleusercontent.com"

    val credentialManager = remember { CredentialManager.create(context) }
    val authorizationClient = remember { Identity.getAuthorizationClient(context) }

    var pendingIdToken by remember { mutableStateOf<String?>(null) }

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val authResult = authorizationClient.getAuthorizationResultFromIntent(result.data)
            viewModel.onGoogleSignInResult(pendingIdToken, authResult.serverAuthCode)
            pendingIdToken = null
        } else {
            viewModel.onGoogleSignInResult(pendingIdToken, null)
            pendingIdToken = null
        }
    }

    LaunchedEffect(viewModel.signedInUser) {
        if (viewModel.signedInUser != null) onSignedIn()
    }

    SignInContent(
        isSignIn = viewModel.isSignIn,
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        email = viewModel.email,
        password = viewModel.password,
        name = viewModel.name,
        onEmailChange = { viewModel.email = it },
        onPasswordChange = { viewModel.password = it },
        onNameChange = { viewModel.name = it },
        onToggleMode = {
            viewModel.isSignIn = !viewModel.isSignIn
            viewModel.error = null
        },
        onMainActionClick = { if (viewModel.isSignIn) viewModel.signIn() else viewModel.signUp() },
        onGoogleSignInClick = {
            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )
                    val credential = result.credential

                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        val calendarScope = Scope("https://www.googleapis.com/auth/calendar")
                        val authRequest = AuthorizationRequest.builder()
                            .setRequestedScopes(listOf(calendarScope))
                            .requestOfflineAccess(webClientId)
                            .build()

                        authorizationClient.authorize(authRequest)
                            .addOnSuccessListener { authResult ->
                                if (authResult.hasResolution()) {
                                    pendingIdToken = idToken
                                    val intentSenderRequest = IntentSenderRequest.Builder(authResult.pendingIntent!!.intentSender).build()
                                    authLauncher.launch(intentSenderRequest)
                                } else {
                                    viewModel.onGoogleSignInResult(idToken, authResult.serverAuthCode)
                                }
                            }
                            .addOnFailureListener {
                                viewModel.onGoogleSignInResult(idToken, null)
                            }
                    }
                } catch (e: GetCredentialException) {
                    viewModel.error = "Google Sign-In failed: ${e.message}"
                } catch (e: Exception) {
                    viewModel.error = "An error occurred: ${e.localizedMessage}"
                }
            }
        }
    )
}

@Composable
fun SignInContent(
    isSignIn: Boolean,
    isLoading: Boolean,
    error: String?,
    email: String,
    password: String,
    name: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onMainActionClick: () -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Top Header Area (Logo & Toggle) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Organize",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = ManropeFont,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryColor,
                        fontSize = 22.sp
                    )
                )

                if (isSignIn) {
                    Row {
                        Text("New user? ", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, color = Color.Gray))
                        Text(
                            text = "Sign up",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, color = PrimaryColor),
                            modifier = Modifier.clickable { onToggleMode() }
                        )
                    }
                } else {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, fontWeight = FontWeight.Bold, color = PrimaryColor),
                        modifier = Modifier.clickable { onToggleMode() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- Main Titles ---
            Text(
                text = if (isSignIn) "Sign In" else "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = ManropeFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = NeutralColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isSignIn)
                    "Access your premium productivity\nenvironment."
                else
                    "Start organizing your professional life with a\nsense of calm and order.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont),
                color = Color.Gray,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Google Button ---
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = NeutralColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 0.dp
                ),
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Placeholder for Google Logo - replace with actual Google Icon drawable if preferred
                    Text(
                        text = "G",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFDB4437),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign in with Google",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Bold,
                            color = NeutralColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Divider ---
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                Text(
                    text = "OR EMAIL",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = InterFont),
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Text Fields ---
            if (!isSignIn) {
                AuthTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "Full Name",
                    placeholder = "Alex Morgan"
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = if (isSignIn) "Email Address" else "Work Email",
                placeholder = if (isSignIn) "name@company.com" else "alex.morgan@pro.com"
            )

            Spacer(modifier = Modifier.height(20.dp))

            var passwordVisible by remember { mutableStateOf(false) }
            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = if (isSignIn) "••••••••" else "Min. 8 characters",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                trailingLabel = if (isSignIn) {
                    {
                        Text(
                            text = "Forgot?",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = InterFont, color = PrimaryColor),
                            modifier = Modifier.clickable { /* Handle forgot password */ }
                        )
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Checkbox Row ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                var agreed by remember { mutableStateOf(false) }
                Checkbox(
                    checked = agreed,
                    onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryColor,
                        uncheckedColor = Color.LightGray
                    ),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                if (isSignIn) {
                    Text(
                        text = "Keep me signed in for 30 days",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, color = NeutralColor)
                    )
                } else {
                    val annotatedString = buildAnnotatedString {
                        append("I agree to the ")
                        withStyle(style = SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold)) {
                            append("Terms of Service")
                        }
                        append(" and ")
                        withStyle(style = SpanStyle(color = PrimaryColor, fontWeight = FontWeight.Bold)) {
                            append("Privacy Policy.")
                        }
                    }
                    Text(text = annotatedString, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = InterFont, color = NeutralColor))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Main Action Button ---
            Button(
                onClick = onMainActionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("loading_indicator")
                    )
                } else {
                    Text(
                        text = if (isSignIn) "Sign In" else "Create Account",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            }

            // --- Footer (Sign In mode only) ---
            if (isSignIn) {
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(32.dp))

                val footerText = buildAnnotatedString {
                    append("By continuing, you agree to Organize's ")
                    withStyle(style = SpanStyle(color = PrimaryColor, fontWeight = FontWeight.SemiBold)) {
                        append("Terms of\nService")
                    }
                    append(" and ")
                    withStyle(style = SpanStyle(color = PrimaryColor, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy.")
                    }
                }

                Text(
                    text = footerText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = InterFont, color = Color.Gray),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    trailingLabel: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Bold,
                    color = NeutralColor
                )
            )
            trailingLabel?.invoke()
        }

        // Input Field
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp)), // Heavy rounding for pill shape
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = InterFont)
                )
            },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = onPasswordToggle) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = Color.Gray)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SecondaryColor,
                unfocusedContainerColor = SecondaryColor,
                disabledContainerColor = SecondaryColor,
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
