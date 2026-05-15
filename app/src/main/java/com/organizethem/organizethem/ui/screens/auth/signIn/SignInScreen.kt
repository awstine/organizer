package com.organizethem.organizethem.ui.screens.auth.signIn

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (isSignIn) "Sign In" else "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSignIn) 
                    "Hi! Welcome back, you've been missed" 
                else 
                    "Fill your information below or register with your social accounts",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!isSignIn) {
                AuthTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "Name",
                    placeholder = "John Doe"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "johndoe@gmail.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            var passwordVisible by remember { mutableStateOf(false) }
            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "************",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible }
            )

            if (isSignIn) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334D4D)
                    ),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .clickable { /* Handle forgot password */ }
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    var agreed by remember { mutableStateOf(false) }
                    Checkbox(
                        checked = agreed,
                        onCheckedChange = { agreed = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF334D4D))
                    )
                    val annotatedString = buildAnnotatedString {
                        append("Agree with ")
                        withStyle(style = SpanStyle(color = Color(0xFF334D4D), fontWeight = FontWeight.Bold)) {
                            append("Terms & Condition")
                        }
                    }
                    Text(text = annotatedString, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onMainActionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334D4D)),
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
                        text = if (isSignIn) "Sign In" else "Sign Up",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                Text(
                    text = " Or ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F5),
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFDB4437),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isSignIn) "Sign in with Google" else "Sign up with Google",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E1E1E)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val footerText = buildAnnotatedString {
                append(if (isSignIn) "Don't have an account? " else "Already have an account? ")
                withStyle(style = SpanStyle(color = Color(0xFF334D4D), fontWeight = FontWeight.Bold)) {
                    append(if (isSignIn) "Sign Up" else "Sign In")
                }
            }
            
            Text(
                text = footerText,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clickable { onToggleMode() },
                style = MaterialTheme.typography.bodyMedium
            )
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
    onPasswordToggle: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1E1E1E),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = onPasswordToggle) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                disabledContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}
