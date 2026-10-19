package com.example.reserve.ui.screens.donor.mydonations

import androidx.lifecycle.ViewModel
import com.example.reserve.data.repository.DonationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyDonationsViewModel : ViewModel() {

    private val donationRepository = DonationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(MyDonationsUiState())
    val uiState: StateFlow<MyDonationsUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadDonations()
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    private fun loadDonations() {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        listenerRegistration?.remove()
        listenerRegistration = donationRepository.listenToDonorDonations(
            donorId = uid,
            onUpdate = { donations ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        donations = donations,
                        errorMessage = null
                    )
                }
            },
            onError = { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            }
        )
    }

    fun retry() {
        loadDonations()
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
