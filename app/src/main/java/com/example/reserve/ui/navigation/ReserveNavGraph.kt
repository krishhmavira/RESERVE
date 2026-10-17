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
import com.example.reserve.ui.screens.auth.ForgotPasswordScreen
import com.example.reserve.ui.screens.auth.LoginScreen
import com.example.reserve.ui.screens.auth.RegisterScreen
import com.example.reserve.ui.screens.profile.EditProfileScreen
import com.example.reserve.ui.screens.profile.ProfileScreen
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
            LoginScreen(
                onLoginSuccess = {
                    // Will route to DonorHome or NgoHome based on role in Commit 5
                    navController.navigate(ReserveRoute.DonorHome) {
                        popUpTo(ReserveRoute.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(ReserveRoute.Register)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(ReserveRoute.ForgotPassword)
                }
            )
        }
        composable<ReserveRoute.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    // Will route to DonorHome or NgoHome based on role in Commit 5
                    navController.navigate(ReserveRoute.DonorHome) {
                        popUpTo(ReserveRoute.Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<ReserveRoute.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditProfile = {
                    navController.navigate(ReserveRoute.EditProfile)
                },
                onLogout = {
                    navController.navigate(ReserveRoute.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<ReserveRoute.EditProfile> {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProfileSaved = {
                    navController.popBackStack()
                }
            )
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
