package com.example.reserve.ui.screens.donor.add

import com.example.reserve.data.model.FoodType

data class AddDonationUiState(
    val foodName: String = "",
    val foodType: FoodType? = null,
    val quantity: String = "",
    val description: String = "",
    val pickupLocation: String = "",
    val preparedTime: String = "",
    val expiryTime: String = "",
    val foodNameError: String? = null,
    val foodTypeError: String? = null,
    val quantityError: String? = null,
    val pickupLocationError: String? = null,
    val expiryTimeError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
