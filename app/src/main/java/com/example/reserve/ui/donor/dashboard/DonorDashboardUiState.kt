package com.example.reserve.ui.donor.dashboard

import com.example.reserve.data.model.DonationItem
import com.example.reserve.data.model.DonorDashboardStats
import com.example.reserve.data.model.DonorProfile

data class DonorDashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val profile: DonorProfile? = null,
    val stats: DonorDashboardStats = DonorDashboardStats(),
    val recentDonations: List<DonationItem> = emptyList(),
    val errorMessage: String? = null
)
