package com.su.communityconnect.model.service.impl

import com.su.communityconnect.model.Ticket
import com.su.communityconnect.model.Validation
import com.su.communityconnect.model.repository.TicketRepository
import com.su.communityconnect.model.service.TicketService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class TicketServiceImpl @Inject constructor(
    private val ticketRepository: TicketRepository
) : TicketService {

    override suspend fun createTicket(ticket: Ticket): Ticket {
        if (ticket.quantity < 1) throw IllegalArgumentException("Quantity must be at least 1.")
        return ticketRepository.createTicket(ticket)
    }

    override suspend fun getTicketsByEvent(eventId: String): List<Ticket> {
        return ticketRepository.getTicketsByEvent(eventId)
    }

    override suspend fun getTicketsByUser(userId: String): List<Ticket> {
        return ticketRepository.getTicketsByUser(userId)
    }

    override suspend fun updateTicket(ticket: Ticket): Ticket {
        return ticketRepository.updateTicket(ticket)
    }

    override suspend fun getTicketById(ticketId: String): Ticket? {
        return ticketRepository.getTicketById(ticketId)
    }

    override suspend fun validateTicket(ticketId: String, validationId: String, validatedBy: String): Ticket {
        val ticket = ticketRepository.getTicketById(ticketId)
            ?: throw IllegalArgumentException("Ticket with ID $ticketId not found.")

        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val updatedValidations = ticket.validations.toMutableMap().apply {
            put(
                validationId,
                Validation(
                    validated = true,
                    validatedBy = validatedBy,
                    validationTimestamp = currentDateTime
                )
            )
        }

        val updatedTicket = ticket.copy(validations = updatedValidations)
        return ticketRepository.updateTicket(updatedTicket)
    }
}
