package com.example.reserve.ui.screens.donor.detail

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.repository.DonationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DonationDetailViewModel : ViewModel() {

    private val donationRepository = DonationRepository()

    private val _uiState = MutableStateFlow(DonationDetailUiState())
    val uiState: StateFlow<DonationDetailUiState> = _uiState.asStateFlow()

    fun loadDonation(donationId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        donationRepository.getDonation(
            donationId = donationId,
            onSuccess = { donation ->
                _uiState.update {
                    it.copy(isLoading = false, donation = donation)
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exception.message)
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
                    it.copy(
                        isUpdating = false,
                        donation = it.donation?.copy(status = DonationStatus.EXPIRED.name),
                        updateMessage = "Donation cancelled"
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        updateMessage = exception.message ?: "Failed to cancel"
                    )
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
                    it.copy(
                        isUpdating = false,
                        donation = it.donation?.copy(status = DonationStatus.COMPLETED.name),
                        updateMessage = "Donation marked as completed"
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        updateMessage = exception.message ?: "Failed to update"
                    )
                }
            }
        )
    }

    fun clearUpdateMessage() {
        _uiState.update { it.copy(updateMessage = null) }
    }
}
