package com.example.reserve.ui.screens.ngo.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reserve.data.model.Donation
import com.example.reserve.ui.components.EmptyStateView
import com.example.reserve.ui.components.ErrorStateView
import com.example.reserve.ui.components.LoadingView
import com.example.reserve.ui.components.ReserveCard
import com.example.reserve.ui.components.StatusBadge
import com.example.reserve.ui.components.formatTimestamp
import com.example.reserve.ui.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoHomeScreen(
    onNavigateToBrowseFood: () -> Unit,
    onNavigateToMyRequests: () -> Unit,
    onNavigateToFoodDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: AuthViewModel,
    ngoHomeViewModel: NgoHomeViewModel = viewModel()
) {
    val uiState by ngoHomeViewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            ngoHomeViewModel.setUserName(user.name)
        }
    }

    LaunchedEffect(uiState.updateMessage) {
        uiState.updateMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            ngoHomeViewModel.clearUpdateMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1B4332), Color(0xFF52B788))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Eco,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "RESERVE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = Color(0xFF1B4332)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onNavigateToProfile) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                    onRetry = { ngoHomeViewModel.retry() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        HeroBanner(
                            userName = uiState.userName,
                            availableCount = uiState.availableCount
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B4332),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GradientStatCard(
                                    label = "Available",
                                    value = uiState.availableCount.toString(),
                                    icon = Icons.Outlined.Fastfood,
                                    gradientColors = listOf(Color(0xFF52B788), Color(0xFF74C69D)),
                                    modifier = Modifier.weight(1f)
                                )
                                GradientStatCard(
                                    label = "Requests",
                                    value = uiState.myRequestsCount.toString(),
                                    icon = Icons.Outlined.Inbox,
                                    gradientColors = listOf(Color(0xFF065A60), Color(0xFF0A9396)),
                                    modifier = Modifier.weight(1f)
                                )
                                GradientStatCard(
                                    label = "Approved",
                                    value = uiState.approvedCount.toString(),
                                    icon = Icons.Outlined.CheckCircle,
                                    gradientColors = listOf(Color(0xFF2D6A4F), Color(0xFF40916C)),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Quick Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B4332),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                QuickActionCard(
                                    title = "Browse Food",
                                    subtitle = "${uiState.availableCount} available",
                                    icon = Icons.Outlined.Search,
                                    gradientColors = listOf(Color(0xFF2D6A4F), Color(0xFF40916C)),
                                    onClick = onNavigateToBrowseFood,
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionCard(
                                    title = "My Requests",
                                    subtitle = "${uiState.myRequestsCount} total",
                                    icon = Icons.Outlined.VolunteerActivism,
                                    gradientColors = listOf(Color(0xFF065A60), Color(0xFF0A9396)),
                                    onClick = onNavigateToMyRequests,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF52B788), Color(0xFF2D6A4F))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Fastfood,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Available Now",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B4332)
                                        )
                                        Text(
                                            text = "${uiState.recentAvailable.size} donation${if (uiState.recentAvailable.size != 1) "s" else ""} near you",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (uiState.recentAvailable.isNotEmpty()) {
                                    TextButton(onClick = onNavigateToBrowseFood) {
                                        Text(
                                            "View All",
                                            color = Color(0xFF2D6A4F),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.AutoMirrored.Outlined.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF2D6A4F)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (uiState.recentAvailable.isEmpty()) {
                        item {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ReserveCard(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Fastfood,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.outlineVariant
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No food available right now",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "New donations appear here automatically",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        FilledTonalButton(onClick = onNavigateToBrowseFood) {
                                            Text("Browse All Food")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(
                            items = uiState.recentAvailable,
                            key = { it.donationId }
                        ) { donation ->
                            Box(modifier = Modifier.animateItem().padding(horizontal = 16.dp, vertical = 5.dp)) {
                                FoodCard(
                                    donation = donation,
                                    onClick = { onNavigateToFoodDetail(donation.donationId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBanner(userName: String, availableCount: Int) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
            val w = size.width
            val h = size.height

            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1B4332), Color(0xFF2D6A4F), Color(0xFF40916C)),
                    start = Offset.Zero,
                    end = Offset(w, h)
                ),
                cornerRadius = CornerRadius(24.dp.toPx())
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = w * 0.35f * animProgress.value,
                center = Offset(w * 0.88f, h * 0.08f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = w * 0.22f * animProgress.value,
                center = Offset(w * 0.08f, h * 0.95f)
            )
            drawCircle(
                color = Color(0xFF52B788).copy(alpha = 0.12f),
                radius = w * 0.16f * animProgress.value,
                center = Offset(w * 0.6f, h * 0.9f)
            )

            val wavePath = Path().apply {
                moveTo(0f, h * 0.65f)
                cubicTo(w * 0.3f, h * 0.85f, w * 0.6f, h * 0.5f, w, h * 0.7f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = wavePath, color = Color.White.copy(alpha = 0.05f))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = if (userName.isNotBlank()) "Welcome back," else "Welcome!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            if (userName.isNotBlank()) {
                Text(
                    text = userName.split(" ").first(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = if (availableCount > 0)
                        "$availableCount donation${if (availableCount != 1) "s" else ""} available to rescue"
                    else
                        "Find food. Serve communities.",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun GradientStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Box(
        modifier = modifier
            .scale(scaleAnim.value)
            .height(100.dp)
            .clip(RoundedCornerShape(18.dp))
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                brush = Brush.linearGradient(gradientColors),
                cornerRadius = CornerRadius(18.dp.toPx())
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.85f, size.height * 0.15f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.3f,
                center = Offset(size.width * 0.1f, size.height * 0.95f)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                brush = Brush.linearGradient(gradientColors),
                cornerRadius = CornerRadius(18.dp.toPx())
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.4f,
                center = Offset(size.width * 0.9f, size.height * 0.1f)
            )
        }

        androidx.compose.material3.Card(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun FoodCard(donation: Donation, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = Color.White,
                cornerRadius = CornerRadius(16.dp.toPx())
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF52B788).copy(alpha = 0.08f), Color.Transparent),
                    startX = 0f,
                    endX = size.width * 0.35f
                ),
                cornerRadius = CornerRadius(16.dp.toPx())
            )
        }

        androidx.compose.material3.Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF52B788).copy(alpha = 0.15f), Color(0xFF2D6A4F).copy(alpha = 0.1f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Fastfood,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color(0xFF2D6A4F)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = donation.foodName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF1B4332)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${donation.foodType.replace("_", " ")} · Qty: ${donation.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (donation.pickupLocation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = donation.pickupLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTimestamp(donation.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = donation.status)
            }
        }
    }
}
