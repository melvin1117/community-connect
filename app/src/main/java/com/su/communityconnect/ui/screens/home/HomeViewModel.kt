package com.su.communityconnect.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.model.state.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Typography.dagger

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventService: EventService,
    private val userService: UserService
) : ViewModel() {

    private val _trendingEvents = MutableStateFlow<List<Event>>(emptyList())
    val trendingEvents: StateFlow<List<Event>> = _trendingEvents

    private val _upcomingEvents = MutableStateFlow<List<Event>>(emptyList())
    val upcomingEvents: StateFlow<List<Event>> = _upcomingEvents

    private val _favoriteEvents = MutableStateFlow<Set<String>>(emptySet())
    val favoriteEvents: StateFlow<Set<String>> = _favoriteEvents

    private val _locationName = MutableStateFlow("")
    val locationName: StateFlow<String> = _locationName

    init {
        fetchFavoriteEvents()
    }

    fun fetchTrendingEvents(location: String) {
        viewModelScope.launch {
            try {
                val trending = eventService.getTrendingEvents(location)
                _trendingEvents.value = trending.take(3)
                _locationName.value = location // Persist location for UI
                fetchUpcomingEvents(location) // Fetch upcoming events when location changes
            } catch (e: Exception) {
                // Handle error, if needed
            }
        }
    }

    private fun fetchUpcomingEvents(location: String) {
        viewModelScope.launch {
            try {
                val userCategories = UserState.userState.value?.preferredCategories.orEmpty()
                val upcoming = eventService.getUpcomingEvents(location, userCategories)
                _upcomingEvents.value = upcoming
            } catch (e: Exception) {
                // Handle error, if needed
            }
        }
    }

    fun fetchFavoriteEvents() {
        viewModelScope.launch {
            try {
                val user = userService.getUser(UserState.userState.value?.id ?: return@launch) ?: return@launch
                _favoriteEvents.value = user.wishlist.toSet()
            } catch (e: Exception) {
                // Handle error, if needed
            }
        }
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            try {
                val user = userService.getUser(UserState.userState.value?.id ?: return@launch) ?: return@launch
                val updatedWishlist = user.wishlist.toMutableSet()
                if (updatedWishlist.contains(eventId)) {
                    updatedWishlist.remove(eventId)
                } else {
                    updatedWishlist.add(eventId)
                }
                userService.updateWishlist(user.id, updatedWishlist.toList())
                _favoriteEvents.value = updatedWishlist
            } catch (e: Exception) {
                // Handle error, if needed
            }
        }
    }
}
