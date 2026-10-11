package com.example.reserve.ui.donor.profile

import com.example.reserve.data.model.DonorProfile

data class DonorProfileUiState(
    val isLoading: Boolean = true,
    val profile: DonorProfile? = null,
    val errorMessage: String? = null
)
