package com.example.reserve.ui.screens.ngo.browse

import com.example.reserve.data.model.Donation

data class BrowseFoodUiState(
    val allDonations: List<Donation> = emptyList(),
    val searchQuery: String = "",
    val selectedFoodType: String = "ALL",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredDonations: List<Donation>
        get() {
            var result = allDonations
            if (selectedFoodType != "ALL") {
                result = result.filter { it.foodType == selectedFoodType }
            }
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                result = result.filter { donation ->
                    donation.foodName.lowercase().contains(query) ||
                        donation.pickupLocation.lowercase().contains(query) ||
                        donation.description.lowercase().contains(query) ||
                        donation.foodType.replace("_", " ").lowercase().contains(query)
                }
            }
            return result
        }
}
