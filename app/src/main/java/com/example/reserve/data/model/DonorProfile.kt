package com.example.reserve.data.model

data class DonorProfile(
    val id: String = "donor_001",
    val contactName: String = "",
    val organizationName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val donorType: DonorType = DonorType.RESTAURANT,
    val profileImageUrl: String? = null,
    val memberSince: String = "October 2026",
    val totalDonationsCount: Int = 0,
    val isVerified: Boolean = true
) {
    val initials: String
        get() {
            val name = organizationName.ifBlank { contactName }
            if (name.isBlank()) return "D"
            val parts = name.trim().split("\\s+".toRegex())
            return if (parts.size >= 2) {
                "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            } else {
                "${parts[0].first().uppercaseChar()}"
            }
        }
}
