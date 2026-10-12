package com.example.reserve.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reserve.ui.screens.splash.SplashScreen

@Composable
fun ReserveNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ReserveRoute.Splash
    ) {
        // Entry
        composable<ReserveRoute.Splash> {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(ReserveRoute.Login) {
                        popUpTo(ReserveRoute.Splash) { inclusive = true }
                    }
                }
            )
        }

        // Auth
        composable<ReserveRoute.Login> {
            PlaceholderScreen("Login")
        }
        composable<ReserveRoute.Register> {
            PlaceholderScreen("Register")
        }
        composable<ReserveRoute.ForgotPassword> {
            PlaceholderScreen("Forgot Password")
        }

        // Donor
        composable<ReserveRoute.DonorHome> {
            PlaceholderScreen("Donor Home")
        }
        composable<ReserveRoute.AddDonation> {
            PlaceholderScreen("Add Donation")
        }
        composable<ReserveRoute.DonationDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<ReserveRoute.DonationDetail>()
            PlaceholderScreen("Donation Detail: ${detail.donationId}")
        }
        composable<ReserveRoute.MyDonations> {
            PlaceholderScreen("My Donations")
        }

        // NGO
        composable<ReserveRoute.NgoHome> {
            PlaceholderScreen("NGO Home")
        }
        composable<ReserveRoute.BrowseFood> {
            PlaceholderScreen("Browse Food")
        }
        composable<ReserveRoute.FoodDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<ReserveRoute.FoodDetail>()
            PlaceholderScreen("Food Detail: ${detail.donationId}")
        }
        composable<ReserveRoute.MyRequests> {
            PlaceholderScreen("My Requests")
        }

        // Shared
        composable<ReserveRoute.Profile> {
            PlaceholderScreen("Profile")
        }
        composable<ReserveRoute.EditProfile> {
            PlaceholderScreen("Edit Profile")
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name)
    }
}
