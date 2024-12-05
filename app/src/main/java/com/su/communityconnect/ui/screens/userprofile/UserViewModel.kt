package com.su.communityconnect.ui.screens.userprofile

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.su.communityconnect.model.User
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userService: UserService,
    private val accountService: AccountService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userState = MutableStateFlow(savedStateHandle["user"] ?: User())
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        fetchUserProfile(accountService.currentUserId)
    }

    private fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val user = userService.getUser(uid)
                _userState.value = user ?: User(id = uid, email = accountService.getUserProfile().email)
                savedStateHandle["user"] = _userState.value
            } catch (e: Exception) {
                // Handle errors
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUser(updatedUser: User) {
        _userState.value = updatedUser
        savedStateHandle["user"] = updatedUser
    }

    fun saveUserProfile(
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                var updatedUser = _userState.value ?: return@launch

                // Upload profile image if a new one was selected
                if (imageUri != null) {
                    val profileImageUrl = userService.uploadProfileImage(accountService.currentUserId, imageUri)
                    updatedUser = updatedUser.copy(profilePictureUrl = profileImageUrl)
                }

                // Save updated user data to the database
                userService.saveUser(updatedUser)
                _userState.value = updatedUser
                savedStateHandle["user"] = updatedUser
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to save profile")
            } finally {
                _loading.value = false
            }
        }
    }
}


