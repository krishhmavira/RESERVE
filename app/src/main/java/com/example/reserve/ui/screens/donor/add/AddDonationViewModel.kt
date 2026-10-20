package com.example.reserve.ui.screens.donor.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.FoodType
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.repository.StorageRepository
import com.example.reserve.data.util.ErrorMapper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddDonationViewModel : ViewModel() {

    private val donationRepository = DonationRepository()
    private val storageRepository = StorageRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AddDonationUiState())
    val uiState: StateFlow<AddDonationUiState> = _uiState.asStateFlow()

    fun updateFoodName(value: String) {
        _uiState.update { it.copy(foodName = value, foodNameError = null, errorMessage = null) }
    }

    fun updateFoodType(value: FoodType) {
        _uiState.update { it.copy(foodType = value, foodTypeError = null, errorMessage = null) }
    }

    fun updateQuantity(value: String) {
        _uiState.update { it.copy(quantity = value, quantityError = null, errorMessage = null) }
    }

    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value, errorMessage = null) }
    }

    fun updatePickupLocation(value: String) {
        _uiState.update { it.copy(pickupLocation = value, pickupLocationError = null, errorMessage = null) }
    }

    fun updatePreparedTime(value: String) {
        _uiState.update { it.copy(preparedTime = value, errorMessage = null) }
    }

    fun updateExpiryTime(value: String) {
        _uiState.update { it.copy(expiryTime = value, expiryTimeError = null, errorMessage = null) }
    }

    fun updateImageUri(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri, errorMessage = null) }
    }

    fun submit() {
        if (!validate()) return

        val uid = auth.currentUser?.uid ?: run {
            _uiState.update { it.copy(errorMessage = "Not authenticated") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val imageUri = _uiState.value.imageUri
        if (imageUri != null) {
            _uiState.update { it.copy(isUploadingImage = true) }
            storageRepository.uploadDonationImage(
                imageUri = imageUri,
                onSuccess = { downloadUrl ->
                    _uiState.update { it.copy(isUploadingImage = false) }
                    createDonation(uid, downloadUrl)
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isUploadingImage = false,
                            errorMessage = ErrorMapper.imageUploadError(exception)
                        )
                    }
                }
            )
        } else {
            createDonation(uid, "")
        }
    }

    private fun createDonation(uid: String, imageUrl: String) {
        val state = _uiState.value
        val donation = Donation(
            donorId = uid,
            foodName = state.foodName.trim(),
            foodType = state.foodType!!.name,
            quantity = state.quantity.trim(),
            description = state.description.trim(),
            pickupLocation = state.pickupLocation.trim(),
            preparedTime = state.preparedTime.trim(),
            expiryTime = state.expiryTime.trim(),
            imageUrl = imageUrl,
            status = DonationStatus.AVAILABLE.name,
            createdAt = System.currentTimeMillis()
        )

        donationRepository.createDonation(
            donation = donation,
            onSuccess = {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = ErrorMapper.donationError("create", exception))
                }
            }
        )
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        var valid = true

        if (state.foodName.isBlank()) {
            _uiState.update { it.copy(foodNameError = "Food name is required") }
            valid = false
        }
        if (state.foodType == null) {
            _uiState.update { it.copy(foodTypeError = "Select a food type") }
            valid = false
        }
        if (state.quantity.isBlank()) {
            _uiState.update { it.copy(quantityError = "Quantity is required") }
            valid = false
        }
        if (state.pickupLocation.isBlank()) {
            _uiState.update { it.copy(pickupLocationError = "Pickup location is required") }
            valid = false
        }
        if (state.expiryTime.isBlank()) {
            _uiState.update { it.copy(expiryTimeError = "Expiry time is required") }
            valid = false
        }

        return valid
    }
}
