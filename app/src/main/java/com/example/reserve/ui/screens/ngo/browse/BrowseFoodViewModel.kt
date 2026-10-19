package com.example.reserve.ui.screens.ngo.browse

import androidx.lifecycle.ViewModel
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.util.FirebaseHelper
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BrowseFoodViewModel : ViewModel() {

    private val donationRepository = DonationRepository()

    private val _uiState = MutableStateFlow(BrowseFoodUiState())
    val uiState: StateFlow<BrowseFoodUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadAvailableDonations()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFoodTypeFilter(foodType: String) {
        _uiState.update { it.copy(selectedFoodType = foodType) }
    }

    private fun loadAvailableDonations() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        listenerRegistration?.remove()
        listenerRegistration = donationRepository.listenToAvailableDonations(
            onUpdate = { donations ->
                _uiState.update {
                    it.copy(isLoading = false, allDonations = donations, errorMessage = null)
                }
            },
            onError = { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = FirebaseHelper.mapFirebaseError(exception))
                }
            }
        )
    }

    fun retry() {
        loadAvailableDonations()
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
