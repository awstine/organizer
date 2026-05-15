package com.organizethem.organizethem.ui.screens.createLink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.BookingLink
import com.organizethem.organizethem.domain.BookingLinksCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class CreateLinkViewModel @Inject constructor(
    private val bookingLinksCollection: BookingLinksCollection,
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var duration by mutableIntStateOf(30)
    var createdLinkId by mutableStateOf<String?>(null)

    fun createLink() {
        viewModelScope.launch {
            val linkId = UUID.randomUUID().toString().take(8)
            val userId = authDataSource.getCurrentUser()?.uid ?: return@launch

            val link = BookingLink(
                linkId = linkId,
                ownerId = userId,
                title = title,
                duration = duration,
                description = description
            )

            bookingLinksCollection.createLink(link)
            createdLinkId = linkId
        }
    }
}