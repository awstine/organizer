package com.organizethem.organizethem.ui.screens.auth.signIn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.GoogleAuthProvider
import com.organizethem.organizethem.data.mapper.toUser
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.data.remote.UsersCollection
import com.organizethem.organizethem.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val usersCollection: UsersCollection
): ViewModel(){

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")
    var isSignIn by mutableStateOf(true)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var signedInUser by mutableStateOf<User?>(null)

    fun signIn(){
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                signedInUser = authDataSource.signIn(email,password)
            }catch (e: Exception){
                error = e.localizedMessage
            }finally {
                isLoading = false
            }
        }
    }

    fun onGoogleSignInResult(idToken: String?, serverAuthCode: String?) {
        if (idToken == null) {
            error = "Google Sign-In failed: No ID Token"
            return
        }
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val user = authDataSource.signInWithGoogle(credential)
                
                // Save the serverAuthCode for Calendar access
                if (serverAuthCode != null) {
                    usersCollection.updateServerAuthCode(user.uid, serverAuthCode)
                }
                
                signedInUser = user
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val user = authDataSource.signUp(email, password)
                // Update user with name if provided
                val userWithName = if (name.isNotBlank()) user.copy(displayName = name) else user
                usersCollection.createUser(userWithName)
                signedInUser = userWithName
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authDataSource.signOut()
            signedInUser = null
        }
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authDataSource.authStateFlow.collect { firebaseUser ->
                _authState.value = if (firebaseUser != null) {
                    AuthState.Authenticated(firebaseUser.toUser())
                } else {
                    AuthState.NotAuthenticated
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authDataSource.signOut()
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object NotAuthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}