package com.example.reserve.ui.screens.donor.mydonations

import com.example.reserve.data.model.Donation

data class MyDonationsUiState(
    val donations: List<Donation> = emptyList(),
    val selectedFilter: String = "ALL",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredDonations: List<Donation>
        get() = if (selectedFilter == "ALL") {
            donations
        } else {
            donations.filter { it.status == selectedFilter }
        }
}
