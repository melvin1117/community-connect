package com.su.communityconnect.model.state

import androidx.compose.runtime.mutableStateOf
import com.su.communityconnect.model.EventLocation

object EventLocationState {
    private val _eventLocation = mutableStateOf<EventLocation?>(null)
    val eventLocation: EventLocation? get() = _eventLocation.value

    fun setEventLocation(location: EventLocation) {
        _eventLocation.value = location
    }

    fun clearEventLocation() {
        _eventLocation.value = null
    }
}
