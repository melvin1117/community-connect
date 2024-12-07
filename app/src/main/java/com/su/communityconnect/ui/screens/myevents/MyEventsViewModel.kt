package com.su.communityconnect.ui.screens.myevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.model.state.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyEventsViewModel @Inject constructor(
    private val eventService: EventService,
    private val userService: UserService
) : ViewModel() {

    private val _createdEvents = MutableStateFlow<List<Event>>(emptyList())
    val createdEvents: StateFlow<List<Event>> = _createdEvents

    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    val wishlist: StateFlow<Set<String>> = _wishlist

    init {
        fetchCreatedEvents()
        fetchWishlist()
    }

    private fun fetchCreatedEvents() {
        viewModelScope.launch {
            try {
                val userId = UserState.userState.value?.id ?: return@launch
                val events = eventService.getEventsByOwnerId(userId)
                _createdEvents.value = events
            } catch (e: Exception) {
                _createdEvents.value = emptyList() // Handle error if needed
            }
        }
    }

    private fun fetchWishlist() {
        viewModelScope.launch {
            try {
                val user = userService.getUser(UserState.userState.value?.id ?: return@launch) ?: return@launch
                _wishlist.value = user.wishlist.toSet()
            } catch (e: Exception) {
                _wishlist.value = emptySet() // Handle error if needed
            }
        }
    }

    fun toggleWishlist(eventId: String) {
        viewModelScope.launch {
            try {
                val userId = UserState.userState.value?.id ?: return@launch
                val updatedWishlist = _wishlist.value.toMutableSet()
                if (updatedWishlist.contains(eventId)) {
                    updatedWishlist.remove(eventId)
                } else {
                    updatedWishlist.add(eventId)
                }

                // Update wishlist in the database
                userService.updateWishlist(userId, updatedWishlist.toList())
                _wishlist.value = updatedWishlist
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}
