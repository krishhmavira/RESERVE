package com.example.reserve.ui.screens.donor.add

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.FoodType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddDonationViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
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

    fun submit() {
        if (!validate()) return

        val uid = auth.currentUser?.uid ?: run {
            _uiState.update { it.copy(errorMessage = "Not authenticated") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val docRef = firestore.collection("donations").document()
        val state = _uiState.value
        val donation = Donation(
            donationId = docRef.id,
            donorId = uid,
            foodName = state.foodName.trim(),
            foodType = state.foodType!!.name,
            quantity = state.quantity.trim(),
            description = state.description.trim(),
            pickupLocation = state.pickupLocation.trim(),
            preparedTime = state.preparedTime.trim(),
            expiryTime = state.expiryTime.trim(),
            status = DonationStatus.AVAILABLE.name,
            createdAt = System.currentTimeMillis()
        )

        docRef.set(donation)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }
            .addOnFailureListener { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exception.message ?: "Failed to create donation")
                }
            }
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
