package com.example.reserve.ui.screens.ngo.requestdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Warning
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.ui.components.DetailRow
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.ReserveOutlinedButton
import com.example.reserve.ui.components.StatusBadge
import com.example.reserve.ui.components.formatTimestamp

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
                    // Status header card
                    StatusHeaderCard(
                        foodName = donation?.foodName ?: "Food Request",
                        requestStatus = request.status,
                        donationStatus = donation?.status
                    )

                    // Status context message
                    StatusContextMessage(
                        requestStatus = request.status,
                        donationStatus = donation?.status
                    )

                    // Request info
                    ReserveCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SectionHeader(
                                title = "Request Info",
                                icon = Icons.Outlined.Info,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                iconColor = MaterialTheme.colorScheme.secondary
                            )
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
                                SectionHeader(
                                    title = "Food Details",
                                    icon = Icons.Outlined.Fastfood,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    iconColor = MaterialTheme.colorScheme.tertiary
                                )
                                HorizontalDivider()
                                DetailRow(Icons.Outlined.Category, "Food Type", donation.foodType.replace("_", " "))
                                DetailRow(Icons.Outlined.Inventory2, "Quantity", donation.quantity)
                                DetailRow(Icons.Outlined.LocationOn, "Pickup Location", donation.pickupLocation)
                                DetailRow(Icons.Outlined.AccessTime, "Expiry", donation.expiryTime)
                            }
                        }
                    }

                    // Actions
                    AnimatedVisibility(
                        visible = request.status == RequestStatus.PENDING.name,
                        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                        exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                    ) {
                        ReserveCard {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SectionHeader(
                                    title = "Actions",
                                    icon = Icons.Outlined.Cancel,
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    iconColor = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "You can cancel this request while it's still pending. The donation will become available for others.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                ReserveOutlinedButton(
                                    text = if (uiState.isUpdating) "Cancelling..." else "Cancel Request",
                                    onClick = { requestDetailViewModel.cancelRequest() },
                                    enabled = !uiState.isUpdating
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
private fun StatusHeaderCard(
    foodName: String,
    requestStatus: String,
    donationStatus: String?
) {
    val statusIcon = when (requestStatus) {
        RequestStatus.PENDING.name -> Icons.Outlined.HourglassEmpty
        RequestStatus.APPROVED.name, RequestStatus.COLLECTED.name, RequestStatus.COMPLETED.name ->
            Icons.Outlined.CheckCircle
        RequestStatus.REJECTED.name -> Icons.Outlined.Close
        else -> Icons.Outlined.Info
    }
    val statusContainerColor = when (requestStatus) {
        RequestStatus.PENDING.name -> MaterialTheme.colorScheme.secondaryContainer
        RequestStatus.APPROVED.name, RequestStatus.COLLECTED.name -> MaterialTheme.colorScheme.primaryContainer
        RequestStatus.COMPLETED.name -> MaterialTheme.colorScheme.tertiaryContainer
        RequestStatus.REJECTED.name -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    ReserveCard(containerColor = statusContainerColor.copy(alpha = 0.5f)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(statusContainerColor, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = requestStatus)
                    if (donationStatus != null) {
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.outline
                        )
                        StatusBadge(status = donationStatus)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusContextMessage(
    requestStatus: String,
    donationStatus: String?
) {
    val message: String?
    val icon: androidx.compose.ui.graphics.vector.ImageVector
    val containerColor: androidx.compose.ui.graphics.Color
    val contentColor: androidx.compose.ui.graphics.Color

    when (requestStatus) {
        RequestStatus.PENDING.name -> {
            if (donationStatus != null && donationStatus != DonationStatus.REQUESTED.name) {
                message = when (donationStatus) {
                    DonationStatus.APPROVED.name ->
                        "This donation has been approved for another request. Your request is still pending."
                    DonationStatus.EXPIRED.name ->
                        "This donation has expired. Your request may no longer be fulfilled."
                    DonationStatus.COMPLETED.name ->
                        "This donation has been completed."
                    else ->
                        "The donation status has changed. Your request is still pending."
                }
                icon = Icons.Outlined.Warning
                containerColor = MaterialTheme.colorScheme.surfaceVariant
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            } else return
        }
        RequestStatus.APPROVED.name -> {
            message = "Your request has been approved! Please arrange pickup at the location shown below."
            icon = Icons.Outlined.CheckCircle
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        }
        RequestStatus.COLLECTED.name -> {
            message = "Food has been collected. Thank you for serving the community!"
            icon = Icons.Outlined.CheckCircle
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        }
        RequestStatus.COMPLETED.name -> {
            message = "This request has been completed successfully. Thank you!"
            icon = Icons.Outlined.CheckCircle
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        }
        RequestStatus.REJECTED.name -> {
            message = "This request was declined or cancelled. You may browse other available donations."
            icon = Icons.Outlined.Close
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        }
        else -> return
    }

    ReserveCard(containerColor = containerColor) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(containerColor, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
