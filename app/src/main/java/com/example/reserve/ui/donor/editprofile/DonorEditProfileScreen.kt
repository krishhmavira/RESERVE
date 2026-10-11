package com.example.reserve.ui.donor.editprofile

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.data.model.DonorType
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.ReserveOutlinedButton
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.components.ReserveTextField
import com.example.reserve.ui.theme.ForestGreen
import com.example.reserve.ui.theme.ReserveBackgroundLight
import com.example.reserve.ui.theme.ReserveCardBorder
import com.example.reserve.ui.theme.ReserveTextPrimary
import com.example.reserve.ui.theme.ReserveTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorEditProfileScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: DonorEditProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.generalErrorMessage) {
        val error = uiState.generalErrorMessage
        if (!error.isNullOrBlank()) {
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Donor Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ReserveTextPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !uiState.isSaving
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Cancel and go back",
                            tint = ReserveTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ReserveBackgroundLight
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = ReserveBackgroundLight,
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading && uiState.initialProfile == null) {
            LoadingView(
                message = "Loading profile details...",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            EditProfileForm(
                uiState = uiState,
                onContactNameChange = viewModel::onContactNameChanged,
                onOrganizationNameChange = viewModel::onOrganizationNameChanged,
                onPhoneChange = viewModel::onPhoneChanged,
                onAddressChange = viewModel::onAddressChanged,
                onDonorTypeChange = viewModel::onDonorTypeChanged,
                onSaveClick = { viewModel.saveProfile(onSuccess = onSaveSuccess) },
                onCancelClick = onNavigateBack,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun EditProfileForm(
    uiState: DonorEditProfileUiState,
    onContactNameChange: (String) -> Unit,
    onOrganizationNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onDonorTypeChange: (DonorType) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var donorTypeDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Keep your organization and contact information updated so NGOs can coordinate food pickups seamlessly.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ReserveTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )
        }

        // Contact Person Name
        item {
            ReserveTextField(
                value = uiState.contactName,
                onValueChange = onContactNameChange,
                label = "Contact Person Name *",
                placeholder = "e.g. Aarav Sharma",
                leadingIcon = Icons.Outlined.Person,
                errorMessage = uiState.contactNameError,
                enabled = !uiState.isSaving
            )
        }

        // Organization Name
        item {
            ReserveTextField(
                value = uiState.organizationName,
                onValueChange = onOrganizationNameChange,
                label = "Organization / Business Name *",
                placeholder = "e.g. The Grand Horizon Hotel",
                leadingIcon = Icons.Outlined.Business,
                errorMessage = uiState.organizationNameError,
                enabled = !uiState.isSaving
            )
        }

        // Donor Type Selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Donor Category *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ReserveTextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.donorType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !uiState.isSaving) {
                                donorTypeDropdownExpanded = true
                            },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.VolunteerActivism,
                                contentDescription = null,
                                tint = ForestGreen
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { donorTypeDropdownExpanded = !donorTypeDropdownExpanded },
                                enabled = !uiState.isSaving
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDropDown,
                                    contentDescription = "Select category"
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = ReserveCardBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    DropdownMenu(
                        expanded = donorTypeDropdownExpanded,
                        onDismissRequest = { donorTypeDropdownExpanded = false }
                    ) {
                        DonorType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    onDonorTypeChange(type)
                                    donorTypeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Phone Number
        item {
            ReserveTextField(
                value = uiState.phone,
                onValueChange = onPhoneChange,
                label = "Phone Number *",
                placeholder = "e.g. +91 98765 43210",
                leadingIcon = Icons.Outlined.Phone,
                errorMessage = uiState.phoneError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                enabled = !uiState.isSaving
            )
        }

        // Pickup Address
        item {
            ReserveTextField(
                value = uiState.address,
                onValueChange = onAddressChange,
                label = "Pickup Address *",
                placeholder = "Enter full street address, landmark, and city",
                leadingIcon = Icons.Outlined.LocationOn,
                errorMessage = uiState.addressError,
                singleLine = false,
                minLines = 3,
                maxLines = 4,
                enabled = !uiState.isSaving
            )
        }

        // Account Email (Read-Only)
        item {
            ReserveTextField(
                value = uiState.email,
                onValueChange = {},
                label = "Account Email (Read-Only)",
                leadingIcon = Icons.Outlined.Email,
                readOnly = true,
                helperText = "Authentication email is protected and cannot be changed here."
            )
        }

        // Action Buttons
        item {
            Spacer(modifier = Modifier.height(10.dp))
            ReservePrimaryButton(
                text = "Save Changes",
                onClick = onSaveClick,
                isLoading = uiState.isSaving,
                leadingIcon = Icons.Outlined.Check,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            ReserveOutlinedButton(
                text = "Cancel",
                onClick = onCancelClick,
                enabled = !uiState.isSaving,
                leadingIcon = Icons.Outlined.Close,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
