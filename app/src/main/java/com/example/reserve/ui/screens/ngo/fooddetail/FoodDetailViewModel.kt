package com.example.reserve.ui.screens.ngo.fooddetail

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.FoodRequest
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

class FoodDetailViewModel : ViewModel() {

    private val donationRepository = DonationRepository()
    private val foodRequestRepository = FoodRequestRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(FoodDetailUiState())
    val uiState: StateFlow<FoodDetailUiState> = _uiState.asStateFlow()

    private var donationListener: ListenerRegistration? = null

    fun loadDonation(donationId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        donationListener?.remove()
        donationListener = donationRepository.listenToDonation(
            donationId = donationId,
            onUpdate = { donation ->
                _uiState.update { it.copy(isLoading = false, donation = donation, errorMessage = null) }
                if (donation != null) checkExistingRequest(donationId)
            },
            onError = { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    private fun checkExistingRequest(donationId: String) {
        val uid = auth.currentUser?.uid ?: return
        foodRequestRepository.checkExistingRequest(
            donationId = donationId,
            ngoId = uid,
            onResult = { exists ->
                _uiState.update { it.copy(alreadyRequested = exists) }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(requestError = "Could not check existing requests: ${FirebaseHelper.mapFirebaseError(exception)}")
                }
            }
        )
    }

    fun requestFood() {
        val donation = _uiState.value.donation ?: return
        val uid = auth.currentUser?.uid ?: run {
            _uiState.update { it.copy(requestError = "Not authenticated") }
            return
        }

        if (donation.status != DonationStatus.AVAILABLE.name) {
            _uiState.update { it.copy(requestError = "This donation is no longer available") }
            return
        }

        _uiState.update { it.copy(isRequesting = true, requestError = null) }

        val request = FoodRequest(
            donationId = donation.donationId,
            ngoId = uid,
            donorId = donation.donorId,
            status = RequestStatus.PENDING.name,
            requestTime = System.currentTimeMillis()
        )

        foodRequestRepository.createRequestAndUpdateDonation(
            request = request,
            donationStatus = DonationStatus.REQUESTED.name,
            onSuccess = {
                _uiState.update {
                    it.copy(
                        isRequesting = false,
                        requestSuccess = true,
                        alreadyRequested = true
                    )
                }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isRequesting = false, requestError = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    fun clearRequestError() {
        _uiState.update { it.copy(requestError = null) }
    }

    override fun onCleared() {
        super.onCleared()
        donationListener?.remove()
    }
}
