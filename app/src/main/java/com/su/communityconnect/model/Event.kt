package com.su.communityconnect.model

import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val guideline: String = "",
    val category: String = "",
    val location: EventLocation = EventLocation(),
    val eventTimestamp: LocalDateTime = LocalDateTime.now(),
    val createdBy: String = "",
    val maxTickets: Int = 2,
    val price: Double = 0.0,
    val ticketLifecycle: TicketLifecycle = TicketLifecycle(),
    val perUserTicketLimit: Int = 0,
    val images: List<String> = emptyList(),
    val eventCreationTimestamp: LocalDateTime = LocalDateTime.now(),
    val promoCode: List<PromoCode> = emptyList(),
    val ticketsBooked: Int = 0,
)

data class EventLocation(
    val id: String = "",
    val city: String = "",
    val shortAddress: String = "",
    val fullAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val displayName: String = "",
)

data class TicketLifecycle(
    val liveFrom: LocalDateTime = LocalDateTime.now(),
    val endsOn: LocalDateTime = LocalDateTime.now(),
)

data class PromoCode(
    val code: String = "",
    val discount: Double = 0.0,
)

data class EventDTO(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val guideline: String = "",
    val category: String = "",
    val location: EventLocation = EventLocation(),
    val eventTimestamp: String = "",
    val createdBy: String = "",
    val maxTickets: Int = 2,
    val price: Double = 0.0,
    val ticketLifecycle: TicketLifecycleDTO = TicketLifecycleDTO(),
    val perUserTicketLimit: Int = 0,
    val images: List<String> = emptyList(),
    val promoCode: List<PromoCode> = emptyList(),
    val ticketsBooked: Int = 0,
)

data class TicketLifecycleDTO(
    val liveFrom: String = "",
    val endsOn: String = ""
)
