package com.example.reserve.ui.screens.donor.home

import com.example.reserve.data.model.Donation

data class DonorHomeUiState(
    val userName: String = "",
    val totalDonations: Int = 0,
    val activeDonations: Int = 0,
    val completedDonations: Int = 0,
    val recentDonations: List<Donation> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
