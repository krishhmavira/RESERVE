package com.example.reserve.ui.donor.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.data.model.DonorProfile
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.ProfileAvatar
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.components.SectionHeader
import com.example.reserve.ui.theme.EmeraldGreenPrimary
import com.example.reserve.ui.theme.ForestGreen
import com.example.reserve.ui.theme.LightSageGreen
import com.example.reserve.ui.theme.ReserveBackgroundLight
import com.example.reserve.ui.theme.ReserveSurfaceLight
import com.example.reserve.ui.theme.ReserveTextPrimary
import com.example.reserve.ui.theme.ReserveTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: DonorProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Donor Profile",
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
                actions = {
                    IconButton(onClick = onNavigateToEditProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Profile",
                            tint = ForestGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ReserveBackgroundLight
                )
            )
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
                    LoadingView(message = "Loading donor profile...")
                }

                uiState.errorMessage != null && uiState.profile == null -> {
                    ErrorStateView(
                        message = uiState.errorMessage ?: "Failed to load profile",
                        onRetry = { viewModel.observeProfile() }
                    )
                }

                uiState.profile != null -> {
                    ProfileContent(
                        profile = uiState.profile!!,
                        onEditClick = onNavigateToEditProfile
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: DonorProfile,
    onEditClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card with Large Avatar
        item {
            ReserveCard(
                backgroundColor = Color.White,
                cornerRadius = 20.dp,
                elevation = 2.dp,
                contentPadding = 20.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileAvatar(
                        initials = profile.initials,
                        size = 80.dp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = profile.organizationName.ifBlank { profile.contactName },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ReserveTextPrimary,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contact: ${profile.contactName}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ReserveTextSecondary,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Donor Type Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(LightSageGreen)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = profile.donorType.displayName,
                                color = EmeraldGreenPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }

                        // Verified Status Chip
                        if (profile.isVerified) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(ForestGreen)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Verified Donor",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Organization Information Section
        item {
            SectionHeader(title = "Organization Details")
            ReserveCard(
                backgroundColor = ReserveSurfaceLight,
                cornerRadius = 16.dp,
                contentPadding = 16.dp
            ) {
                ProfileDetailRow(
                    icon = Icons.Outlined.Business,
                    label = "Organization Name",
                    value = profile.organizationName.ifBlank { "Not set" }
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileDetailRow(
                    icon = Icons.Outlined.VolunteerActivism,
                    label = "Donor Category",
                    value = profile.donorType.displayName
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileDetailRow(
                    icon = Icons.Outlined.LocationOn,
                    label = "Registered Pickup Address",
                    value = profile.address.ifBlank { "Not set" }
                )
            }
        }

        // 3. Contact Details Section
        item {
            SectionHeader(title = "Contact Information")
            ReserveCard(
                backgroundColor = ReserveSurfaceLight,
                cornerRadius = 16.dp,
                contentPadding = 16.dp
            ) {
                ProfileDetailRow(
                    icon = Icons.Outlined.Person,
                    label = "Contact Person",
                    value = profile.contactName.ifBlank { "Not set" }
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileDetailRow(
                    icon = Icons.Outlined.Email,
                    label = "Email Address",
                    value = profile.email.ifBlank { "Not set" },
                    helperNote = "Authentication email (read-only)"
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileDetailRow(
                    icon = Icons.Outlined.Phone,
                    label = "Phone Number",
                    value = profile.phone.ifBlank { "Not set" }
                )
            }
        }

        // 4. Activity & Impact Section
        item {
            SectionHeader(title = "Account & Activity")
            ReserveCard(
                backgroundColor = ReserveSurfaceLight,
                cornerRadius = 16.dp,
                contentPadding = 16.dp
            ) {
                ProfileDetailRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Member Since",
                    value = profile.memberSince
                )
                Spacer(modifier = Modifier.height(14.dp))
                ProfileDetailRow(
                    icon = Icons.Outlined.VolunteerActivism,
                    label = "Total Food Donations",
                    value = "${profile.totalDonationsCount} listings completed"
                )
            }
        }

        // 5. Edit Profile Button
        item {
            Spacer(modifier = Modifier.height(4.dp))
            ReservePrimaryButton(
                text = "Edit Profile",
                onClick = onEditClick,
                leadingIcon = Icons.Outlined.Edit
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    helperNote: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(LightSageGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ReserveTextSecondary,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ReserveTextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            )
            if (helperNote != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = helperNote,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ReserveTextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
