package com.example.reserve.ui.screens.ngo.myrequests

import com.example.reserve.data.model.FoodRequest

data class MyRequestsUiState(
    val requests: List<FoodRequest> = emptyList(),
    val donationNames: Map<String, String> = emptyMap(),
    val selectedFilter: String = "ALL",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredRequests: List<FoodRequest>
        get() = if (selectedFilter == "ALL") {
            requests
        } else {
            requests.filter { it.status == selectedFilter }
        }
}
