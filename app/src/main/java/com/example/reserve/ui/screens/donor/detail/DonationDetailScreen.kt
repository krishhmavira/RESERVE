package com.example.reserve.ui.screens.donor.detail

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.data.model.Donation
import com.example.reserve.data.model.DonationStatus
import com.example.reserve.data.model.FoodRequest
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.DetailRow
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.ReserveOutlinedButton
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.components.StatusBadge
import com.example.reserve.ui.components.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationDetailScreen(
    donationId: String,
    onNavigateBack: () -> Unit,
    donationDetailViewModel: DonationDetailViewModel = viewModel()
) {
    val uiState by donationDetailViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(donationId) {
        donationDetailViewModel.loadDonation(donationId)
    }

    LaunchedEffect(uiState.updateMessage) {
        uiState.updateMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            donationDetailViewModel.clearUpdateMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donation Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            uiState.errorMessage != null -> {
                ErrorStateView(
                    message = uiState.errorMessage!!,
                    onRetry = { donationDetailViewModel.loadDonation(donationId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.donation != null -> {
                DonationDetailContent(
                    donation = uiState.donation!!,
                    incomingRequests = uiState.incomingRequests,
                    isUpdating = uiState.isUpdating,
                    onCancel = { donationDetailViewModel.cancelDonation() },
                    onMarkCompleted = { donationDetailViewModel.markCompleted() },
                    onApproveRequest = { donationDetailViewModel.approveRequest(it) },
                    onRejectRequest = { donationDetailViewModel.rejectRequest(it) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DonationDetailContent(
    donation: Donation,
    incomingRequests: List<FoodRequest>,
    isUpdating: Boolean,
    onCancel: () -> Unit,
    onMarkCompleted: () -> Unit,
    onApproveRequest: (FoodRequest) -> Unit,
    onRejectRequest: (FoodRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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

        // Image
        if (donation.imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(donation.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Donation image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
        }

        // Details
        ReserveCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                HorizontalDivider()
                DetailRow(
                    icon = Icons.Outlined.Category,
                    label = "Food Type",
                    value = donation.foodType.replace("_", " ")
                )
                DetailRow(
                    icon = Icons.Outlined.Inventory2,
                    label = "Quantity",
                    value = donation.quantity
                )
                if (donation.description.isNotBlank()) {
                    DetailRow(
                        icon = Icons.Outlined.Description,
                        label = "Description",
                        value = donation.description
                    )
                }
                DetailRow(
                    icon = Icons.Outlined.LocationOn,
                    label = "Pickup Location",
                    value = donation.pickupLocation
                )
                if (donation.preparedTime.isNotBlank()) {
                    DetailRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Prepared Time",
                        value = donation.preparedTime
                    )
                }
                DetailRow(
                    icon = Icons.Outlined.AccessTime,
                    label = "Expiry Time",
                    value = donation.expiryTime
                )
                DetailRow(
                    icon = Icons.Outlined.Fastfood,
                    label = "Created",
                    value = formatTimestamp(donation.createdAt)
                )
            }
        }

        // Incoming Requests
        val hasRequests = incomingRequests.isNotEmpty()
        val expectsRequests = donation.status == DonationStatus.REQUESTED.name ||
            donation.status == DonationStatus.APPROVED.name

        if (hasRequests) {
            IncomingRequestsSection(
                requests = incomingRequests,
                isUpdating = isUpdating,
                onApprove = onApproveRequest,
                onReject = onRejectRequest
            )
        } else if (expectsRequests) {
            ReserveCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Loading incoming requests...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Actions
        val canCancel = donation.status == DonationStatus.AVAILABLE.name
        val canComplete = donation.status == DonationStatus.COLLECTED.name ||
            donation.status == DonationStatus.APPROVED.name

        if (canCancel || canComplete) {
            ReserveCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.tertiaryContainer,
                                    MaterialTheme.shapes.medium
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Actions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (canComplete) {
                        ReservePrimaryButton(
                            text = "Mark as Completed",
                            onClick = onMarkCompleted,
                            isLoading = isUpdating
                        )
                    }
                    if (canCancel) {
                        ReserveOutlinedButton(
                            text = "Cancel Donation",
                            onClick = onCancel,
                            enabled = !isUpdating
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun IncomingRequestsSection(
    requests: List<FoodRequest>,
    isUpdating: Boolean,
    onApprove: (FoodRequest) -> Unit,
    onReject: (FoodRequest) -> Unit
) {
    ReserveCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Incoming Requests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "${requests.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider()
            val pendingCount = requests.count { it.status == RequestStatus.PENDING.name }
            requests.forEachIndexed { index, request ->
                RequestItem(
                    request = request,
                    label = if (requests.size > 1) "Request ${index + 1}" else "NGO Request",
                    isUpdating = isUpdating,
                    onApprove = { onApprove(request) },
                    onReject = { onReject(request) }
                )
                if (index < requests.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
            if (pendingCount > 1) {
                Text(
                    text = "$pendingCount requests awaiting your decision",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RequestItem(
    request: FoodRequest,
    label: String,
    isUpdating: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            StatusBadge(status = request.status)
        }

        if (request.requestTime > 0) {
            Text(
                text = "Requested: ${formatTimestamp(request.requestTime)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (request.status == RequestStatus.PENDING.name) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReservePrimaryButton(
                    text = "Approve",
                    onClick = onApprove,
                    isLoading = isUpdating,
                    modifier = Modifier.weight(1f)
                )
                ReserveOutlinedButton(
                    text = "Reject",
                    onClick = onReject,
                    enabled = !isUpdating,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

