package com.su.communityconnect.model.service

import com.su.communityconnect.model.Ticket

interface TicketService {
    suspend fun createTicket(ticket: Ticket): Ticket
    suspend fun getTicketsByEvent(eventId: String): List<Ticket>
    suspend fun getTicketsByUser(userId: String): List<Ticket>
    suspend fun updateTicket(ticket: Ticket): Ticket
    suspend fun validateTicket(ticketId: String, validationId: String, validatedBy: String): Ticket
}
