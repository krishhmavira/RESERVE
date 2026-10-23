package com.example.reserve.ui.screens.ngo.requestdetail

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.repository.FoodRequestRepository
import com.example.reserve.data.util.FirebaseHelper
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RequestDetailViewModel : ViewModel() {

    private val foodRequestRepository = FoodRequestRepository()
    private val donationRepository = DonationRepository()

    private val _uiState = MutableStateFlow(RequestDetailUiState())
    val uiState: StateFlow<RequestDetailUiState> = _uiState.asStateFlow()

    private var requestListener: ListenerRegistration? = null
    private var donationListener: ListenerRegistration? = null
    private var currentDonationId: String? = null

    fun loadRequest(requestId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        requestListener?.remove()
        requestListener = foodRequestRepository.listenToRequest(
            requestId = requestId,
            onUpdate = { request ->
                if (request != null) {
                    _uiState.update { it.copy(request = request) }
                    listenToDonation(request.donationId)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Request not found") }
                }
            },
            onError = { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    private fun listenToDonation(donationId: String) {
        if (donationId == currentDonationId && donationListener != null) return
        currentDonationId = donationId
        donationListener?.remove()
        donationListener = donationRepository.listenToDonation(
            donationId = donationId,
            onUpdate = { donation ->
                _uiState.update { it.copy(isLoading = false, donation = donation) }
            },
            onError = { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        updateMessage = "Could not load donation details: ${FirebaseHelper.mapFirebaseError(exception)}"
                    )
                }
            }
        )
    }

    fun cancelRequest() {
        val request = _uiState.value.request ?: return
        if (request.status != RequestStatus.PENDING.name) {
            _uiState.update { it.copy(updateMessage = "Only pending requests can be cancelled") }
            return
        }
        if (_uiState.value.isUpdating) return

        _uiState.update { it.copy(isUpdating = true, updateMessage = null) }

        foodRequestRepository.rejectRequest(
            requestId = request.requestId,
            donationId = request.donationId,
            onSuccess = {
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = "Request cancelled")
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
        requestListener?.remove()
        donationListener?.remove()
    }
}
