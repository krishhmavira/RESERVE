package com.example.reserve.ui.screens.ngo.home

import com.example.reserve.data.model.Donation

data class NgoHomeUiState(
    val userName: String = "",
    val availableCount: Int = 0,
    val myRequestsCount: Int = 0,
    val approvedCount: Int = 0,
    val recentAvailable: List<Donation> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
