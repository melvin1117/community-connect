package com.su.communityconnect.ui.screens.mybookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.Ticket
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.TicketService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.model.state.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Clock
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

@HiltViewModel
class MyTicketsViewModel @Inject constructor(
    private val ticketService: TicketService,
    private val eventService: EventService
) : ViewModel() {

    private val _ticketsWithEvents = MutableStateFlow<List<Pair<Ticket, Event>>>(emptyList())
    val ticketsWithEvents: StateFlow<List<Pair<Ticket, Event>>> = _ticketsWithEvents

    fun fetchUserTickets() {
        viewModelScope.launch {
            try {
                val userId = UserState.userState.value?.id ?: return@launch
                val tickets = ticketService.getTicketsByUser(userId)
                val events = tickets.mapNotNull { ticket ->
                    val event = eventService.getEvent(ticket.eventId)
                    if (event != null) Pair(ticket, event) else null
                }

                // Sort by upcoming and past events
                val now = LocalDateTime.now()
                _ticketsWithEvents.value = events.sortedBy { it.second.eventTimestamp >= now }
            } catch (e: Exception) {
                _ticketsWithEvents.value = emptyList() // Handle error if needed
            }
        }
    }
}

