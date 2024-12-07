package com.su.communityconnect.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.model.provider.LocationProvider
import com.su.communityconnect.model.state.UserState
import com.su.communityconnect.ui.components.EventCard
import com.su.communityconnect.ui.components.EventMiniCard
import com.su.communityconnect.ui.components.ProfilePicture
import com.su.communityconnect.ui.components.SideNavigationDrawer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items


@Composable
fun HomeScreen(
    onRequestLocationPermission: () -> Unit,
    onLocationBoxClick: () -> Unit,
    onEventClick: (String) -> Unit,
    drawerItemClicked: (String) -> Unit,
    isPermissionGranted: Boolean,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userState = UserState.userState.collectAsState().value
    val isDrawerOpen = remember { MutableStateFlow(false) }
    val trendingEvents by viewModel.trendingEvents.collectAsState()
    val favoriteEvents by viewModel.favoriteEvents.collectAsState()
    val locationName by viewModel.locationName.collectAsState()
    val upcomingEvents by viewModel.upcomingEvents.collectAsState()

    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted && locationName.isBlank()) {
            val locationProvider = LocationProvider(context)
            val location = locationProvider.getCurrentLocation()
            location?.city?.let { viewModel.fetchTrendingEvents(it) }
                ?: onRequestLocationPermission()
        } else if (!isPermissionGranted) {
            onRequestLocationPermission()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Hello ${userState?.displayName?.split(" ")?.firstOrNull() ?: "User"}!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                // Location Box
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { onLocationBoxClick() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = locationName.ifBlank { "Fetching Location..." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                ProfilePicture(
                    imageUrl = userState?.profilePictureUrl,
                    displayName = userState?.displayName,
                    onImageSelected = {}, // Handle image selection if needed
                    size = 40,
                    profileClicked = {
                        coroutineScope.launch { isDrawerOpen.value = true }
                    }
                )
            }
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Trending Events Section
                item {
                    Text(
                        text = "Trending events near you:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                }
                item {
                    LazyRow(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(trendingEvents) { event ->
                            EventCard(
                                event = event,
                                isFavorite = favoriteEvents.contains(event.id),
                                onFavoriteClick = { eventId -> viewModel.toggleFavorite(eventId) },
                                onEventClick = { eventId -> onEventClick(eventId) },
                                modifier = Modifier.fillParentMaxWidth(0.93f)
                            )
                        }
                    }
                }

                // Categories Section
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Categories:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(userState?.preferredCategories.orEmpty()) { category ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.background,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = category.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(upcomingEvents.chunked(2)) { rowEvents ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowEvents.forEach { event ->
                            EventMiniCard(
                                event = event,
                                isFavorite = favoriteEvents.contains(event.id),
                                onFavoriteClick = { eventId -> viewModel.toggleFavorite(eventId) },
                                onEventClick = { eventId -> onEventClick(eventId) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Add spacer if there is an odd number of events
                        if (rowEvents.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    )

    // Drawer Content
    Box(modifier = Modifier.fillMaxSize()) {
        if (isDrawerOpen.collectAsState().value) {
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    SideNavigationDrawer(
                        itemClicked = { selectedPage ->
                            coroutineScope.launch {
                                isDrawerOpen.value = false // Close the drawer
                                drawerItemClicked(selectedPage) // Trigger navigation
                            }
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            coroutineScope.launch {
                                isDrawerOpen.value = false // Close the drawer
                            }
                        }
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                )
            }
        }
    }
}
