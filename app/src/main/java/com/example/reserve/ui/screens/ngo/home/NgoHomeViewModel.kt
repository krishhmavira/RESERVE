package com.example.reserve.ui.screens.ngo.home

import androidx.lifecycle.ViewModel
import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.RequestStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NgoHomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(NgoHomeUiState())
    val uiState: StateFlow<NgoHomeUiState> = _uiState.asStateFlow()

    private var donationsListener: ListenerRegistration? = null
    private var requestsListener: ListenerRegistration? = null

    init {
        loadData()
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadAvailableDonations()
        loadMyRequestStats()
    }

    private fun loadAvailableDonations() {
        donationsListener?.remove()
        donationsListener = firestore.collection("donations")
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
                    it.copy(
                        isLoading = false,
                        availableCount = donations.size,
                        recentAvailable = donations.take(5),
                        errorMessage = null
                    )
                }
            }
    }

    private fun loadMyRequestStats() {
        val uid = auth.currentUser?.uid ?: return
        requestsListener?.remove()
        requestsListener = firestore.collection("foodRequests")
            .whereEqualTo("ngoId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val requests = snapshot?.documents ?: emptyList()
                val totalRequests = requests.size
                val approved = requests.count { doc ->
                    val status = doc.getString("status")
                    status == RequestStatus.APPROVED.name || status == RequestStatus.COMPLETED.name
                }
                _uiState.update {
                    it.copy(myRequestsCount = totalRequests, approvedCount = approved)
                }
            }
    }

    fun retry() {
        loadData()
    }

    override fun onCleared() {
        super.onCleared()
        donationsListener?.remove()
        requestsListener?.remove()
    }
}
