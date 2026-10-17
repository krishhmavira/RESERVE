package com.example.reserve.ui.screens.ngo.browse

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.DonationStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BrowseFoodViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

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
        listenerRegistration = firestore.collection("donations")
            .whereEqualTo("status", DonationStatus.AVAILABLE.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    return@addSnapshotListener
                }
                val donations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Donation::class.java)
                } ?: emptyList()

                _uiState.update {
                    it.copy(isLoading = false, allDonations = donations, errorMessage = null)
                }
            }
    }

    fun retry() {
        loadAvailableDonations()
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
