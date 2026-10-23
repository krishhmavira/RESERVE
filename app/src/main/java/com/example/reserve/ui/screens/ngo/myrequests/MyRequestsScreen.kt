package com.example.reserve.ui.screens.ngo.myrequests

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.data.model.FoodRequest
import com.example.reserve.data.model.RequestStatus
import com.example.reserve.ui.components.EmptyStateView
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.StatusBadge
import com.example.reserve.ui.components.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRequestsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRequestDetail: (String) -> Unit,
    myRequestsViewModel: MyRequestsViewModel = viewModel()
) {
    val uiState by myRequestsViewModel.uiState.collectAsState()
    val filters = listOf("ALL") + RequestStatus.entries.map { it.name }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.updateMessage) {
        uiState.updateMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            myRequestsViewModel.clearUpdateMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Requests")
                        if (!uiState.isLoading && uiState.errorMessage == null) {
                            Text(
                                text = "${uiState.requests.size} total request${if (uiState.requests.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { myRequestsViewModel.setFilter(filter) },
                        label = {
                            Text(
                                text = if (filter == "ALL") "All" else filter.replace("_", " "),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            when {
                uiState.isLoading -> LoadingView()
                uiState.errorMessage != null -> ErrorStateView(
                    message = uiState.errorMessage!!,
                    onRetry = { myRequestsViewModel.retry() }
                )
                uiState.filteredRequests.isEmpty() -> EmptyStateView(
                    title = if (uiState.selectedFilter == "ALL")
                        "No requests yet"
                    else
                        "No ${uiState.selectedFilter.replace("_", " ").lowercase()} requests",
                    message = if (uiState.selectedFilter == "ALL")
                        "Browse available food and make your first request"
                    else
                        "Try selecting a different filter",
                    icon = Icons.Outlined.VolunteerActivism
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${uiState.filteredRequests.size} request${if (uiState.filteredRequests.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(
                        items = uiState.filteredRequests,
                        key = { it.requestId }
                    ) { request ->
                        RequestCard(
                            request = request,
                            foodName = uiState.donationNames[request.donationId] ?: "Loading...",
                            onClick = { onNavigateToRequestDetail(request.requestId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestCard(request: FoodRequest, foodName: String, onClick: () -> Unit) {
    val statusInfo = requestStatusInfo(request.status)

    ReserveCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            statusInfo.containerColor,
                            MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusInfo.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = statusInfo.iconColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = foodName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = statusInfo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusInfo.iconColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimestamp(request.requestTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(status = request.status)
        }
    }
}

private data class RequestStatusDisplayInfo(
    val icon: ImageVector,
    val description: String,
    val containerColor: Color,
    val iconColor: Color
)

@Composable
private fun requestStatusInfo(status: String): RequestStatusDisplayInfo {
    val scheme = MaterialTheme.colorScheme
    return when (status) {
        RequestStatus.PENDING.name -> RequestStatusDisplayInfo(
            icon = Icons.Outlined.HourglassEmpty,
            description = "Awaiting donor response",
            containerColor = scheme.secondaryContainer,
            iconColor = scheme.secondary
        )
        RequestStatus.APPROVED.name -> RequestStatusDisplayInfo(
            icon = Icons.Outlined.CheckCircle,
            description = "Approved — arrange pickup",
            containerColor = scheme.primaryContainer,
            iconColor = scheme.primary
        )
        RequestStatus.COLLECTED.name -> RequestStatusDisplayInfo(
            icon = Icons.Outlined.CheckCircle,
            description = "Food collected",
            containerColor = scheme.primaryContainer,
            iconColor = scheme.primary
        )
        RequestStatus.COMPLETED.name -> RequestStatusDisplayInfo(
            icon = Icons.Outlined.CheckCircle,
            description = "Completed",
            containerColor = scheme.tertiaryContainer,
            iconColor = scheme.tertiary
        )
        RequestStatus.REJECTED.name -> RequestStatusDisplayInfo(
            icon = Icons.Outlined.ThumbDown,
            description = "Declined",
            containerColor = scheme.errorContainer,
            iconColor = scheme.error
        )
        else -> RequestStatusDisplayInfo(
            icon = Icons.Outlined.Receipt,
            description = status.replace("_", " "),
            containerColor = scheme.surfaceVariant,
            iconColor = scheme.onSurfaceVariant
        )
    }
}
