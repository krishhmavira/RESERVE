package com.example.reserve.ui.screens.donor.add

import android.net.Uri
import com.example.reserve.data.model.FoodType

data class AddDonationUiState(
    val foodName: String = "",
    val foodType: FoodType? = null,
    val quantity: String = "",
    val description: String = "",
    val pickupLocation: String = "",
    val preparedTime: String = "",
    val expiryTime: String = "",
    val imageUri: Uri? = null,
    val foodNameError: String? = null,
    val foodTypeError: String? = null,
    val quantityError: String? = null,
    val pickupLocationError: String? = null,
    val expiryTimeError: String? = null,
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
