package com.su.communityconnect.ui.screens.category

import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.Category
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.CategoryService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.ui.screens.CommunityConnectAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val userService: UserService,
    private val accountService: AccountService
) : CommunityConnectAppViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategories: StateFlow<List<String>> = _selectedCategories.asStateFlow()

    private val _uiEvent = MutableStateFlow<CategoryUiEvent?>(null)
    val uiEvent: StateFlow<CategoryUiEvent?> = _uiEvent.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        fetchCategories()
        loadPreferredCategories()
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

    private fun loadPreferredCategories() {
        viewModelScope.launch {
            try {
                val user = userService.getUser(accountService.currentUserId)
                _selectedCategories.value = user?.preferredCategories ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace() // Log error for debugging
            }
        }
    }

    fun toggleCategorySelection(categoryId: String) {
        val updatedSelection = if (_selectedCategories.value.contains(categoryId)) {
            _selectedCategories.value - categoryId
        } else {
            if (_selectedCategories.value.size < 4) {
                _selectedCategories.value + categoryId
            } else {
                _uiEvent.value = CategoryUiEvent.MaxLimitReached
                _selectedCategories.value
            }
        }
        _selectedCategories.value = updatedSelection.distinct() // Ensure no duplicates
    }

    fun savePreferredCategories(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // Validation: At least one category must be selected
                if (_selectedCategories.value.isEmpty()) {
                    _uiEvent.value = CategoryUiEvent.MinLimitNotReached
                    return@launch
                }

                // Update preferred categories in the database
                userService.updatePreferredCategories(
                    userId = accountService.currentUserId,
                    categories = _selectedCategories.value
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to save preferred categories")
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearSelectedCategories() {
        _selectedCategories.value = emptyList()
    }

    fun onUiEventConsumed() {
        _uiEvent.value = null
    }

    sealed class CategoryUiEvent {
        object MaxLimitReached : CategoryUiEvent()
        object MinLimitNotReached : CategoryUiEvent() // Add this branch
    }
}

