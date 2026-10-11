package com.example.reserve.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reserve.ui.theme.ReserveTheme

@Composable
fun ReserveApp(
    startDestination: String = ReserveNavRoutes.DONOR_DASHBOARD
) {
    ReserveTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // Donor Graph (Person 2 - Implemented)
            donorGraph(navController = navController)

            // Auth Graph placeholder (Person 1 Integration Point)
            composable(ReserveNavRoutes.AUTH_LOGIN) {
                PhasePlaceholderScreen(
                    title = "Authentication",
                    phaseInfo = "Person 1 Auth Flow",
                    description = "Login and registration screens implemented by Person 1.",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // NGO Graph placeholder (Person 3 Integration Point)
            composable(ReserveNavRoutes.NGO_DASHBOARD) {
                PhasePlaceholderScreen(
                    title = "NGO Dashboard",
                    phaseInfo = "Person 3 NGO Flow",
                    description = "NGO portal and browsing features implemented by Person 3.",
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
