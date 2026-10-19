package com.example.reserve.ui.screens.ngo.fooddetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    donationId: String,
    onNavigateBack: () -> Unit,
    onRequestSuccess: () -> Unit,
    foodDetailViewModel: FoodDetailViewModel = viewModel()
) {
    val uiState by foodDetailViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(donationId) {
        foodDetailViewModel.loadDonation(donationId)
    }

    LaunchedEffect(uiState.requestSuccess) {
        if (uiState.requestSuccess) {
            snackbarHostState.showSnackbar("Food requested successfully!")
        }
    }

    LaunchedEffect(uiState.requestError) {
        uiState.requestError?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            foodDetailViewModel.clearRequestError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingView(modifier = Modifier.padding(paddingValues))
            uiState.errorMessage != null -> ErrorStateView(
                message = uiState.errorMessage!!,
                onRetry = { foodDetailViewModel.loadDonation(donationId) },
                modifier = Modifier.padding(paddingValues)
            )
            uiState.donation != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val donation = uiState.donation!!

                    ReserveCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = donation.foodName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(status = donation.status)
                        }
                    }

                    if (donation.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(donation.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Food image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(MaterialTheme.shapes.large),
                            contentScale = ContentScale.Crop
                        )
                    }

                    ReserveCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            HorizontalDivider()
                            DetailRow(Icons.Outlined.Category, "Food Type", donation.foodType.replace("_", " "))
                            DetailRow(Icons.Outlined.Inventory2, "Quantity", donation.quantity)
                            if (donation.description.isNotBlank()) {
                                DetailRow(Icons.Outlined.Description, "Description", donation.description)
                            }
                            DetailRow(Icons.Outlined.LocationOn, "Pickup Location", donation.pickupLocation)
                            if (donation.preparedTime.isNotBlank()) {
                                DetailRow(Icons.Outlined.Schedule, "Prepared Time", donation.preparedTime)
                            }
                            DetailRow(Icons.Outlined.AccessTime, "Expiry Time", donation.expiryTime)
                            DetailRow(Icons.Outlined.Fastfood, "Posted", formatTimestamp(donation.createdAt))
                        }
                    }

                    if (donation.status == DonationStatus.AVAILABLE.name && !uiState.alreadyRequested) {
                        ReservePrimaryButton(
                            text = "Request Food",
                            onClick = { foodDetailViewModel.requestFood() },
                            isLoading = uiState.isRequesting,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (uiState.alreadyRequested) {
                        ReserveCard {
                            Text(
                                text = "You have already requested this donation",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        ReserveCard {
                            Text(
                                text = "This donation is no longer available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 12.dp, top = 2.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(timestamp))
    } catch (_: Exception) { "" }
}
