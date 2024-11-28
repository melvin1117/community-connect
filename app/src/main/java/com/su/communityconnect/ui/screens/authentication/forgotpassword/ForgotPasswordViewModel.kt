package com.su.communityconnect.ui.screens.authentication.forgotpassword

import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.ui.screens.CommunityConnectAppViewModel
import com.su.communityconnect.ui.screens.authentication.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val accountService: AccountService
) : CommunityConnectAppViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()


    private val _uiEvent = MutableStateFlow<ForgotPasswordType?>(null)
    val uiEvent: StateFlow<ForgotPasswordType?> = _uiEvent.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun onResetPasswordClick(onPasswordResetSuccess: () -> Unit) {
        launchCatching {
            try {
                if (!_email.value.isValidEmail()) {
                    _uiEvent.value = ForgotPasswordType.INVALID_EMAIL
                    return@launchCatching
                }

                accountService.sendPasswordResetEmail(_email.value)
                _uiEvent.value = ForgotPasswordType.SUCCESS // Success case
                onPasswordResetSuccess()
            } catch (e: Exception) {
                _uiEvent.value = ForgotPasswordType.OPERATION_FAILED
            }
        }
    }

    fun onUiEventConsumed() {
        _uiEvent.value = null
    }
}