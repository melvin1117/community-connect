package com.su.communityconnect.model.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.EventDTO
import com.su.communityconnect.model.TicketLifecycle
import com.su.communityconnect.model.TicketLifecycleDTO
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime

class EventRepository(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage,
) {
    private val eventsRef = database.reference.child("events")

    suspend fun getEvent(eventId: String): Event? {
        val snapshot = eventsRef.child(eventId).get().await()
        val eventDTO = snapshot.getValue(EventDTO::class.java) ?: return null
        return eventDTO.toEvent()
    }

    suspend fun getAllEvents(): List<Event> {
        val snapshot = eventsRef.get().await()
        return snapshot.children.mapNotNull { it.getValue(EventDTO::class.java)?.toEvent() }
    }

    suspend fun saveEvent(event: Event) {
        val eventDTO = event.toEventDTO()
        eventsRef.child(event.id).setValue(eventDTO).await()
    }

    suspend fun deleteEvent(eventId: String) {
        eventsRef.child(eventId).removeValue().await()
    }

    suspend fun uploadImage(userId: String, eventId: String, imageUri: String): String {
        val fileRef = storage.reference
            .child("event_images")
            .child(userId)
            .child(eventId)
            .child(System.currentTimeMillis().toString())
        val uploadTask = fileRef.putFile(android.net.Uri.parse(imageUri)).await()
        return uploadTask.storage.downloadUrl.await().toString()
    }

    private val dateFormatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

    private fun EventDTO.toEvent(): Event {
        return Event(
            id = id,
            title = title,
            description = description,
            guideline = guideline,
            category = category,
            location = location,
            eventTimestamp = java.time.LocalDateTime.parse(eventTimestamp, dateFormatter).toKotlinLocalDateTime(),
            createdBy = createdBy,
            maxTickets = maxTickets,
            price = price,
            ticketLifecycle = TicketLifecycle(
                liveFrom = java.time.LocalDateTime.parse(ticketLifecycle.liveFrom, dateFormatter).toKotlinLocalDateTime(),
                endsOn = java.time.LocalDateTime.parse(ticketLifecycle.endsOn, dateFormatter).toKotlinLocalDateTime()
            ),
            perUserTicketLimit = perUserTicketLimit,
            images = images,
            promoCode = promoCode
        )
    }

    private fun Event.toEventDTO(): EventDTO {
        return EventDTO(
            id = id,
            title = title,
            description = description,
            guideline = guideline,
            category = category,
            location = location,
            eventTimestamp = eventTimestamp.toJavaLocalDateTime().format(dateFormatter),
            createdBy = createdBy,
            maxTickets = maxTickets,
            price = price,
            ticketLifecycle = TicketLifecycleDTO(
                liveFrom = ticketLifecycle.liveFrom.toJavaLocalDateTime().format(dateFormatter),
                endsOn = ticketLifecycle.endsOn.toJavaLocalDateTime().format(dateFormatter)
            ),
            perUserTicketLimit = perUserTicketLimit,
            images = images,
            promoCode = promoCode
        )
    }
}
