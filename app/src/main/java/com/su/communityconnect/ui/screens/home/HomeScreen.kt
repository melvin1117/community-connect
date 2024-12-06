package com.su.communityconnect.ui.screens.home

import SideNavigationDrawer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.CREATE_EVENT_SCREEN
import com.su.communityconnect.FAVOURITE_SCREEN
import com.su.communityconnect.HOME_SCREEN
import com.su.communityconnect.R
import com.su.communityconnect.SEARCH_EVENT_SCREEN
import com.su.communityconnect.ui.components.BottomNavBar
import com.su.communityconnect.ui.components.EventCard
import com.su.communityconnect.ui.components.MiniCardComponent
import com.su.communityconnect.ui.components.PrimaryButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    selectedCategories: List<String>,
    onLogout: () -> Unit
) {
    val isFavorite = remember { mutableStateOf(false) }
    var favoriteEvent by remember { mutableStateOf("") }
    var isDrawerOpen = remember { MutableStateFlow(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Top Row with Profile Image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user), // Replace with actual image
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                coroutineScope.launch {
                                    isDrawerOpen.value = true // Open the drawer
                                }
                            }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome to the Home Screen!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.background,
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Categories:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.background,
                )
                Spacer(modifier = Modifier.height(4.dp))
                selectedCategories.forEach { category ->
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                PrimaryButton(
                    text = stringResource(id = R.string.logout),
                    onClick = { viewModel.logout(onLogout) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                MiniCardComponent(
                    imageRes = R.drawable.default_event, // Replace with your drawable resource
                    dateText = "Nov 01",
                    timeText = "07:30 PM",
                    title = "Maggie Rogers Music Jam",
                    location = "Clinton Square, Syracuse",
                    isFavorite = isFavorite.value,
                    onFavoriteClick = { isFavorite.value = !isFavorite.value }
                )

                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(sampleEvents.size) { index ->
                        val event = sampleEvents[index]
                        EventCard(
                            imageRes = event.imageRes,
                            badgeText = event.badgeText,
                            title = event.title,
                            date = event.date,
                            time = event.time,
                            location = event.location,
                            status = event.status,
                            isFavorite = favoriteEvent == event.title,
                            onFavoriteClick = {
                                favoriteEvent = if (favoriteEvent == event.title) "" else event.title
                            }
                        )
                    }
                }
            }
        }
    )

    // Custom Drawer Content
    if (isDrawerOpen.collectAsState().value) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f) // Limit drawer height to half of the screen
                .background(MaterialTheme.colorScheme.surface)
        ) {
            SideNavigationDrawer() // Use your existing navigation drawer component here
        }

        // Close Drawer Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    coroutineScope.launch {
                        isDrawerOpen.value = false // Close the drawer
                    }
                } // Close drawer when clicking outside
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        )
    }
}


data class Event(
    val imageRes: Int,
    val badgeText: String,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val status: String
)

val sampleEvents = listOf(
    Event(
        imageRes = R.drawable.default_event,
        badgeText = "Top",
        title = "Astronomy on Tap | Nov 17, 2024",
        date = "Nov 17, 2024",
        time = "06:30 - 08:30 PM",
        location = "Recess, Syracuse",
        status = "Free"
    ),
    Event(
        imageRes = R.drawable.default_event,
        badgeText = "Featured",
        title = "Music Night | Nov 25, 2024",
        date = "Nov 25, 2024",
        time = "07:00 - 10:00 PM",
        location = "Downtown Arena",
        status = "Paid"
    )
)