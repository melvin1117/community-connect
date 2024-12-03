package com.su.communityconnect.ui.screens.categories

import com.su.communityconnect.ui.screens.CommunityConnectAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor() : CommunityConnectAppViewModel() {

    // State for selected categories
    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories.asStateFlow()

    // State for UI events (like errors or success)
    private val _uiEvent = MutableStateFlow<CategoryUiEvent?>(null)
    val uiEvent: StateFlow<CategoryUiEvent?> = _uiEvent.asStateFlow()

    // Update the selected categories (add or remove categories)
    fun toggleCategorySelection(category: String) {
        // Ensure no more than 4 categories are selected
        if (_selectedCategories.value.size < 4 || _selectedCategories.value.contains(category)) {
            val updatedSelection = if (_selectedCategories.value.contains(category)) {
                _selectedCategories.value - category // Deselect category
            } else {
                _selectedCategories.value + category // Select category
            }
            _selectedCategories.value = updatedSelection
        } else {
            // UI event for when more than 4 categories are selected
            _uiEvent.value = CategoryUiEvent.MaxLimitReached
        }
    }

    // Clear all selected categories
    fun clearSelectedCategories() {
        _selectedCategories.value = emptySet()
    }

    // Consume the current UI event (e.g., to reset error messages)
    fun onUiEventConsumed() {
        _uiEvent.value = null
    }

    // UI Events for category selection
    sealed class CategoryUiEvent {
        object MaxLimitReached : CategoryUiEvent()
    }
}
