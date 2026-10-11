package com.example.reserve.ui.donor.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.data.model.DonationItem
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.DonorDashboardStats
import com.example.reserve.data.model.DonorProfile
import com.example.reserve.ui.components.EmptyStateView
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.MetricStatCard
import com.example.reserve.ui.components.ProfileAvatar
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.components.SectionHeader
import com.example.reserve.ui.components.StatusBadge
import com.example.reserve.ui.theme.EmeraldGreenPrimary
import com.example.reserve.ui.theme.ForestGreen
import com.example.reserve.ui.theme.LightSageGreen
import com.example.reserve.ui.theme.MintAccent
import com.example.reserve.ui.theme.ReserveBackgroundLight
import com.example.reserve.ui.theme.ReserveCardBorder
import com.example.reserve.ui.theme.ReserveSurfaceLight
import com.example.reserve.ui.theme.ReserveTextPrimary
import com.example.reserve.ui.theme.ReserveTextSecondary
import com.example.reserve.ui.theme.StatusApprovedBg
import com.example.reserve.ui.theme.StatusApprovedText
import com.example.reserve.ui.theme.StatusClaimedBg
import com.example.reserve.ui.theme.StatusClaimedText
import com.example.reserve.ui.theme.StatusCompletedBg
import com.example.reserve.ui.theme.StatusCompletedText
import com.example.reserve.ui.theme.StatusPendingBg
import com.example.reserve.ui.theme.StatusPendingText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDashboardScreen(
    onNavigateToDonateFood: () -> Unit,
    onNavigateToMyDonations: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DonorDashboardViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(LightSageGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Eco,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "RESERVE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = EmeraldGreenPrimary
                                )
                            )
                            Text(
                                text = "Donor Portal",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ReserveTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = ForestGreen
                        )
                    }
                    val profile = uiState.profile
                    ProfileAvatar(
                        initials = profile?.initials ?: "D",
                        size = 38.dp,
                        onClick = onNavigateToProfile,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ReserveBackgroundLight
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToDonateFood,
                containerColor = ForestGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VolunteerActivism,
                        contentDescription = "Donate Surplus Food",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Donate Food",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        containerColor = ReserveBackgroundLight,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && uiState.profile == null -> {
                    LoadingView(message = "Loading your donor dashboard...")
                }

                uiState.errorMessage != null && uiState.profile == null -> {
                    ErrorStateView(
                        message = uiState.errorMessage ?: "Unknown error",
                        onRetry = { viewModel.loadDashboardData() }
                    )
                }

                else -> {
                    DashboardContent(
                        profile = uiState.profile,
                        stats = uiState.stats,
                        recentDonations = uiState.recentDonations,
                        onNavigateToDonateFood = onNavigateToDonateFood,
                        onNavigateToMyDonations = onNavigateToMyDonations,
                        onNavigateToProfile = onNavigateToProfile
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    profile: DonorProfile?,
    stats: DonorDashboardStats,
    recentDonations: List<DonationItem>,
    onNavigateToDonateFood: () -> Unit,
    onNavigateToMyDonations: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome Header
        item {
            DonorWelcomeCard(
                profile = profile,
                onProfileClick = onNavigateToProfile
            )
        }

        // 2. Donate Surplus Food Banner / CTA Card
        item {
            DonateFoodActionCard(
                onDonateClick = onNavigateToDonateFood
            )
        }

        // 3. Donation Status Summary Section
        item {
            SectionHeader(
                title = "Donation Status",
                subtitle = "Live status breakdown of your food listings"
            )
            DonationStatusGrid(
                stats = stats,
                onStatusClick = { onNavigateToMyDonations() }
            )
        }

        // 4. Impact Summary Banner
        item {
            ImpactBanner(mealsSaved = stats.estimatedMealsSaved)
        }

        // 5. Recent Donations Section ("My Donations")
        item {
            SectionHeader(
                title = "Recent Donations",
                subtitle = "Your active and past food contributions",
                actionText = if (recentDonations.isNotEmpty()) "View All" else null,
                onActionClick = onNavigateToMyDonations
            )
        }

        if (recentDonations.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No food donations yet",
                    description = "When you list surplus food from your kitchen or event, it will appear here for NGOs to claim.",
                    actionText = "Donate Surplus Food",
                    onActionClick = onNavigateToDonateFood
                )
            }
        } else {
            items(recentDonations.take(5), key = { it.id }) { item ->
                DonationListItemCard(
                    donation = item,
                    onClick = onNavigateToMyDonations
                )
            }
            item {
                Spacer(modifier = Modifier.height(60.dp)) // Space for FAB
            }
        }
    }
}

@Composable
private fun DonorWelcomeCard(
    profile: DonorProfile?,
    onProfileClick: () -> Unit
) {
    ReserveCard(
        backgroundColor = Color.White,
        cornerRadius = 18.dp,
        elevation = 2.dp,
        onClick = onProfileClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                initials = profile?.initials ?: "D",
                size = 54.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ReserveTextSecondary,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = profile?.contactName?.ifBlank { "Food Donor" } ?: "Food Donor",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ReserveTextPrimary,
                        fontSize = 18.sp
                    )
                )
                val orgName = profile?.organizationName
                if (!orgName.isNullOrBlank()) {
                    Text(
                        text = "$orgName • ${profile.donorType.displayName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DonateFoodActionCard(
    onDonateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            EmeraldGreenPrimary,
                            ForestGreen,
                            MintAccent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.VolunteerActivism,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Reduce Food Waste",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Have Surplus Food Today?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Connect with verified NGOs nearby and ensure edible food feeds people in need instead of going to waste.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                ReservePrimaryButton(
                    text = "Donate Surplus Food",
                    onClick = onDonateClick,
                    leadingIcon = Icons.Outlined.VolunteerActivism,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DonationStatusGrid(
    stats: DonorDashboardStats,
    onStatusClick: (DonationStatus) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricStatCard(
                count = stats.pendingCount,
                label = "Pending",
                icon = Icons.Outlined.HourglassEmpty,
                accentColor = StatusPendingText,
                backgroundColor = StatusPendingBg,
                modifier = Modifier.weight(1f),
                onClick = { onStatusClick(DonationStatus.PENDING) }
            )
            MetricStatCard(
                count = stats.approvedCount,
                label = "Approved",
                icon = Icons.Outlined.CheckCircle,
                accentColor = StatusApprovedText,
                backgroundColor = StatusApprovedBg,
                modifier = Modifier.weight(1f),
                onClick = { onStatusClick(DonationStatus.APPROVED) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricStatCard(
                count = stats.claimedCount,
                label = "Claimed",
                icon = Icons.Outlined.LocalShipping,
                accentColor = StatusClaimedText,
                backgroundColor = StatusClaimedBg,
                modifier = Modifier.weight(1f),
                onClick = { onStatusClick(DonationStatus.CLAIMED) }
            )
            MetricStatCard(
                count = stats.completedCount,
                label = "Completed",
                icon = Icons.Outlined.TaskAlt,
                accentColor = StatusCompletedText,
                backgroundColor = StatusCompletedBg,
                modifier = Modifier.weight(1f),
                onClick = { onStatusClick(DonationStatus.COMPLETED) }
            )
        }
    }
}

@Composable
private fun ImpactBanner(mealsSaved: Int) {
    ReserveCard(
        backgroundColor = LightSageGreen,
        borderColor = MintAccent.copy(alpha = 0.4f),
        cornerRadius = 16.dp,
        contentPadding = 14.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ForestGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Eco,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "~$mealsSaved Meals Rescued",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = "Thank you for contributing to zero food waste and zero hunger.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ReserveTextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun DonationListItemCard(
    donation: DonationItem,
    onClick: () -> Unit
) {
    ReserveCard(
        backgroundColor = ReserveSurfaceLight,
        cornerRadius = 14.dp,
        contentPadding = 14.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = donation.foodTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ReserveTextPrimary,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${donation.category} • ${donation.quantity}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ForestGreen,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                )
            }
            StatusBadge(status = donation.status)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = ReserveTextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = donation.pickupTimeWindow,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ReserveTextSecondary,
                    fontSize = 11.sp
                )
            )
        }

        if (donation.ngoClaimantName != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = StatusClaimedText,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Claimed by: ${donation.ngoClaimantName}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = StatusClaimedText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
