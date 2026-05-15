package com.organizethem.organizethem.domain

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookingLinksCollection @Inject constructor(
    private val firestore: FirebaseFirestore
){
    suspend fun createLink(link: BookingLink){
        firestore.collection("booking_links")
            .document(link.linkId)
            .set(mapOf(
                "linkId" to link.linkId,
                "ownerId" to link.ownerId,
                "title" to link.title,
                "duration" to link.duration,
                "description" to link.description,
                "active" to link.active,
                "createdAt" to FieldValue.serverTimestamp()//uses server time
            ))
            .await()
    }

    suspend fun getLink(linkId: String): BookingLink? {
        val doc = firestore.collection("booking_links")
            .document(linkId)
            .get()
            .await()

        return if (doc.exists()){
            BookingLink(
                linkId = doc.getString("linkId") ?: "",
                ownerId = doc.getString("ownerId") ?: "",
                title = doc.getString("title") ?: "",
                duration = (doc.getLong("duration")?.toInt() ?: 30),
                description = doc.getString("description") ?: "",
                active = doc.getBoolean("active") ?: true
            )
        }else null
    }
    suspend fun getOwnerLinks(ownerId: String): List<BookingLink> {
        return firestore.collection("booking_links")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
            .documents//returns lists of all documents
            .mapNotNull { doc ->
                BookingLink(
                    linkId = doc.getString("linkId") ?: "",
                    ownerId = doc.getString("ownerId") ?: "",
                    title = doc.getString("title") ?: "",
                    duration = (doc.getLong("duration")?.toInt() ?: 30),
                    description = doc.getString("description") ?: "",
                    active = doc.getBoolean("active") ?: true
                )
            }
    }
}
