package com.example.reserve.data.repository

import com.example.reserve.data.model.DonationItem
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.DonorDashboardStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryDonationRepository(
    initialDonations: List<DonationItem> = defaultDonations
) : DonationRepository {

    private val mutex = Mutex()
    private val _donationsFlow = MutableStateFlow(initialDonations)

    override fun getDonationsFlow(): Flow<List<DonationItem>> = _donationsFlow.asStateFlow()

    override fun getDashboardStatsFlow(): Flow<DonorDashboardStats> = _donationsFlow.map { list ->
        calculateStats(list)
    }

    override suspend fun getDonations(): List<DonationItem> {
        return mutex.withLock { _donationsFlow.value }
    }

    override suspend fun addDonation(item: DonationItem): Result<DonationItem> {
        mutex.withLock {
            _donationsFlow.value = listOf(item) + _donationsFlow.value
        }
        return Result.success(item)
    }

    private fun calculateStats(list: List<DonationItem>): DonorDashboardStats {
        var pending = 0
        var approved = 0
        var claimed = 0
        var completed = 0

        list.forEach { item ->
            when (item.status) {
                DonationStatus.PENDING -> pending++
                DonationStatus.APPROVED -> approved++
                DonationStatus.CLAIMED -> claimed++
                DonationStatus.COMPLETED -> completed++
                DonationStatus.CANCELLED -> {}
            }
        }

        // Estimate meals saved based on completed + active listings
        val estimatedMeals = (completed * 60) + (claimed * 40) + (approved * 30)

        return DonorDashboardStats(
            pendingCount = pending,
            approvedCount = approved,
            claimedCount = claimed,
            completedCount = completed,
            estimatedMealsSaved = estimatedMeals
        )
    }

    companion object {
        val defaultDonations = listOf(
            DonationItem(
                id = "don_101",
                donorId = "donor_001",
                foodTitle = "Buffet Surplus (Dal, Rice & Veg Curry)",
                category = "Cooked Meals",
                quantity = "45 portions",
                pickupAddress = "42 Marine Drive, Nariman Point, Mumbai",
                pickupTimeWindow = "Today, 10:30 PM - 11:30 PM",
                status = DonationStatus.PENDING,
                createdAtFormatted = "30 mins ago",
                expiryHoursRemaining = 4
            ),
            DonationItem(
                id = "don_102",
                donorId = "donor_001",
                foodTitle = "Fresh Bakery Assortment & Buns",
                category = "Bakery",
                quantity = "25 boxes",
                pickupAddress = "42 Marine Drive, Nariman Point, Mumbai",
                pickupTimeWindow = "Tomorrow, 8:00 AM - 10:00 AM",
                status = DonationStatus.APPROVED,
                createdAtFormatted = "3 hours ago",
                expiryHoursRemaining = 16
            ),
            DonationItem(
                id = "don_103",
                donorId = "donor_001",
                foodTitle = "Executive Lunch Bento Packs",
                category = "Packed Meals",
                quantity = "35 packs",
                pickupAddress = "42 Marine Drive, Nariman Point, Mumbai",
                pickupTimeWindow = "Today, 4:00 PM - 5:00 PM",
                status = DonationStatus.CLAIMED,
                ngoClaimantName = "Seva Food Bank Mumbai",
                createdAtFormatted = "5 hours ago",
                expiryHoursRemaining = 2
            ),
            DonationItem(
                id = "don_104",
                donorId = "donor_001",
                foodTitle = "Banquet Event Dinner Packages",
                category = "Cooked Meals",
                quantity = "110 portions",
                pickupAddress = "42 Marine Drive, Nariman Point, Mumbai",
                pickupTimeWindow = "Yesterday, 11:00 PM",
                status = DonationStatus.COMPLETED,
                ngoClaimantName = "Robin Hood Army",
                createdAtFormatted = "Yesterday",
                expiryHoursRemaining = 0
            )
        )

        val instance: InMemoryDonationRepository by lazy {
            InMemoryDonationRepository()
        }
    }
}
