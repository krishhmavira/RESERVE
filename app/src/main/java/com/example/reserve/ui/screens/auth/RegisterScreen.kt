package com.example.reserve.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.ui.components.ReservePrimaryButton
import com.example.reserve.ui.components.ReserveTextField

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val state by authViewModel.registerState.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.authEvent.collect { event ->
            if (event is AuthEvent.RegisterSuccess) {
                onRegisterSuccess()
            }
        }
    }

    val darkBg = Color(0xFF0B1A12)
    val deepGreen = Color(0xFF1B4332)
    val midGreen = Color(0xFF2D6A4F)
    val lightGreen = Color(0xFF40916C)
    val tealBlue = Color(0xFF065A60)
    val softBlue = Color(0xFF0A9396)
    val plateGreen1 = Color(0xFF52B788)
    val bgColor = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Dark header with circles
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val w = size.width
                val h = size.height

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(darkBg, Color(0xFF112D1B))
                    )
                )

                // Decorative plate circles
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(plateGreen1.copy(alpha = 0.2f), midGreen.copy(alpha = 0.05f)),
                        center = Offset(w * 0.85f, h * 0.1f),
                        radius = w * 0.25f
                    ),
                    radius = w * 0.25f,
                    center = Offset(w * 0.85f, h * 0.1f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(softBlue.copy(alpha = 0.15f), tealBlue.copy(alpha = 0.04f)),
                        center = Offset(w * 0.1f, h * 0.8f),
                        radius = w * 0.2f
                    ),
                    radius = w * 0.2f,
                    center = Offset(w * 0.1f, h * 0.8f)
                )
                drawCircle(
                    color = lightGreen.copy(alpha = 0.08f),
                    radius = w * 0.12f,
                    center = Offset(w * 0.55f, h * 0.15f)
                )

                // Bottom wave transition
                val wavePath = Path().apply {
                    moveTo(0f, h * 0.78f)
                    cubicTo(w * 0.3f, h * 1.0f, w * 0.7f, h * 0.65f, w, h * 0.85f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = wavePath,
                    color = bgColor
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Eco,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = plateGreen1
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Join RESERVE and help reduce food waste",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = midGreen,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReserveTextField(
                value = state.name,
                onValueChange = authViewModel::updateRegisterName,
                label = "Full Name",
                leadingIcon = Icons.Outlined.Person,
                errorMessage = state.nameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReserveTextField(
                value = state.email,
                onValueChange = authViewModel::updateRegisterEmail,
                label = "Email",
                leadingIcon = Icons.Outlined.Email,
                errorMessage = state.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReserveTextField(
                value = state.phone,
                onValueChange = authViewModel::updateRegisterPhone,
                label = "Phone Number",
                leadingIcon = Icons.Outlined.Phone,
                errorMessage = state.phoneError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Security",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = midGreen,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReserveTextField(
                value = state.password,
                onValueChange = authViewModel::updateRegisterPassword,
                label = "Password",
                leadingIcon = Icons.Outlined.Lock,
                errorMessage = state.passwordError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReserveTextField(
                value = state.confirmPassword,
                onValueChange = authViewModel::updateRegisterConfirmPassword,
                label = "Confirm Password",
                leadingIcon = Icons.Outlined.Lock,
                errorMessage = state.confirmPasswordError,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "I am a",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = midGreen,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RoleCard(
                    title = "Food Donor",
                    description = "Share surplus food",
                    icon = Icons.Outlined.VolunteerActivism,
                    isSelected = state.selectedRole == "DONOR",
                    onClick = { authViewModel.updateRegisterRole("DONOR") },
                    modifier = Modifier.weight(1f),
                    accentColor = midGreen
                )
                RoleCard(
                    title = "NGO / Community",
                    description = "Collect & distribute",
                    icon = Icons.Rounded.Handshake,
                    isSelected = state.selectedRole == "NGO",
                    onClick = { authViewModel.updateRegisterRole("NGO") },
                    modifier = Modifier.weight(1f),
                    accentColor = tealBlue
                )
            }

            if (state.roleError != null) {
                Text(
                    text = state.roleError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            ReservePrimaryButton(
                text = "Create Account",
                onClick = { authViewModel.register() },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = midGreen,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF2D6A4F)
) {
    val borderColor = if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = contentColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}
