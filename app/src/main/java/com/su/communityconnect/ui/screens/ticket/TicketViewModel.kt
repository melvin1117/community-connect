package com.su.communityconnect.ui.screens.ticket

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.Ticket
import com.su.communityconnect.model.provider.PDFProvider
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.TicketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val ticketService: TicketService,
    private val eventService: EventService
) : ViewModel() {

    private val _ticketState = MutableStateFlow<TicketState>(TicketState.Loading)
    val ticketState: StateFlow<TicketState> = _ticketState

    private val _pdfDownloadState = MutableStateFlow<String?>(null) // Null means no state change
    val pdfDownloadState: StateFlow<String?> = _pdfDownloadState

    fun loadTicket(ticketId: String) {
        viewModelScope.launch {
            try {
                _ticketState.value = TicketState.Loading
                val ticket = ticketService.getTicketById(ticketId) ?: throw Exception("Ticket not found")
                val event = eventService.getEvent(ticket.eventId) ?: throw Exception("Event not found")
                _ticketState.value = TicketState.Success(ticket, event)
            } catch (e: Exception) {
                _ticketState.value = TicketState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    fun downloadTicketAsPDF(context: Context, ticket: Ticket, event: Event, qrCodeBitmap: Bitmap?) {
        viewModelScope.launch {
            val result = PDFProvider.createTicketPdf(context, ticket, event, qrCodeBitmap)
            _pdfDownloadState.value = result.fold(
                onSuccess = { filePath -> "PDF saved at $filePath" },
                onFailure = { error -> "Failed to download ticket: ${error.localizedMessage}" }
            )
        }
    }

    fun resetPdfDownloadState() {
        _pdfDownloadState.value = null // Reset the state after showing the toast
    }
}

// State classes for TicketViewModel
sealed class TicketState {
    object Loading : TicketState()
    data class Success(val ticket: Ticket, val event: Event) : TicketState()
    data class Error(val message: String) : TicketState()
}
