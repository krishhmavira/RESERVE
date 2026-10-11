package com.example.reserve.data.repository

import com.example.reserve.data.model.DonorProfile
import com.example.reserve.data.model.DonorType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryDonorProfileRepository(
    initialProfile: DonorProfile = defaultSampleProfile
) : DonorProfileRepository {

    private val mutex = Mutex()
    private val _profileFlow = MutableStateFlow(initialProfile)

    override fun getProfileFlow(): Flow<DonorProfile> = _profileFlow.asStateFlow()

    override suspend fun getProfile(): DonorProfile {
        return mutex.withLock { _profileFlow.value }
    }

    override suspend fun updateProfile(updated: DonorProfile): Result<DonorProfile> {
        // Validation checks
        if (updated.contactName.trim().length < 2) {
            return Result.failure(IllegalArgumentException("Contact person name must be at least 2 characters."))
        }
        if (updated.organizationName.trim().length < 2) {
            return Result.failure(IllegalArgumentException("Organization name must be at least 2 characters."))
        }
        val cleanPhone = updated.phone.replace("[\\s-]".toRegex(), "")
        if (cleanPhone.length < 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid phone number with at least 10 digits."))
        }
        if (updated.address.trim().length < 5) {
            return Result.failure(IllegalArgumentException("Please enter a complete address."))
        }

        // Simulate backend latency
        delay(600)

        mutex.withLock {
            _profileFlow.value = updated
        }
        return Result.success(updated)
    }

    companion object {
        val defaultSampleProfile = DonorProfile(
            id = "donor_001",
            contactName = "Aarav Sharma",
            organizationName = "The Grand Horizon Hotel",
            email = "contact@grandhorizon.com",
            phone = "+91 98765 43210",
            address = "42 Marine Drive, Nariman Point, Mumbai, MH 400021",
            donorType = DonorType.HOTEL,
            profileImageUrl = null,
            memberSince = "October 2026",
            totalDonationsCount = 28,
            isVerified = true
        )

        // Singleton instance for in-memory shared state across ViewModels
        val instance: InMemoryDonorProfileRepository by lazy {
            InMemoryDonorProfileRepository()
        }
    }
}
