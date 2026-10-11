package com.example.reserve.ui.navigation

object ReserveNavRoutes {
    // Donor Flow (Person 2 - Current Phase)
    const val DONOR_DASHBOARD = "donor_dashboard"
    const val DONOR_PROFILE = "donor_profile"
    const val DONOR_EDIT_PROFILE = "donor_edit_profile"
    const val DONOR_DONATE_FOOD = "donor_donate_food" // Phase 3 integration point
    const val DONOR_MY_DONATIONS = "donor_my_donations" // Phase 3 integration point

    // Authentication Flow (Person 1 Integration Point)
    const val AUTH_LOGIN = "auth_login"
    const val AUTH_REGISTER = "auth_register"
    const val AUTH_FORGOT_PASSWORD = "auth_forgot_password"

    // NGO Flow (Person 3 Integration Point)
    const val NGO_DASHBOARD = "ngo_dashboard"
    const val NGO_PROFILE = "ngo_profile"
}
