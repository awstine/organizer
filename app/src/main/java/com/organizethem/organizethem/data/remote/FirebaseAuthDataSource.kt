package com.organizethem.organizethem.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.organizethem.organizethem.data.mapper.toUser
import com.organizethem.organizethem.domain.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signUp(email: String, password: String): User {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return result.user!!.toUser()
    }

    suspend fun signIn(email: String, password: String): User {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user!!.toUser()
    }

    suspend fun signInWithGoogle(credential: com.google.firebase.auth.AuthCredential): User {
        val result = firebaseAuth.signInWithCredential(credential).await()
        return result.user!!.toUser()
    }

    suspend fun signOut() = firebaseAuth.signOut()

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }
}