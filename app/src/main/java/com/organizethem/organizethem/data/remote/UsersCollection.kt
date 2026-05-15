package com.organizethem.organizethem.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.organizethem.organizethem.domain.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//Stroring users in firestore and not only in firebase auth
class UsersCollection @Inject constructor(
    private val firestore: FirebaseFirestore
){
    suspend fun createUser(user: User){
        firestore.collection("users")
            .document(user.uid)
            .set(mapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to user.displayName,
                "createdAt" to FieldValue.serverTimestamp()
            )).await()
    }

    suspend fun updateServerAuthCode(uid: String, code: String) {
        firestore.collection("users")
            .document(uid)
            .update("serverAuthCode", code)
            .await()
    }

    suspend fun getUser(uid: String): User?{
        val doc = firestore.collection("users").document(uid).get().await()
        return if (doc.exists()){
            User(
                uid = doc.getString("uid")?:"",
                email = doc.getString("email")?:"",
                displayName = doc.getString("displayName")?:""
            )
        }else null
    }
}