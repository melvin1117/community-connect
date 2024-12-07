package com.su.communityconnect.model.repository

import com.google.firebase.database.FirebaseDatabase
import com.su.communityconnect.model.Ticket
import com.su.communityconnect.model.TicketDTO
import com.su.communityconnect.model.toTicket
import com.su.communityconnect.model.toTicketDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TicketRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val ticketsRef = database.reference.child("tickets")

    suspend fun createTicket(ticket: Ticket): Ticket {
        val ticketId = ticketsRef.push().key ?: throw Exception("Failed to generate ticket ID")
        val ticketWithId = ticket.copy(id = ticketId)
        ticketsRef.child(ticketId).setValue(ticketWithId.toTicketDTO()).await()
        return ticketWithId
    }

    suspend fun getTicketsByEvent(eventId: String): List<Ticket> {
        val snapshot = ticketsRef.orderByChild("eventId").equalTo(eventId).get().await()
        return snapshot.children.mapNotNull { it.getValue(TicketDTO::class.java)?.toTicket() }
    }

    suspend fun getTicketsByUser(userId: String): List<Ticket> {
        val snapshot = ticketsRef.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(TicketDTO::class.java)?.toTicket() }
    }

    suspend fun getTicketById(ticketId: String): Ticket? {
        val snapshot = ticketsRef.child(ticketId).get().await()
        return snapshot.getValue(TicketDTO::class.java)?.toTicket()
    }

    suspend fun updateTicket(ticket: Ticket): Ticket {
        ticketsRef.child(ticket.id).setValue(ticket.toTicketDTO()).await()
        return ticket
    }
}
