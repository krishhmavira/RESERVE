package com.example.reserve.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Eco
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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val state by authViewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.authEvent.collect { event ->
            if (event is AuthEvent.LoginSuccess) {
                onLoginSuccess()
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
    val plateGreen2 = Color(0xFF74C69D)
    val plateTeal = Color(0xFF2C7DA0)

    Box(modifier = Modifier.fillMaxSize()) {
        // Dark background with food-plate-like circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Dark gradient background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(darkBg, Color(0xFF112D1B), darkBg),
                    startY = 0f,
                    endY = h
                )
            )

            // Diagonal split like the reference image
            val splitPath = Path().apply {
                moveTo(0f, h * 0.35f)
                cubicTo(w * 0.3f, h * 0.42f, w * 0.7f, h * 0.28f, w, h * 0.38f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = splitPath, color = Color(0xFF081410).copy(alpha = 0.4f))

            // Large plate circle — top right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(plateGreen2.copy(alpha = 0.25f), lightGreen.copy(alpha = 0.08f)),
                    center = Offset(w * 0.82f, h * 0.08f),
                    radius = w * 0.28f
                ),
                radius = w * 0.28f,
                center = Offset(w * 0.82f, h * 0.08f)
            )

            // Medium plate circle — top left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(plateTeal.copy(alpha = 0.22f), tealBlue.copy(alpha = 0.06f)),
                    center = Offset(w * 0.12f, h * 0.05f),
                    radius = w * 0.22f
                ),
                radius = w * 0.22f,
                center = Offset(w * 0.12f, h * 0.05f)
            )

            // Large plate — center left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(midGreen.copy(alpha = 0.3f), deepGreen.copy(alpha = 0.1f)),
                    center = Offset(w * 0.08f, h * 0.25f),
                    radius = w * 0.26f
                ),
                radius = w * 0.26f,
                center = Offset(w * 0.08f, h * 0.25f)
            )

            // Medium plate — right side
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(softBlue.copy(alpha = 0.2f), tealBlue.copy(alpha = 0.06f)),
                    center = Offset(w * 0.88f, h * 0.28f),
                    radius = w * 0.2f
                ),
                radius = w * 0.2f,
                center = Offset(w * 0.88f, h * 0.28f)
            )

            // Small plate — center
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(plateGreen1.copy(alpha = 0.18f), midGreen.copy(alpha = 0.05f)),
                    center = Offset(w * 0.45f, h * 0.15f),
                    radius = w * 0.14f
                ),
                radius = w * 0.14f,
                center = Offset(w * 0.45f, h * 0.15f)
            )

            // Small accent circles
            drawCircle(
                color = plateGreen2.copy(alpha = 0.12f),
                radius = w * 0.08f,
                center = Offset(w * 0.65f, h * 0.05f)
            )
            drawCircle(
                color = plateTeal.copy(alpha = 0.1f),
                radius = w * 0.06f,
                center = Offset(w * 0.35f, h * 0.32f)
            )

            // Plate ring outlines for depth
            drawCircle(
                color = plateGreen1.copy(alpha = 0.1f),
                radius = w * 0.27f,
                center = Offset(w * 0.82f, h * 0.08f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
            drawCircle(
                color = plateTeal.copy(alpha = 0.08f),
                radius = w * 0.25f,
                center = Offset(w * 0.08f, h * 0.25f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
        }

        // Branding on the dark area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Eco,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = plateGreen1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "RESERVE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Rescue Food. Feed Communities.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        // White form card at the bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            ReserveTextField(
                value = state.email,
                onValueChange = authViewModel::updateLoginEmail,
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

            Spacer(modifier = Modifier.height(16.dp))

            ReserveTextField(
                value = state.password,
                onValueChange = authViewModel::updateLoginPassword,
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        authViewModel.login()
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.labelLarge,
                color = tealBlue,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onNavigateToForgotPassword() }
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                text = "Sign In",
                onClick = { authViewModel.login() },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = midGreen,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
