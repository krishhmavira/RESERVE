package com.example.reserve.ui.screens.donor.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.data.model.FoodType
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.components.ReserveTextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddDonationScreen(
    onNavigateBack: () -> Unit,
    onDonationCreated: () -> Unit,
    addDonationViewModel: AddDonationViewModel = viewModel()
) {
    val uiState by addDonationViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onDonationCreated()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Donation") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            ReserveTextField(
                value = uiState.foodName,
                onValueChange = { addDonationViewModel.updateFoodName(it) },
                label = "Food Name",
                leadingIcon = Icons.Outlined.Fastfood,
                errorMessage = uiState.foodNameError
            )

            // Food type chips
            Column {
                Text(
                    text = "Food Type",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.foodTypeError != null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FoodType.entries.forEach { type ->
                        FilterChip(
                            selected = uiState.foodType == type,
                            onClick = { addDonationViewModel.updateFoodType(type) },
                            label = {
                                Text(type.name.replace("_", " "))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                if (uiState.foodTypeError != null) {
                    Text(
                        text = uiState.foodTypeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            ReserveTextField(
                value = uiState.quantity,
                onValueChange = { addDonationViewModel.updateQuantity(it) },
                label = "Quantity (e.g. 5 plates, 2 kg)",
                leadingIcon = Icons.Outlined.Inventory2,
                errorMessage = uiState.quantityError
            )

            ReserveTextField(
                value = uiState.description,
                onValueChange = { addDonationViewModel.updateDescription(it) },
                label = "Description (optional)",
                leadingIcon = Icons.Outlined.Description,
                singleLine = false,
                maxLines = 3
            )

            ReserveTextField(
                value = uiState.pickupLocation,
                onValueChange = { addDonationViewModel.updatePickupLocation(it) },
                label = "Pickup Location",
                leadingIcon = Icons.Outlined.LocationOn,
                errorMessage = uiState.pickupLocationError
            )

            ReserveTextField(
                value = uiState.preparedTime,
                onValueChange = { addDonationViewModel.updatePreparedTime(it) },
                label = "Prepared Time (optional)",
                leadingIcon = Icons.Outlined.Schedule
            )

            ReserveTextField(
                value = uiState.expiryTime,
                onValueChange = { addDonationViewModel.updateExpiryTime(it) },
                label = "Expiry Time (e.g. Today 8 PM)",
                leadingIcon = Icons.Outlined.AccessTime,
                errorMessage = uiState.expiryTimeError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ReservePrimaryButton(
                text = "Submit Donation",
                onClick = { addDonationViewModel.submit() },
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
