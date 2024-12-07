package com.su.communityconnect.ui.screens.mybookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.ui.components.EventMiniCard
import com.su.communityconnect.ui.components.BackButton
import com.su.communityconnect.ui.components.TicketMiniCard
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

@Composable
fun MyTicketsScreen(
    onBackClick: () -> Unit,
    onTicketClick: (String) -> Unit,
    viewModel: MyTicketsViewModel = hiltViewModel()
) {
    val ticketsWithEvents by viewModel.ticketsWithEvents.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserTickets()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "My Tickets",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Box(modifier = Modifier.align(Alignment.TopStart)) {
                    BackButton(onBackClick = onBackClick)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (ticketsWithEvents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tickets booked yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ticketsWithEvents) { (ticket, event) ->
                        val isUpcoming = event.eventTimestamp >= LocalDateTime.now()
                        TicketMiniCard(
                            event = event,
                            ticket = ticket,
                            isUpcoming = isUpcoming,
                            onTicketClick = onTicketClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
