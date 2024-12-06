package com.su.communityconnect.model.service.impl

import com.su.communityconnect.model.Event
import com.su.communityconnect.model.repository.EventRepository
import com.su.communityconnect.model.service.EventService
import javax.inject.Inject

class EventServiceImpl @Inject constructor(
    private val eventRepository: EventRepository
) : EventService {

    override suspend fun getEvent(eventId: String): Event? {
        return eventRepository.getEvent(eventId)
    }

    override suspend fun getAllEvents(): List<Event> {
        return eventRepository.getAllEvents()
    }

    override suspend fun saveEvent(event: Event): Event {
        eventRepository.saveEvent(event)
        return event
    }

    override suspend fun deleteEvent(eventId: String) {
        eventRepository.deleteEvent(eventId)
    }

    override suspend fun uploadImages(userId: String, eventId: String, imageUris: List<String>): List<String> {
        val uploadedUrls = mutableListOf<String>()
        for (imageUri in imageUris) {
            val uploadedUrl = eventRepository.uploadImage(userId, eventId, imageUri)
            uploadedUrls.add(uploadedUrl)
        }
        return uploadedUrls
    }

    override fun generateEventId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    override suspend fun getTrendingEvents(location: String): List<Event> {
        return eventRepository.getTrendingEvents(location)
    }

    override suspend fun getUpcomingEvents(location: String, preferredUserCategories: List<String>): List<Event> {
        return eventRepository.getUpcomingEvents(location, preferredUserCategories)
    }
}
