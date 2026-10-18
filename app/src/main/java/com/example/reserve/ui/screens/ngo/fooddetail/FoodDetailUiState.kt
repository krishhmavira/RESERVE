package com.example.reserve.ui.screens.ngo.fooddetail

import com.example.reserve.data.model.Donation

data class FoodDetailUiState(
    val donation: Donation? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isRequesting: Boolean = false,
    val alreadyRequested: Boolean = false,
    val requestSuccess: Boolean = false,
    val requestError: String? = null
)
