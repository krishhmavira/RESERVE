package com.example.reserve.data.model

import androidx.compose.ui.graphics.Color
import com.example.reserve.ui.theme.StatusApprovedBg
import com.example.reserve.ui.theme.StatusApprovedBorder
import com.example.reserve.ui.theme.StatusApprovedText
import com.example.reserve.ui.theme.StatusCancelledBg
import com.example.reserve.ui.theme.StatusCancelledBorder
import com.example.reserve.ui.theme.StatusCancelledText
import com.example.reserve.ui.theme.StatusClaimedBg
import com.example.reserve.ui.theme.StatusClaimedBorder
import com.example.reserve.ui.theme.StatusClaimedText
import com.example.reserve.ui.theme.StatusCompletedBg
import com.example.reserve.ui.theme.StatusCompletedBorder
import com.example.reserve.ui.theme.StatusCompletedText
import com.example.reserve.ui.theme.StatusPendingBg
import com.example.reserve.ui.theme.StatusPendingBorder
import com.example.reserve.ui.theme.StatusPendingText

enum class DonationStatus(
    val displayName: String,
    val description: String
) {
    PENDING("Pending", "Awaiting review or matching"),
    APPROVED("Approved", "Approved & visible to NGOs"),
    CLAIMED("Claimed", "Claimed by an NGO for pickup"),
    COMPLETED("Completed", "Food collected & distributed"),
    CANCELLED("Cancelled", "Donation was cancelled");

    fun badgeBackgroundColor(): Color = when (this) {
        PENDING -> StatusPendingBg
        APPROVED -> StatusApprovedBg
        CLAIMED -> StatusClaimedBg
        COMPLETED -> StatusCompletedBg
        CANCELLED -> StatusCancelledBg
    }

    fun badgeTextColor(): Color = when (this) {
        PENDING -> StatusPendingText
        APPROVED -> StatusApprovedText
        CLAIMED -> StatusClaimedText
        COMPLETED -> StatusCompletedText
        CANCELLED -> StatusCancelledText
    }

    fun badgeBorderColor(): Color = when (this) {
        PENDING -> StatusPendingBorder
        APPROVED -> StatusApprovedBorder
        CLAIMED -> StatusClaimedBorder
        COMPLETED -> StatusCompletedBorder
        CANCELLED -> StatusCancelledBorder
    }
}

data class DonationItem(
    val id: String,
    val donorId: String,
    val foodTitle: String,
    val category: String,
    val quantity: String,
    val pickupAddress: String,
    val pickupTimeWindow: String,
    val status: DonationStatus,
    val ngoClaimantName: String? = null,
    val createdAtFormatted: String,
    val expiryHoursRemaining: Int = 12
)

data class DonorDashboardStats(
    val pendingCount: Int = 0,
    val approvedCount: Int = 0,
    val claimedCount: Int = 0,
    val completedCount: Int = 0,
    val estimatedMealsSaved: Int = 0
) {
    val totalActiveDonations: Int
        get() = pendingCount + approvedCount + claimedCount
}
