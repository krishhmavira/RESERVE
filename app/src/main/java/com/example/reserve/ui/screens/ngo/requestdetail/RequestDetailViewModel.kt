package com.example.reserve.ui.screens.ngo.requestdetail

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.repository.FoodRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RequestDetailViewModel : ViewModel() {

    private val foodRequestRepository = FoodRequestRepository()
    private val donationRepository = DonationRepository()

    private val _uiState = MutableStateFlow(RequestDetailUiState())
    val uiState: StateFlow<RequestDetailUiState> = _uiState.asStateFlow()

    fun loadRequest(requestId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        foodRequestRepository.getRequest(
            requestId = requestId,
            onSuccess = { request ->
                _uiState.update { it.copy(request = request) }
                if (request != null) {
                    loadDonation(request.donationId)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Request not found") }
                }
            },
            onFailure = { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
            }
        )
    }

    private fun loadDonation(donationId: String) {
        donationRepository.getDonation(
            donationId = donationId,
            onSuccess = { donation ->
                _uiState.update { it.copy(isLoading = false, donation = donation) }
            },
            onFailure = {
                _uiState.update { it.copy(isLoading = false) }
            }
        )
    }

    fun cancelRequest() {
        val request = _uiState.value.request ?: return
        _uiState.update { it.copy(isUpdating = true, updateMessage = null) }

        foodRequestRepository.updateRequestStatus(
            requestId = request.requestId,
            status = RequestStatus.REJECTED.name,
            onSuccess = {
                donationRepository.updateDonationStatus(
                    donationId = request.donationId,
                    status = DonationStatus.AVAILABLE.name,
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                request = it.request?.copy(status = RequestStatus.REJECTED.name),
                                donation = it.donation?.copy(status = DonationStatus.AVAILABLE.name),
                                updateMessage = "Request cancelled"
                            )
                        }
                    },
                    onFailure = {
                        _uiState.update {
                            it.copy(
                                isUpdating = false,
                                request = it.request?.copy(status = RequestStatus.REJECTED.name),
                                updateMessage = "Request cancelled"
                            )
                        }
                    }
                )
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isUpdating = false, updateMessage = exception.message ?: "Failed to cancel")
                }
            }
        )
    }

    fun clearUpdateMessage() {
        _uiState.update { it.copy(updateMessage = null) }
    }
}
