package com.example.reserve.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.donor.dashboard.DonorDashboardScreen
import com.example.reserve.ui.donor.editprofile.DonorEditProfileScreen
import com.example.reserve.ui.donor.profile.DonorProfileScreen
import com.example.reserve.ui.theme.ForestGreen
import com.example.reserve.ui.theme.ReserveBackgroundLight
import com.example.reserve.ui.theme.ReserveTextPrimary
import com.example.reserve.ui.theme.ReserveTextSecondary

fun NavGraphBuilder.donorGraph(navController: NavController) {
    composable(ReserveNavRoutes.DONOR_DASHBOARD) {
        DonorDashboardScreen(
            onNavigateToDonateFood = {
                navController.navigate(ReserveNavRoutes.DONOR_DONATE_FOOD)
            },
            onNavigateToMyDonations = {
                navController.navigate(ReserveNavRoutes.DONOR_MY_DONATIONS)
            },
            onNavigateToProfile = {
                navController.navigate(ReserveNavRoutes.DONOR_PROFILE)
            }
        )
    }

    composable(ReserveNavRoutes.DONOR_PROFILE) {
        DonorProfileScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToEditProfile = {
                navController.navigate(ReserveNavRoutes.DONOR_EDIT_PROFILE)
            }
        )
    }

    composable(ReserveNavRoutes.DONOR_EDIT_PROFILE) {
        DonorEditProfileScreen(
            onNavigateBack = {
                navController.popBackStack()
            },
            onSaveSuccess = {
                navController.popBackStack()
            }
        )
    }

    composable(ReserveNavRoutes.DONOR_DONATE_FOOD) {
        PhasePlaceholderScreen(
            title = "Donate Surplus Food",
            phaseInfo = "Phase 3: Add Food (Person 2)",
            description = "The food donation creation form with food category selection, quantity, photo upload, pickup time, and shelf-life tracking will be implemented in Phase 3.",
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(ReserveNavRoutes.DONOR_MY_DONATIONS) {
        PhasePlaceholderScreen(
            title = "My Donations",
            phaseInfo = "Phase 3: My Donations (Person 2)",
            description = "The dedicated donation history, active donation tracking, and claim request management will be implemented in Phase 3.",
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhasePlaceholderScreen(
    title: String,
    phaseInfo: String,
    description: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ReserveTextPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = ReserveTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ReserveBackgroundLight
                )
            )
        },
        containerColor = ReserveBackgroundLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Construction,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = phaseInfo,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        fontSize = 17.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ReserveTextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                ReservePrimaryButton(
                    text = "Back to Dashboard",
                    onClick = onNavigateBack
                )
            }
        }
    }
}
