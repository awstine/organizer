package com.organizethem.organizethem.ui.screens.manageLinks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.organizethem.organizethem.data.remote.FirebaseAuthDataSource
import com.organizethem.organizethem.domain.BookingLink
import com.organizethem.organizethem.domain.BookingLinksCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageLinksViewModel @Inject constructor(
    private val bookingLinksCollection: BookingLinksCollection,
    private val authDataSource: FirebaseAuthDataSource
) : ViewModel() {
    var links by mutableStateOf<List<BookingLink>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        loadLinks()
    }

    private fun loadLinks() {
        viewModelScope.launch {
            val userId = authDataSource.getCurrentUser()?.uid ?: return@launch
            links = bookingLinksCollection.getOwnerLinks(userId)
            isLoading = false
        }
    }
}