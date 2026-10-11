package com.example.reserve.ui.donor.editprofile

import com.example.reserve.data.model.DonorProfile
import com.example.reserve.data.model.DonorType

data class DonorEditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val initialProfile: DonorProfile? = null,
    val contactName: String = "",
    val organizationName: String = "",
    val phone: String = "",
    val address: String = "",
    val donorType: DonorType = DonorType.RESTAURANT,
    val email: String = "",
    val contactNameError: String? = null,
    val organizationNameError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val generalErrorMessage: String? = null
) {
    val hasValidationErrors: Boolean
        get() = contactNameError != null || organizationNameError != null || phoneError != null || addressError != null
}
