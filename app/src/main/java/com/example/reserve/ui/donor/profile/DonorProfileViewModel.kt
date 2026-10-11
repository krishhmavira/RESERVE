package com.example.reserve.ui.donor.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reserve.data.repository.DonorProfileRepository
import com.example.reserve.data.repository.InMemoryDonorProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DonorProfileViewModel(
    private val profileRepository: DonorProfileRepository = InMemoryDonorProfileRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorProfileUiState())
    val uiState: StateFlow<DonorProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    fun observeProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            profileRepository.getProfileFlow()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load donor profile."
                    )
                }
                .collect { profile ->
                    _uiState.value = DonorProfileUiState(
                        isLoading = false,
                        profile = profile,
                        errorMessage = null
                    )
                }
        }
    }
}
