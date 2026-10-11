package com.example.reserve.data.repository

import com.example.reserve.data.model.DonorProfile
import kotlinx.coroutines.flow.Flow

interface DonorProfileRepository {
    fun getProfileFlow(): Flow<DonorProfile>
    suspend fun getProfile(): DonorProfile
    suspend fun updateProfile(updated: DonorProfile): Result<DonorProfile>
}
