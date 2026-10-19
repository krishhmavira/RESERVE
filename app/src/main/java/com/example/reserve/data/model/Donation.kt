package com.example.reserve.data.model

data class Donation(
    val donationId: String = "",
    val donorId: String = "",
    val foodName: String = "",
    val foodType: String = "",
    val quantity: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val pickupLocation: String = "",
    val preparedTime: String = "",
    val expiryTime: String = "",
    val status: String = DonationStatus.AVAILABLE.name,
    val createdAt: Long = System.currentTimeMillis()
)

enum class DonationStatus {
    AVAILABLE,
    REQUESTED,
    APPROVED,
    COLLECTED,
    COMPLETED,
    EXPIRED
}
