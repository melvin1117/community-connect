package com.su.communityconnect.ui.screens.category

import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Category
import com.su.communityconnect.model.service.CategoryService
import com.su.communityconnect.ui.screens.CommunityConnectAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryService: CategoryService
) : CommunityConnectAppViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories.asStateFlow()

    private val _uiEvent = MutableStateFlow<CategoryUiEvent?>(null)
    val uiEvent: StateFlow<CategoryUiEvent?> = _uiEvent.asStateFlow()

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

    fun toggleCategorySelection(category: String) {
        if (_selectedCategories.value.size < 4 || _selectedCategories.value.contains(category)) {
            val updatedSelection = if (_selectedCategories.value.contains(category)) {
                _selectedCategories.value - category
            } else {
                _selectedCategories.value + category
            }
            _selectedCategories.value = updatedSelection
        } else {
            _uiEvent.value = CategoryUiEvent.MaxLimitReached
        }
    }

    fun clearSelectedCategories() {
        _selectedCategories.value = emptySet()
    }

    fun onUiEventConsumed() {
        _uiEvent.value = null
    }

    sealed class CategoryUiEvent {
        object MaxLimitReached : CategoryUiEvent()
    }
}
