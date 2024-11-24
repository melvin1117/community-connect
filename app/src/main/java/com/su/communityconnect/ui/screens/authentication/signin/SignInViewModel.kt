package com.su.communityconnect.ui.screens.authentication.signin

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.su.communityconnect.ERROR_TAG
import com.su.communityconnect.UNEXPECTED_CREDENTIAL
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.ui.screens.CommunityConnectAppViewModel
import com.su.communityconnect.ui.screens.authentication.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : CommunityConnectAppViewModel() {
    // Backing properties to avoid state updates from other classes
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiEvent = MutableStateFlow<SignInErrorType?>(null)
    val uiEvent: StateFlow<SignInErrorType?> = _uiEvent.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun onSignInClick(onSignInSuccess: () -> Unit) {
        launchCatching {
            try {
                if (!_email.value.isValidEmail()) {
                    _uiEvent.value = SignInErrorType.INVALID_EMAIL
                    return@launchCatching
                }
                if (_password.value.isEmpty()) {
                    _uiEvent.value = SignInErrorType.EMPTY_PASSWORD
                    return@launchCatching
                }

                accountService.signInWithEmail(_email.value, _password.value)
                _uiEvent.value = SignInErrorType.SUCCESS // Success case
                onSignInSuccess()
            } catch (e: Exception) {
                _uiEvent.value = SignInErrorType.AUTHENTICATION_FAILED
            }
        }
    }

    fun onSignInWithGoogle(credential: Credential, onSignInSuccess: () -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                _uiEvent.value = SignInErrorType.SUCCESS
                onSignInSuccess()
            } else {
                _uiEvent.value = SignInErrorType.AUTHENTICATION_FAILED
                Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
            }
        }
    }

    fun onUiEventConsumed() {
        _uiEvent.value = null
    }
}