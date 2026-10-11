package com.example.reserve

import com.example.reserve.data.model.DonationItem
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.DonorDashboardStats
import com.example.reserve.data.repository.InMemoryDonationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DonorDashboardStatsTest {

    private lateinit var donationRepository: InMemoryDonationRepository

    @Before
    fun setUp() {
        donationRepository = InMemoryDonationRepository()
    }

    @Test
    fun stats_totalActiveDonations_sumOfPendingApprovedClaimed() {
        val stats = DonorDashboardStats(
            pendingCount = 2,
            approvedCount = 3,
            claimedCount = 1,
            completedCount = 5
        )
        assertEquals(6, stats.totalActiveDonations)
    }

    @Test
    fun defaultDonations_haveValidCategoriesAndStatuses() = runBlocking {
        val list = donationRepository.getDonations()
        assertTrue("Expected non-empty default donations", list.isNotEmpty())

        val stats = donationRepository.getDashboardStatsFlow().first()
        assertEquals(1, stats.pendingCount)
        assertEquals(1, stats.approvedCount)
        assertEquals(1, stats.claimedCount)
        assertEquals(1, stats.completedCount)
        assertTrue("Estimated meals should be > 0", stats.estimatedMealsSaved > 0)
    }

    @Test
    fun addDonation_incrementsStatusAndFlow() = runBlocking {
        val newDonation = DonationItem(
            id = "new_001",
            donorId = "donor_001",
            foodTitle = "100 Sandwiches",
            category = "Snacks",
            quantity = "100 packs",
            pickupAddress = "Churchgate Station",
            pickupTimeWindow = "Today 6 PM",
            status = DonationStatus.PENDING,
            createdAtFormatted = "Just now"
        )

        donationRepository.addDonation(newDonation)
        val stats = donationRepository.getDashboardStatsFlow().first()
        assertEquals(2, stats.pendingCount)
    }
}
