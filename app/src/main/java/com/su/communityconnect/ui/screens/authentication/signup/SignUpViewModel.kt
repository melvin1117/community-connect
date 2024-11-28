package com.su.communityconnect.ui.screens.authentication.signup

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.su.communityconnect.ERROR_TAG
import com.su.communityconnect.UNEXPECTED_CREDENTIAL
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.ui.screens.authentication.isValidEmail
import com.su.communityconnect.ui.screens.authentication.isValidPassword
import com.su.communityconnect.ui.screens.CommunityConnectAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : CommunityConnectAppViewModel() {
    // Backing properties to avoid state updates from other classes
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _uiEvent = MutableStateFlow<SignUpType?>(null)
    val uiEvent: StateFlow<SignUpType?> = _uiEvent.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun onSignUpClick(onNavigateToSignIn: () -> Unit) {
        launchCatching {
            if (!_email.value.isValidEmail()) {
                _uiEvent.value = SignUpType.INVALID_EMAIL
                return@launchCatching
            }
            if (!_password.value.isValidPassword()) {
                _uiEvent.value = SignUpType.INVALID_PASSWORD
                return@launchCatching
            }
            if (_password.value != _confirmPassword.value) {
                _uiEvent.value = SignUpType.PASSWORDS_DO_NOT_MATCH
                return@launchCatching
            }

            try {
                accountService.signUp(_email.value, _password.value)
                try {
                    accountService.sendEmailVerification()
                    _uiEvent.value = SignUpType.EMAIL_VERIFICATION_SENT
                    onNavigateToSignIn()
                } catch (e: Exception) {
                    _uiEvent.value = SignUpType.SENDING_EMAIL_VERIFICATION_FAILED
                }
            } catch (e: Exception) {
                _uiEvent.value = SignUpType.FAILED
            }
        }
    }


    fun onSignUpWithGoogle(credential: Credential, onNavigateToSignIn: () -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                _uiEvent.value = SignUpType.EMAIL_VERIFICATION_SENT
                onNavigateToSignIn()
            } else {
                _uiEvent.value = SignUpType.FAILED
                Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
            }
        }
    }

    fun onUiEventConsumed() {
        _uiEvent.value = null // Reset the state after handling
    }
}