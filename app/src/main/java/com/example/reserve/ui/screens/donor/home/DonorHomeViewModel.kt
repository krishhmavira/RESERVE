package com.example.reserve.ui.screens.donor.home

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.repository.DonationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DonorHomeViewModel : ViewModel() {

    private val donationRepository = DonationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(DonorHomeUiState())
    val uiState: StateFlow<DonorHomeUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadDonations()
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    private fun loadDonations() {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        listenerRegistration?.remove()
        listenerRegistration = donationRepository.listenToDonorDonations(
            donorId = uid,
            onUpdate = { donations ->
                val active = donations.count { donation ->
                    donation.status == DonationStatus.AVAILABLE.name ||
                        donation.status == DonationStatus.REQUESTED.name ||
                        donation.status == DonationStatus.APPROVED.name
                }
                val completed = donations.count { donation ->
                    donation.status == DonationStatus.COMPLETED.name ||
                        donation.status == DonationStatus.COLLECTED.name
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalDonations = donations.size,
                        activeDonations = active,
                        completedDonations = completed,
                        recentDonations = donations.take(5),
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
