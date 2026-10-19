package com.example.reserve.ui.screens.ngo.requestdetail

import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.FoodRequest

data class RequestDetailUiState(
    val request: FoodRequest? = null,
    val donation: Donation? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateMessage: String? = null
)
