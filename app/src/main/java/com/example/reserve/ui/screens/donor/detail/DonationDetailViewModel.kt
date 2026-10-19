package com.example.reserve.ui.screens.donor.detail

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.FoodRequest
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.repository.FoodRequestRepository
import com.example.reserve.data.util.FirebaseHelper
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DonationDetailViewModel : ViewModel() {

    private val donationRepository = DonationRepository()
    private val foodRequestRepository = FoodRequestRepository()

    private val _uiState = MutableStateFlow(DonationDetailUiState())
    val uiState: StateFlow<DonationDetailUiState> = _uiState.asStateFlow()

    private var donationListener: ListenerRegistration? = null
    private var requestsListener: ListenerRegistration? = null

    fun loadDonation(donationId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        donationListener?.remove()
        donationListener = donationRepository.listenToDonation(
            donationId = donationId,
            onUpdate = { donation ->
                _uiState.update {
                    it.copy(isLoading = false, donation = donation, errorMessage = null)
                }
            },
            onError = { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )

        requestsListener?.remove()
        requestsListener = foodRequestRepository.listenToRequestsForDonation(
            donationId = donationId,
            onUpdate = { requests ->
                _uiState.update { it.copy(incomingRequests = requests) }
            },
            onError = { }
        )
    }

    fun approveRequest(request: FoodRequest) {
        _uiState.update { it.copy(isUpdating = true, updateMessage = null) }
        foodRequestRepository.approveRequest(
            requestId = request.requestId,
            donationId = request.donationId,
            onSuccess = {
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = "Request approved")
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    fun rejectRequest(request: FoodRequest) {
        _uiState.update { it.copy(isUpdating = true, updateMessage = null) }
        foodRequestRepository.rejectRequest(
            requestId = request.requestId,
            donationId = request.donationId,
            onSuccess = {
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = "Request rejected")
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    fun cancelDonation() {
        val donation = _uiState.value.donation ?: return
        _uiState.update { it.copy(isUpdating = true, updateMessage = null) }
        donationRepository.updateDonationStatus(
            donationId = donation.donationId,
            status = DonationStatus.EXPIRED.name,
            onSuccess = {
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = "Donation cancelled")
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    fun markCompleted() {
        val donation = _uiState.value.donation ?: return
        _uiState.update { it.copy(isUpdating = true, updateMessage = null) }
        donationRepository.updateDonationStatus(
            donationId = donation.donationId,
            status = DonationStatus.COMPLETED.name,
            onSuccess = {
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = "Donation marked as completed")
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    fun clearUpdateMessage() {
        _uiState.update { it.copy(updateMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        donationListener?.remove()
        requestsListener?.remove()
    }
}
