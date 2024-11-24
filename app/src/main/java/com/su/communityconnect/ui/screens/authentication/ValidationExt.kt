package com.su.communityconnect.ui.screens.authentication
import android.util.Patterns
import java.util.regex.Pattern

// Passwords must have at least eight digits and include
// one digit, one lower case letter and one upper case letter.
private const val MIN_PASS_LENGTH = 8
private const val PASS_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}\$"

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_PASS_LENGTH &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}

