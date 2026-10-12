package com.example.reserve.data.model

data class FoodRequest(
    val requestId: String = "",
    val donationId: String = "",
    val ngoId: String = "",
    val donorId: String = "",
    val requestTime: Long = System.currentTimeMillis(),
    val status: String = RequestStatus.PENDING.name,
    val collectionTime: Long = 0L
)

enum class RequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    COLLECTED,
    COMPLETED
}
