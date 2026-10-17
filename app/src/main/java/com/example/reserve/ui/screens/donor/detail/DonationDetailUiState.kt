package com.example.reserve.ui.screens.donor.detail

import com.example.reserve.data.model.Donation

data class DonationDetailUiState(
    val donation: Donation? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateMessage: String? = null
)
