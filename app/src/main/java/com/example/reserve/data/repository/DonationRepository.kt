package com.example.reserve.data.repository

import com.example.reserve.data.model.DonationItem
import com.example.reserve.data.model.DonorDashboardStats
import kotlinx.coroutines.flow.Flow

interface DonationRepository {
    fun getDonationsFlow(): Flow<List<DonationItem>>
    fun getDashboardStatsFlow(): Flow<DonorDashboardStats>
    suspend fun getDonations(): List<DonationItem>
    suspend fun addDonation(item: DonationItem): Result<DonationItem>
}
