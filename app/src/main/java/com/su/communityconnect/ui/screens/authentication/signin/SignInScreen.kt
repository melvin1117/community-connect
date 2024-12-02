package com.su.communityconnect.ui.screens.authentication.signin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.unit.dp
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.PasswordField
import com.su.communityconnect.ui.components.PrimaryButton
import com.su.communityconnect.ui.components.TextField
import com.su.communityconnect.ui.screens.authentication.AuthenticationButton
import com.su.communityconnect.ui.screens.authentication.isValidEmail
import com.su.communityconnect.ui.screens.authentication.launchCredManBottomSheet

@Composable
fun SignInScreen(
    onNavigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    var emailError by remember { mutableStateOf<String?>(null) }
    val uiEvent = viewModel.uiEvent.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launchCredManBottomSheet(context) { result ->
            viewModel.onSignInWithGoogle(result, onSignInSuccess)
        }
    }

    // Handle UI events
    LaunchedEffect(uiEvent.value) {
        uiEvent.value?.let { errorType ->
            val message = when (errorType) {
                SignInType.INVALID_EMAIL -> context.getString(R.string.email_valid_error)
                SignInType.EMPTY_PASSWORD -> context.getString(R.string.password_required_error)
                SignInType.AUTHENTICATION_FAILED -> context.getString(R.string.authentication_failed)
                SignInType.EMAIL_NOT_VERIFIED -> context.getString(R.string.email_not_verified)
                SignInType.SUCCESS -> context.getString(R.string.signin_successful)
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.onUiEventConsumed()
        }
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
                    text = stringResource(id = R.string.sign_in_to_account),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.sign_in_instruction),
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
                        emailError = if (!it.isValidEmail()) {
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
                    },
                    label = stringResource(id = R.string.password),
                    placeholder = stringResource(id = R.string.password_placeholder)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onForgotPassword) {
                        Text(
                            text = stringResource(id = R.string.forgot_password),
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryButton(
                    text = stringResource(id = R.string.sign_in),
                    onClick = {
                        if (emailError == null) {
                            viewModel.onSignInClick(onSignInSuccess)
                        }
                    },
                    horizontalPadding = 50.dp,
                    enabled = emailError == null
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.dont_have_account),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    )
                    TextButton(onClick = onNavigateToSignUp) {
                        Text(
                            text = stringResource(id = R.string.register_here),
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
                AuthenticationButton(buttonText = R.string.sign_in_with_google)  { credential ->
                    viewModel.onSignInWithGoogle(credential, onSignInSuccess)
                }
            }
        }
    }
}
