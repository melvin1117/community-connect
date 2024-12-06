package com.su.communityconnect.model.state

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.su.communityconnect.model.User
import com.su.communityconnect.model.service.UserService

object UserState {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    private var userService: UserService? = null
    private var userId: String? = null

    fun initialize(userService: UserService, userId: String) {
        this.userService = userService
        this.userId = userId
        loadUserData()
    }

    private fun loadUserData() {
        if (userService == null || userId == null) return
        CoroutineScope(Dispatchers.IO).launch {
            val user = userService?.getUser(userId!!)
            withContext(Dispatchers.Main) {
                _userState.value = user
            }
        }
    }

    fun updateUser(updatedUser: User) {
        _userState.value = updatedUser
        saveUserToDatabase(updatedUser)
    }

    private fun saveUserToDatabase(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userService?.saveUser(user)
        }
    }
}
