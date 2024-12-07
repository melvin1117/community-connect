package com.su.communityconnect.ui.screens.wishlist

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
class WishlistViewModel @Inject constructor(
    private val eventService: EventService,
    private val userService: UserService
) : ViewModel() {

    private val _wishlistedEvents = MutableStateFlow<List<Event>>(emptyList())
    val wishlistedEvents: StateFlow<List<Event>> = _wishlistedEvents

    init {
        fetchWishlistedEvents()
    }

    private fun fetchWishlistedEvents() {
        viewModelScope.launch {
            try {
                val wishlistIds = UserState.userState.value?.wishlist.orEmpty()
                if (wishlistIds.isNotEmpty()) {
                    val events = mutableListOf<Event>()
                    for (id in wishlistIds) {
                        val event = eventService.getEvent(id)
                        if (event != null) {
                            events.add(event)
                        }
                    }
                    _wishlistedEvents.value = events
                } else {
                    _wishlistedEvents.value = emptyList()
                }
            } catch (e: Exception) {
                _wishlistedEvents.value = emptyList() // Handle error
            }
        }
    }

    fun toggleWishlist(eventId: String) {
        viewModelScope.launch {
            try {
                val user = UserState.userState.value ?: return@launch
                val updatedWishlist = user.wishlist.toMutableList()
                if (updatedWishlist.contains(eventId)) {
                    updatedWishlist.remove(eventId) // Remove from wishlist
                } else {
                    updatedWishlist.add(eventId) // Add to wishlist
                }

                // Update in Firebase and UserState
                userService.updateWishlist(user.id, updatedWishlist)
                UserState.updateUser(user.copy(wishlist = updatedWishlist))

                // Refresh the wishlist view
                fetchWishlistedEvents()
            } catch (e: Exception) {
                // Handle error, if needed
            }
        }
    }
}
