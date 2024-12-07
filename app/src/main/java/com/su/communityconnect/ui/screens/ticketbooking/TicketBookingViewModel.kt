package com.su.communityconnect.ui.screens.ticketbooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.Ticket
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.TicketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class TicketBookingViewModel @Inject constructor(
    private val ticketService: TicketService,
    private val eventService: EventService
) : ViewModel() {

    private val _eventState = MutableStateFlow<TicketBookingState>(TicketBookingState.Loading)
    val eventState: StateFlow<TicketBookingState> = _eventState

    private val _promoCodeError = MutableStateFlow<String?>(null)
    val promoCodeError: StateFlow<String?> = _promoCodeError

    private val _createdTicketId = MutableStateFlow<String?>(null)
    val createdTicketId: StateFlow<String?> = _createdTicketId

    private val _payButtonEnabled = MutableStateFlow<Boolean>(true)
    val payButtonEnabled: StateFlow<Boolean> = _payButtonEnabled

    private val _payButtonError = MutableStateFlow<String?>(null)
    val payButtonError: StateFlow<String?> = _payButtonError

    private var appliedPromoCode: String? = null

    fun loadEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            try {
                _eventState.value = TicketBookingState.Loading
                val event = eventService.getEvent(eventId)
                if (event != null) {
                    _eventState.value = TicketBookingState.Success(event)
                    validateUserTickets(eventId, userId, event.perUserTicketLimit)
                } else {
                    _eventState.value = TicketBookingState.Error("Event not found")
                }
            } catch (e: Exception) {
                _eventState.value = TicketBookingState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    private suspend fun validateUserTickets(eventId: String, userId: String, perUserLimit: Int) {
        try {
            // Fetch all tickets for the event
            val allTicketsForEvent = ticketService.getTicketsByEvent(eventId)

            // If no tickets are found, assume user hasn't booked any tickets yet
            if (allTicketsForEvent.isEmpty()) {
                _payButtonEnabled.value = true
                _payButtonError.value = null
                return
            }

            // Filter tickets for the specific user
            val userTickets = allTicketsForEvent.filter { it.userId == userId }
            val totalTicketsByUser = userTickets.sumOf { it.quantity }

            // Validate the total tickets booked by the user
            if (totalTicketsByUser >= perUserLimit) {
                _payButtonEnabled.value = false
                _payButtonError.value = "You have reached the maximum ticket limit of $perUserLimit for this event."
            } else {
                _payButtonEnabled.value = true
                _payButtonError.value = null
            }
        } catch (e: Exception) {
            _payButtonEnabled.value = false
            _payButtonError.value = "Failed to validate tickets. Please try again later."
            println("Error in validateUserTickets: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }



    fun applyPromoCode(promoCode: String, event: Event, ticketCount: Int): Double {
        val promo = event.promoCode.find { it.code == promoCode }
        return if (promo != null) {
            appliedPromoCode = promoCode
            _promoCodeError.value = null
            event.price * ticketCount * (promo.discount / 100)
        } else {
            appliedPromoCode = null
            _promoCodeError.value = "Invalid promo code"
            0.0
        }
    }

    fun recalculateDiscount(event: Event, ticketCount: Int): Double {
        val promo = event.promoCode.find { it.code == appliedPromoCode }
        return promo?.let {
            event.price * ticketCount * (it.discount / 100)
        } ?: 0.0
    }

    fun bookTickets(
        eventId: String,
        userId: String,
        ticketCount: Int,
        promoCode: String?,
        discount: Double,
        totalPrice: Double
    ) {
        viewModelScope.launch {
            try {
                // Validate event state
                val currentState = _eventState.value
                if (currentState !is TicketBookingState.Success) return@launch

                val event = currentState.event
                if (ticketCount < 1 || ticketCount > getMaxTicketLimit(event)) {
                    throw IllegalArgumentException("Invalid ticket quantity")
                }
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

                // Check if the current time is before the event or booking ends
                if (now >= event.eventTimestamp || now >= event.ticketLifecycle.endsOn) {
                    _payButtonError.value = "Booking is not allowed anymore."
                    _payButtonEnabled.value = false
                    return@launch
                }

                if (now <= event.ticketLifecycle.liveFrom) {
                    _payButtonError.value = "Booking is not yet started."
                    _payButtonEnabled.value = false
                    return@launch
                }

                // Re-validate user tickets before finalizing booking
                val allTicketsForEvent = ticketService.getTicketsByEvent(eventId)
                val userTickets = allTicketsForEvent.filter { it.userId == userId }
                val totalTicketsByUser = userTickets.sumOf { it.quantity }

                if (totalTicketsByUser + ticketCount > event.perUserTicketLimit) {
                    _payButtonEnabled.value = false
                    _payButtonError.value = "You cannot book more tickets than the allowed limit for this event."
                    return@launch
                }

                // Create the ticket object
                val ticket = Ticket(
                    id = "",
                    eventId = eventId,
                    userId = userId,
                    quantity = ticketCount,
                    purchaseDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    promoCodeApplied = promoCode,
                    discountApplied = discount,
                    totalPrice = totalPrice
                )

                // Save ticket and update ticketsBooked
                val createdTicket = ticketService.createTicket(ticket)
                eventService.updateTicketsBookedCount(eventId, ticketCount)

                // Notify success
                _createdTicketId.value = createdTicket.id
                _payButtonError.value = null // Clear any errors
            } catch (e: Exception) {
                _payButtonError.value = e.localizedMessage ?: "An error occurred during booking."
                _payButtonEnabled.value = false
            }
        }
    }

    private fun getMaxTicketLimit(event: Event): Int {
        val remainingTickets = event.maxTickets - (event.ticketsBooked ?: 0)
        return minOf(event.perUserTicketLimit, remainingTickets)
    }
}

// State classes for the TicketViewModel
sealed class TicketBookingState {
    object Loading : TicketBookingState()
    data class Success(val event: Event) : TicketBookingState()
    data class Error(val message: String) : TicketBookingState()
}
