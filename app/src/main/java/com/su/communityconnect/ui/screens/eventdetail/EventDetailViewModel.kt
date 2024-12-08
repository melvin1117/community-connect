package com.su.communityconnect.ui.screens.eventdetail

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.User
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.model.state.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventService: EventService,
    private val userService: UserService
) : ViewModel() {

    private val _eventState = MutableStateFlow<EventDetailState>(EventDetailState.Loading)
    val eventState: StateFlow<EventDetailState> = _eventState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _organizerState = MutableStateFlow<OrganizerState>(OrganizerState.Loading)
    val organizerState: StateFlow<OrganizerState> = _organizerState

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            try {
                val event = eventService.getEvent(eventId)
                if (event != null) {
                    _eventState.value = EventDetailState.Success(event)
                    val currentUser = userService.getUser(UserState.userState.value?.id ?: return@launch) ?: return@launch
                    _isFavorite.value = currentUser.wishlist.contains(eventId)
                    loadOrganizerDetails(event.createdBy)
                } else {
                    _eventState.value = EventDetailState.Error
                }
            } catch (e: Exception) {
                _eventState.value = EventDetailState.Error
            }
        }
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            try {
                val currentUser = userService.getUser(UserState.userState.value?.id ?: return@launch) ?: return@launch
                val updatedWishlist = currentUser.wishlist.toMutableSet()
                if (updatedWishlist.contains(eventId)) {
                    updatedWishlist.remove(eventId)
                } else {
                    updatedWishlist.add(eventId)
                }
                userService.updateWishlist(currentUser.id, updatedWishlist.toList())
                _isFavorite.value = updatedWishlist.contains(eventId)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun shareEvent(title: String, description: String, context: android.content.Context) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this event: $title")
            putExtra(Intent.EXTRA_TEXT, "Event Details:$title\n$description")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Event via"))
    }

    private fun loadOrganizerDetails(organizerId: String) {
        viewModelScope.launch {
            _organizerState.value = OrganizerState.Loading
            try {
                val user = userService.getUser(organizerId)
                if (user != null) {
                    _organizerState.value = OrganizerState.Success(user)
                } else {
                    _organizerState.value = OrganizerState.Error("Organizer not found")
                }
            } catch (e: Exception) {
                _organizerState.value = OrganizerState.Error(e.message ?: "Failed to load organizer details")
            }
        }
    }
}

sealed class EventDetailState {
    object Loading : EventDetailState()
    object Error : EventDetailState()
    data class Success(val event: Event) : EventDetailState()
}

sealed class OrganizerState {
    object Loading : OrganizerState()
    data class Success(val user: User) : OrganizerState()
    data class Error(val message: String) : OrganizerState()
}
