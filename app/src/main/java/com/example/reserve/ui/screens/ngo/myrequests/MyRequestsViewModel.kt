package com.example.reserve.ui.screens.ngo.myrequests

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.Donation
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.repository.FoodRequestRepository
import com.example.reserve.data.util.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyRequestsViewModel : ViewModel() {

    private val foodRequestRepository = FoodRequestRepository()
    private val donationRepository = DonationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(MyRequestsUiState())
    val uiState: StateFlow<MyRequestsUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadRequests()
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    private fun loadRequests() {
        val uid = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        listenerRegistration?.remove()
        listenerRegistration = foodRequestRepository.listenToNgoRequests(
            ngoId = uid,
            onUpdate = { requests ->
                _uiState.update {
                    it.copy(isLoading = false, requests = requests, errorMessage = null)
                }
                fetchDonationNames(requests.map { it.donationId }.distinct())
            },
            onError = { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = FirebaseHelper.mapFirebaseError(error)) }
            }
        )
    }

    private fun fetchDonationNames(donationIds: List<String>) {
        donationIds.forEach { donationId ->
            if (_uiState.value.donationNames.containsKey(donationId)) return@forEach
            donationRepository.getDonation(
                donationId = donationId,
                onSuccess = { donation ->
                    if (donation != null) {
                        _uiState.update {
                            it.copy(donationNames = it.donationNames + (donationId to donation.foodName))
                        }
                    }
                },
                onFailure = { }
            )
        }
    }

    fun retry() {
        loadRequests()
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
