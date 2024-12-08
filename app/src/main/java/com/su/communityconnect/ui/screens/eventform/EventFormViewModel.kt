package com.su.communityconnect.ui.screens.eventform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Category
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.EventLocation
import com.su.communityconnect.model.PromoCode
import com.su.communityconnect.model.TicketLifecycle
import com.su.communityconnect.model.TicketLifecycleDTO
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.CategoryService
import com.su.communityconnect.model.service.EventService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

@HiltViewModel
class EventFormViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val eventService: EventService,
    private val accountService: AccountService
) : ViewModel() {

    private val _eventFormState = MutableStateFlow(EventFormState())
    val eventFormState: StateFlow<EventFormState> = _eventFormState.asStateFlow()

    private val _validationError = MutableStateFlow<EventFormValidationError?>(null)
    val validationError: StateFlow<EventFormValidationError?> = _validationError.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val fetchedCategories = categoryService.getCategories()
                _categories.value = fetchedCategories
            } catch (e: Exception) {
                e.printStackTrace() // Log error for debugging
            }
        }
    }

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            try {
                val event = eventService.getEvent(eventId)
                event?.let {
                    _eventFormState.value = EventFormState(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        category = it.category,
                        guideline = it.guideline,
                        location = it.location,
                        eventDate = it.eventTimestamp,
                        bookingStartDate = it.ticketLifecycle.liveFrom,
                        bookingEndDate = it.ticketLifecycle.endsOn,
                        totalCapacity = it.maxTickets,
                        costPerTicket = it.price,
                        ticketLimitPerPerson = it.perUserTicketLimit,
                        images = it.images.toList(),
                        promoCode = it.promoCode,
                        ticketsBooked = it.ticketsBooked,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateForm(update: EventFormState.() -> EventFormState) {
        _eventFormState.value = _eventFormState.value.update()
    }

    fun validateAndSaveEvent(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val formState = _eventFormState.value
        val validationError = validateForm(formState)

        if (validationError != null) {
            _validationError.value = validationError
            onError(validationError.name)
            return
        }

        val eventId = if (formState.id.isEmpty()) eventService.generateEventId() else formState.id
        val event = Event(
            id = eventId,
            title = formState.title,
            description = formState.description,
            guideline = formState.guideline,
            category = formState.category,
            location = formState.location,
            eventTimestamp = formState.eventDate ?: LocalDateTime.now(),
            createdBy = accountService.currentUserId,
            maxTickets = formState.totalCapacity,
            price = formState.costPerTicket,
            ticketLifecycle = TicketLifecycle(
                liveFrom = formState.bookingStartDate ?: LocalDateTime.now(),
                endsOn = formState.bookingEndDate ?: formState.eventDate ?: LocalDateTime.now()
            ),
            perUserTicketLimit = formState.ticketLimitPerPerson ?: 0,
            images = emptyList(),
            promoCode = formState.promoCode,
            ticketsBooked = formState.ticketsBooked
        )

        viewModelScope.launch {
            try {
                val uploadedImages = eventService.uploadImages(accountService.currentUserId, eventId, formState.images)
                val updatedEvent = event.copy(images = uploadedImages)
                eventService.saveEvent(updatedEvent)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.localizedMessage ?: "Error occurred while saving the event")
            }
        }
    }


    private fun validateForm(formState: EventFormState): EventFormValidationError? {
        val now = LocalDateTime.now()
        val eventDate = formState.eventDate ?: return EventFormValidationError.INVALID_EVENT_DATE
        val bookingStart = formState.bookingStartDate ?: now
        val bookingEnd = formState.bookingEndDate ?: eventDate

        if (eventDate.toJavaLocalDateTime().isBefore(now.toJavaLocalDateTime())) {
            return EventFormValidationError.INVALID_EVENT_DATE
        }
        if (bookingStart.toJavaLocalDateTime().isAfter(eventDate.toJavaLocalDateTime())) {
            return EventFormValidationError.INVALID_BOOKING_START_DATE
        }
        if (bookingEnd.toJavaLocalDateTime().isBefore(bookingStart.toJavaLocalDateTime())) {
            return EventFormValidationError.INVALID_BOOKING_END_DATE
        }
        if (formState.title.isBlank()) return EventFormValidationError.TITLE_REQUIRED
        if (formState.description.isBlank()) return EventFormValidationError.DESCRIPTION_REQUIRED
        if (formState.totalCapacity <= 0) return EventFormValidationError.INVALID_CAPACITY
        if (formState.costPerTicket < 0.0) return EventFormValidationError.INVALID_PRICE

        return null
    }
}

data class EventFormState(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val guideline: String = "",
    val location: EventLocation = EventLocation(),
    val eventDate: LocalDateTime? = null,
    val bookingStartDate: LocalDateTime? = null,
    val bookingEndDate: LocalDateTime? = null,
    val totalCapacity: Int = 0,
    val costPerTicket: Double = 0.0,
    val ticketLimitPerPerson: Int? = 2,
    val images: List<String> = emptyList(),
    val promoCode: List<PromoCode> = emptyList(),
    val ticketsBooked: Int = 0,
)
