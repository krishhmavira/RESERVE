package com.example.reserve.ui.donor.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reserve.data.repository.DonationRepository
import com.example.reserve.data.repository.DonorProfileRepository
import com.example.reserve.data.repository.InMemoryDonationRepository
import com.example.reserve.data.repository.InMemoryDonorProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DonorDashboardViewModel(
    private val profileRepository: DonorProfileRepository = InMemoryDonorProfileRepository.instance,
    private val donationRepository: DonationRepository = InMemoryDonationRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorDashboardUiState())
    val uiState: StateFlow<DonorDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                combine(
                    profileRepository.getProfileFlow(),
                    donationRepository.getDonationsFlow(),
                    donationRepository.getDashboardStatsFlow()
                ) { profile, donations, stats ->
                    DonorDashboardUiState(
                        isLoading = false,
                        isRefreshing = false,
                        profile = profile,
                        stats = stats,
                        recentDonations = donations,
                        errorMessage = null
                    )
                }.catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.message ?: "Failed to load dashboard data."
                    )
                }.collect { combinedState ->
                    _uiState.value = combinedState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = e.message ?: "Failed to load dashboard."
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            delay(500) // slight debounce for smooth UI pull-refresh animation
            loadDashboardData()
        }
    }
}
