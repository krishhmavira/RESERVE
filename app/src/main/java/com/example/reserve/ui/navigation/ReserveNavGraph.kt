package com.example.reserve.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reserve.ui.screens.auth.AuthCheckState
import com.example.reserve.ui.screens.auth.AuthViewModel
import com.example.reserve.ui.screens.auth.ForgotPasswordScreen
import com.example.reserve.ui.screens.auth.LoginScreen
import com.example.reserve.ui.screens.auth.RegisterScreen
import com.example.reserve.ui.screens.donor.add.AddDonationScreen
import com.example.reserve.ui.screens.ngo.browse.BrowseFoodScreen
import com.example.reserve.ui.screens.ngo.fooddetail.FoodDetailScreen
import com.example.reserve.ui.screens.ngo.home.NgoHomeScreen
import com.example.reserve.ui.screens.donor.detail.DonationDetailScreen
import com.example.reserve.ui.screens.donor.home.DonorHomeScreen
import com.example.reserve.ui.screens.donor.mydonations.MyDonationsScreen
import com.example.reserve.ui.screens.profile.EditProfileScreen
import com.example.reserve.ui.screens.profile.ProfileScreen
import com.example.reserve.ui.screens.splash.SplashScreen

@Composable
fun ReserveNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = ReserveRoute.Splash
    ) {
        // Entry
        composable<ReserveRoute.Splash> {
            var splashDone by remember { mutableStateOf(false) }
            val authState by authViewModel.authCheckState.collectAsState()

            LaunchedEffect(Unit) {
                authViewModel.checkAuthState()
            }

            LaunchedEffect(splashDone, authState) {
                if (!splashDone) return@LaunchedEffect
                when (authState) {
                    is AuthCheckState.Checking -> { }
                    is AuthCheckState.NotAuthenticated -> {
                        navController.navigate(ReserveRoute.Login) {
                            popUpTo(ReserveRoute.Splash) { inclusive = true }
                        }
                    }
                    is AuthCheckState.Authenticated -> {
                        val route = roleHomeRoute((authState as AuthCheckState.Authenticated).role)
                        navController.navigate(route) {
                            popUpTo(ReserveRoute.Splash) { inclusive = true }
                        }
                    }
                }
            }

            SplashScreen(
                onSplashFinished = { splashDone = true }
            )
        }

        // Auth
        composable<ReserveRoute.Login> {
            LoginScreen(
                onLoginSuccess = {
                    val route = roleHomeRoute(authViewModel.currentUserRole())
                    navController.navigate(route) {
                        popUpTo(ReserveRoute.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(ReserveRoute.Register)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(ReserveRoute.ForgotPassword)
                },
                authViewModel = authViewModel
            )
        }
        composable<ReserveRoute.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    val route = roleHomeRoute(authViewModel.currentUserRole())
                    navController.navigate(route) {
                        popUpTo(ReserveRoute.Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }
        composable<ReserveRoute.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }

        // Donor
        composable<ReserveRoute.DonorHome> {
            DonorHomeScreen(
                onNavigateToAddDonation = {
                    navController.navigate(ReserveRoute.AddDonation)
                },
                onNavigateToMyDonations = {
                    navController.navigate(ReserveRoute.MyDonations)
                },
                onNavigateToDonationDetail = { donationId ->
                    navController.navigate(ReserveRoute.DonationDetail(donationId))
                },
                onNavigateToProfile = {
                    navController.navigate(ReserveRoute.Profile)
                },
                authViewModel = authViewModel
            )
        }
        composable<ReserveRoute.AddDonation> {
            AddDonationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDonationCreated = {
                    navController.popBackStack()
                }
            )
        }
        composable<ReserveRoute.DonationDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<ReserveRoute.DonationDetail>()
            DonationDetailScreen(
                donationId = detail.donationId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<ReserveRoute.MyDonations> {
            MyDonationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDonationDetail = { donationId ->
                    navController.navigate(ReserveRoute.DonationDetail(donationId))
                }
            )
        }

        // NGO
        composable<ReserveRoute.NgoHome> {
            NgoHomeScreen(
                onNavigateToBrowseFood = {
                    navController.navigate(ReserveRoute.BrowseFood)
                },
                onNavigateToMyRequests = {
                    navController.navigate(ReserveRoute.MyRequests)
                },
                onNavigateToFoodDetail = { donationId ->
                    navController.navigate(ReserveRoute.FoodDetail(donationId))
                },
                onNavigateToProfile = {
                    navController.navigate(ReserveRoute.Profile)
                },
                authViewModel = authViewModel
            )
        }
        composable<ReserveRoute.BrowseFood> {
            BrowseFoodScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToFoodDetail = { donationId ->
                    navController.navigate(ReserveRoute.FoodDetail(donationId))
                }
            )
        }
        composable<ReserveRoute.FoodDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<ReserveRoute.FoodDetail>()
            FoodDetailScreen(
                donationId = detail.donationId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRequestSuccess = {
                    navController.popBackStack()
                }
            )
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
                    authViewModel.logout()
                    navController.navigate(ReserveRoute.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable<ReserveRoute.EditProfile> {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProfileSaved = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }
    }
}

private fun roleHomeRoute(role: String): ReserveRoute {
    return when (role) {
        "NGO" -> ReserveRoute.NgoHome
        else -> ReserveRoute.DonorHome
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
