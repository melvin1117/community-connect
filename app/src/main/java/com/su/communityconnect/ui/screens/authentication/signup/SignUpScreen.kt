package com.su.communityconnect.ui.screens.authentication.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.PasswordField
import com.su.communityconnect.ui.components.PrimaryButton
import com.su.communityconnect.ui.components.TermsAndConditionsBottomSheet
import com.su.communityconnect.ui.components.TextField
import com.su.communityconnect.ui.screens.authentication.AuthenticationButton
import com.su.communityconnect.ui.screens.authentication.isValidEmail
import com.su.communityconnect.ui.screens.authentication.isValidPassword
import com.su.communityconnect.ui.screens.authentication.launchCredManBottomSheet

@Composable
fun SignUpScreen(onNavigateToSignIn: () -> Unit, viewModel: SignUpViewModel = hiltViewModel()) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var isTermsAccepted by remember { mutableStateOf(false) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val uiEvent = viewModel.uiEvent.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        launchCredManBottomSheet(context) { result ->
            viewModel.onSignUpWithGoogle(result, onNavigateToSignIn)
        }
    }

    LaunchedEffect(uiEvent.value) {
        uiEvent.value?.let { event ->
            val message = when (event) {
                SignUpType.INVALID_EMAIL -> context.getString(R.string.email_valid_error)
                SignUpType.INVALID_PASSWORD -> context.getString(R.string.password_min_length_error)
                SignUpType.PASSWORDS_DO_NOT_MATCH -> context.getString(R.string.password_match_error)
                SignUpType.FAILED -> context.getString(R.string.signup_failed)
                SignUpType.EMAIL_VERIFICATION_SENT -> context.getString(R.string.email_verification_sent)
                SignUpType.SENDING_EMAIL_VERIFICATION_FAILED -> context.getString(R.string.sending_verification_email_failed)
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.onUiEventConsumed()
        }
    }

    if (isBottomSheetVisible) {
        TermsAndConditionsBottomSheet(onDismiss = { isBottomSheetVisible = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "App Logo",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.welcome),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.create_account),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.sign_up_instruction),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextField(
                    value = email.value,
                    onValueChange = {
                        viewModel.updateEmail(it)
                        emailError =
                            if (!it.isValidEmail()) {
                                context.getString(R.string.email_valid_error)
                            } else {
                                null
                            }
                    },
                    label = stringResource(id = R.string.email_address),
                    placeholder = stringResource(id = R.string.email_placeholder),
                    errorMessage = emailError
                )
                Spacer(modifier = Modifier.height(24.dp))
                PasswordField(
                    value = password.value,
                    onValueChange = {
                        viewModel.updatePassword(it)
                        passwordError = if (!it.isValidPassword()) {
                            context.getString(R.string.password_min_length_error)
                        } else {
                            null
                        }
                    },
                    label = stringResource(id = R.string.password),
                    placeholder = stringResource(id = R.string.password_placeholder),
                    errorMessage = passwordError
                )
                Spacer(modifier = Modifier.height(24.dp))
                PasswordField(
                    value = confirmPassword.value,
                    onValueChange = {
                        viewModel.updateConfirmPassword(it)
                        confirmPasswordError = if (it != password.value) {
                            context.getString(R.string.password_match_error)
                        } else {
                            null
                        }
                    },
                    label = stringResource(id = R.string.confirm_password),
                    placeholder = stringResource(id = R.string.password_placeholder),
                    errorMessage = confirmPasswordError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isTermsAccepted,
                        onCheckedChange = { isTermsAccepted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.by_registering),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        TextButton(onClick = { isBottomSheetVisible = true }) {
                            Text(
                                text = stringResource(id = R.string.terms_and_condition),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryButton(
                    text = stringResource(id = R.string.sign_up),
                    onClick = {
                        if (emailError == null && passwordError == null && confirmPasswordError == null && isTermsAccepted) {
                            viewModel.onSignUpClick(onNavigateToSignIn)
                        }
                    },
                    horizontalPadding = 50.dp,
                    enabled = emailError == null && passwordError == null && confirmPasswordError == null && isTermsAccepted
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.already_have_account),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    )
                    TextButton(onClick = onNavigateToSignIn) {
                        Text(
                            text = stringResource(id = R.string.signin_here),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onTertiary,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(id = R.string.or),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AuthenticationButton(buttonText = R.string.sign_up_with_google)  { credential ->
                    viewModel.onSignUpWithGoogle(credential, onNavigateToSignIn)
                }
            }
        }
    }
}
