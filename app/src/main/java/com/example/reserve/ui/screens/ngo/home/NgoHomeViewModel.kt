package com.example.reserve.ui.screens.ngo.home

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.repository.FoodRequestRepository
import com.example.reserve.data.util.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NgoHomeViewModel : ViewModel() {

    private val donationRepository = DonationRepository()
    private val foodRequestRepository = FoodRequestRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(NgoHomeUiState())
    val uiState: StateFlow<NgoHomeUiState> = _uiState.asStateFlow()

    private var donationsListener: ListenerRegistration? = null
    private var requestsListener: ListenerRegistration? = null

    init {
        loadData()
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadAvailableDonations()
        loadMyRequestStats()
    }

    private fun loadAvailableDonations() {
        donationsListener?.remove()
        donationsListener = donationRepository.listenToAvailableDonations(
            onUpdate = { donations ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        availableCount = donations.size,
                        recentAvailable = donations.take(5),
                        errorMessage = null
                    )
                }
            },
            onError = { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    private fun loadMyRequestStats() {
        val uid = auth.currentUser?.uid ?: return
        requestsListener?.remove()
        requestsListener = foodRequestRepository.listenToNgoRequests(
            ngoId = uid,
            onUpdate = { requests ->
                val totalRequests = requests.size
                val approved = requests.count { request ->
                    request.status == RequestStatus.APPROVED.name ||
                        request.status == RequestStatus.COMPLETED.name
                }
                _uiState.update {
                    it.copy(myRequestsCount = totalRequests, approvedCount = approved)
                }
            },
            onError = { }
        )
    }

    fun retry() {
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        donationsListener?.remove()
        requestsListener?.remove()
    }
}
