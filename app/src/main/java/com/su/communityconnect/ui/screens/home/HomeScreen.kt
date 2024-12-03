package com.su.communityconnect.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.EventCard
import com.su.communityconnect.ui.components.PrimaryButton

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    selectedCategories: List<String>,
    onLogout: () -> Unit
) {
    var favoriteEvent by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        selectedCategories.forEach { category ->
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
        Text(text = "Welcome to the Home Screen!")
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = stringResource(id = R.string.logout),
            onClick = { viewModel.logout(onLogout) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Selected Categories:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
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
