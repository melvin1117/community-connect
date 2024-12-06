package com.su.communityconnect.model.service

import com.su.communityconnect.model.Event

interface EventService {
    suspend fun getEvent(eventId: String): Event?
    suspend fun getAllEvents(): List<Event>
    suspend fun saveEvent(event: Event): Event
    suspend fun deleteEvent(eventId: String)
    suspend fun uploadImages(userId: String, eventId: String, imageUris: List<String>): List<String>
    fun generateEventId(): String
    suspend fun getTrendingEvents(location: String): List<Event>
    suspend fun getUpcomingEvents(location: String, preferredUserCategories: List<String>): List<Event>
}
