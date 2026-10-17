package com.example.reserve.ui.navigation

import kotlinx.serialization.Serializable

sealed interface ReserveRoute {

    // Entry
    @Serializable data object Splash : ReserveRoute

    // Auth
    @Serializable data object Login : ReserveRoute
    @Serializable data object Register : ReserveRoute
    @Serializable data object ForgotPassword : ReserveRoute

    // Donor
    @Serializable data object DonorHome : ReserveRoute
    @Serializable data object AddDonation : ReserveRoute
    @Serializable data class DonationDetail(val donationId: String) : ReserveRoute
    @Serializable data object MyDonations : ReserveRoute

    // NGO
    @Serializable data object NgoHome : ReserveRoute
    @Serializable data object BrowseFood : ReserveRoute
    @Serializable data class FoodDetail(val donationId: String) : ReserveRoute
    @Serializable data object MyRequests : ReserveRoute

    // Shared
    @Serializable data object Profile : ReserveRoute
    @Serializable data object EditProfile : ReserveRoute
}
