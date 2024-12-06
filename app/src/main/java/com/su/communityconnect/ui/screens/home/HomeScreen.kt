package com.su.communityconnect.ui.screens.home

import SideNavigationDrawer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.su.communityconnect.model.provider.LocationProvider
import com.su.communityconnect.model.state.UserState
import com.su.communityconnect.ui.components.ProfilePicture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onRequestLocationPermission: () -> Unit,
    onLocationBoxClick: () -> Unit,
    drawerItemClicked: (String) -> Unit,
    isPermissionGranted: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userState = UserState.userState.collectAsState().value
    val isDrawerOpen = remember { MutableStateFlow(false) }

    // Location Name dynamically based on UserState preferredCity
    val locationName = userState?.preferredCity ?: "Fetching Location..."

    // React to permission changes
    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted && userState?.preferredCity.isNullOrBlank()) {
            val locationProvider = LocationProvider(context)
            val location = locationProvider.getCurrentLocation()
            UserState.updateUser(userState!!.copy(preferredCity = location?.city ?: "Location not found"))
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
                    fontWeight = FontWeight.SemiBold,
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
                            text = locationName,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Welcome to the Home Screen!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
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
