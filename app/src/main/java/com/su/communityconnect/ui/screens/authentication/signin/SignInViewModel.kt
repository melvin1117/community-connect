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

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiEvent = MutableStateFlow<SignInType?>(null)
    val uiEvent: StateFlow<SignInType?> = _uiEvent.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun onSignInClick(onSignInSuccess: () -> Unit) {
        launchCatching {
            if (!_email.value.isValidEmail()) {
                _uiEvent.value = SignInType.INVALID_EMAIL
                return@launchCatching
            }

            if (_password.value.isEmpty()) {
                _uiEvent.value = SignInType.EMPTY_PASSWORD
                return@launchCatching
            }

            try {
                accountService.signInWithEmail(_email.value, _password.value)

                if (accountService.isEmailVerified()) {
                    _uiEvent.value = SignInType.SUCCESS
                    onSignInSuccess()
                } else {
                    accountService.signOut()
                    _uiEvent.value = SignInType.EMAIL_NOT_VERIFIED
                }
            } catch (e: Exception) {
                _uiEvent.value = SignInType.AUTHENTICATION_FAILED
            }
        }
    }

    fun onSignInWithGoogle(credential: Credential, onSignInSuccess: () -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                _uiEvent.value = SignInType.SUCCESS
                onSignInSuccess()
            } else {
                _uiEvent.value = SignInType.AUTHENTICATION_FAILED
                Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
            }
        }
    }

    fun onUiEventConsumed() {
        _uiEvent.value = null
    }
}