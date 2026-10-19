package com.example.reserve.ui.screens.donor.detail

import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.FoodRequest

data class DonationDetailUiState(
    val donation: Donation? = null,
    val incomingRequests: List<FoodRequest> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateMessage: String? = null
)
