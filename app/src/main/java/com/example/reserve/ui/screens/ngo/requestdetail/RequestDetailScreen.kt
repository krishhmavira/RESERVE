package com.example.reserve.ui.screens.ngo.requestdetail

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
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.ReserveOutlinedButton
import com.example.reserve.ui.components.StatusBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    requestDetailViewModel: RequestDetailViewModel = viewModel()
) {
    val uiState by requestDetailViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(requestId) {
        requestDetailViewModel.loadRequest(requestId)
    }

    LaunchedEffect(uiState.updateMessage) {
        uiState.updateMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            requestDetailViewModel.clearUpdateMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details") },
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
                onRetry = { requestDetailViewModel.loadRequest(requestId) },
                modifier = Modifier.padding(paddingValues)
            )
            uiState.request != null -> {
                val request = uiState.request!!
                val donation = uiState.donation

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Request status header
                    ReserveCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Request Status",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = donation?.foodName ?: "Food Request",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            StatusBadge(status = request.status)
                        }
                        if (donation != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Donation Status",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                StatusBadge(status = donation.status)
                            }
                        }
                    }

                    // Request info
                    ReserveCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Request Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            HorizontalDivider()
                            DetailRow(Icons.Outlined.Schedule, "Requested", formatTimestamp(request.requestTime))
                            DetailRow(Icons.Outlined.Fastfood, "Status", request.status.replace("_", " "))
                        }
                    }

                    // Food info
                    if (donation != null) {
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
                                Text("Food Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                HorizontalDivider()
                                DetailRow(Icons.Outlined.Category, "Food Type", donation.foodType.replace("_", " "))
                                DetailRow(Icons.Outlined.Inventory2, "Quantity", donation.quantity)
                                DetailRow(Icons.Outlined.LocationOn, "Pickup Location", donation.pickupLocation)
                                DetailRow(Icons.Outlined.AccessTime, "Expiry", donation.expiryTime)
                            }
                        }
                    }

                    // Actions
                    if (request.status == RequestStatus.PENDING.name) {
                        ReserveCard {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                ReserveOutlinedButton(
                                    text = "Cancel Request",
                                    onClick = { requestDetailViewModel.cancelRequest() },
                                    enabled = !uiState.isUpdating
                                )
                            }
                        }
                    }

                    // Status messages
                    when (request.status) {
                        RequestStatus.PENDING.name -> {
                            if (donation != null && donation.status != DonationStatus.REQUESTED.name) {
                                ReserveCard {
                                    Text(
                                        text = when (donation.status) {
                                            DonationStatus.APPROVED.name ->
                                                "This donation has been approved for another request. Your request is still pending."
                                            DonationStatus.EXPIRED.name ->
                                                "This donation has expired. Your request may no longer be fulfilled."
                                            DonationStatus.COMPLETED.name ->
                                                "This donation has been completed."
                                            else ->
                                                "The donation status has changed. Your request is still pending."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        RequestStatus.APPROVED.name -> {
                            ReserveCard {
                                Text(
                                    text = "Your request has been approved! Please arrange pickup.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        RequestStatus.COLLECTED.name -> {
                            ReserveCard {
                                Text(
                                    text = "Food has been collected. Thank you!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        RequestStatus.COMPLETED.name -> {
                            ReserveCard {
                                Text(
                                    text = "This request has been completed. Thank you for serving the community!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        RequestStatus.REJECTED.name -> {
                            ReserveCard {
                                Text(
                                    text = "This request was declined or cancelled.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
