package com.example.reserve.ui.donor.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reserve.data.model.DonorProfile
import com.example.reserve.data.model.DonorType
import com.example.reserve.data.repository.DonorProfileRepository
import com.example.reserve.data.repository.InMemoryDonorProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DonorEditProfileViewModel(
    private val profileRepository: DonorProfileRepository = InMemoryDonorProfileRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonorEditProfileUiState())
    val uiState: StateFlow<DonorEditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentProfile()
    }

    fun loadCurrentProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, generalErrorMessage = null)
            try {
                val profile = profileRepository.getProfile()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    initialProfile = profile,
                    contactName = profile.contactName,
                    organizationName = profile.organizationName,
                    phone = profile.phone,
                    address = profile.address,
                    donorType = profile.donorType,
                    email = profile.email
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalErrorMessage = e.message ?: "Failed to load current profile."
                )
            }
        }
    }

    fun onContactNameChanged(value: String) {
        val error = validateContactName(value)
        _uiState.value = _uiState.value.copy(
            contactName = value,
            contactNameError = error
        )
    }

    fun onOrganizationNameChanged(value: String) {
        val error = validateOrganizationName(value)
        _uiState.value = _uiState.value.copy(
            organizationName = value,
            organizationNameError = error
        )
    }

    fun onPhoneChanged(value: String) {
        val error = validatePhone(value)
        _uiState.value = _uiState.value.copy(
            phone = value,
            phoneError = error
        )
    }

    fun onAddressChanged(value: String) {
        val error = validateAddress(value)
        _uiState.value = _uiState.value.copy(
            address = value,
            addressError = error
        )
    }

    fun onDonorTypeChanged(type: DonorType) {
        _uiState.value = _uiState.value.copy(donorType = type)
    }

    private fun validateContactName(value: String): String? {
        return when {
            value.isBlank() -> "Contact person name is required"
            value.trim().length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    private fun validateOrganizationName(value: String): String? {
        return when {
            value.isBlank() -> "Organization name is required"
            value.trim().length < 2 -> "Organization name must be at least 2 characters"
            else -> null
        }
    }

    private fun validatePhone(value: String): String? {
        val digits = value.replace("[^0-9+]".toRegex(), "")
        return when {
            value.isBlank() -> "Phone number is required"
            digits.length < 10 -> "Please enter a valid phone number (at least 10 digits)"
            else -> null
        }
    }

    private fun validateAddress(value: String): String? {
        return when {
            value.isBlank() -> "Address is required"
            value.trim().length < 5 -> "Please provide a complete address for food pickup"
            else -> null
        }
    }

    fun validateAll(): Boolean {
        val contactError = validateContactName(_uiState.value.contactName)
        val orgError = validateOrganizationName(_uiState.value.organizationName)
        val phoneError = validatePhone(_uiState.value.phone)
        val addressError = validateAddress(_uiState.value.address)

        _uiState.value = _uiState.value.copy(
            contactNameError = contactError,
            organizationNameError = orgError,
            phoneError = phoneError,
            addressError = addressError
        )

        return contactError == null && orgError == null && phoneError == null && addressError == null
    }

    fun saveProfile(onSuccess: () -> Unit) {
        // Prevent concurrent saves
        if (_uiState.value.isSaving) return

        if (!validateAll()) return

        val currentProfile = _uiState.value.initialProfile ?: DonorProfile()
        val updated = currentProfile.copy(
            contactName = _uiState.value.contactName.trim(),
            organizationName = _uiState.value.organizationName.trim(),
            phone = _uiState.value.phone.trim(),
            address = _uiState.value.address.trim(),
            donorType = _uiState.value.donorType
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                generalErrorMessage = null
            )
            try {
                val result = profileRepository.updateProfile(updated)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isSaveSuccess = true,
                        initialProfile = result.getOrNull()
                    )
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        generalErrorMessage = result.exceptionOrNull()?.message ?: "Failed to save profile changes."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    generalErrorMessage = e.message ?: "An unexpected error occurred while saving."
                )
            }
        }
    }
}
