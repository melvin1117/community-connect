package com.su.communityconnect.ui.screens.authentication.forgotpassword

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.PrimaryButton
import com.su.communityconnect.ui.components.TextField
import com.su.communityconnect.ui.screens.authentication.isValidEmail

@Composable
fun ForgotPasswordScreen(onPasswordResetSuccess: () -> Unit, onBackSignIn: () -> Unit, viewModel: ForgotPasswordViewModel = hiltViewModel()) {
    val email = viewModel.email.collectAsState()
    var emailError by remember { mutableStateOf<String?>(null) }
    val uiEvent = viewModel.uiEvent.collectAsState()
    val context = LocalContext.current

    // Handle UI events
    LaunchedEffect(uiEvent.value) {
        uiEvent.value?.let { errorType ->
            val message = when (errorType) {
                ForgotPasswordType.INVALID_EMAIL -> context.getString(R.string.email_valid_error)
                ForgotPasswordType.OPERATION_FAILED -> context.getString(R.string.forgot_password_failed)
                ForgotPasswordType.SUCCESS -> context.getString(R.string.forgot_password_success)
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
                    text = stringResource(id = R.string.reset_password),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.reset_password_instructions),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextField(
                    value = email.value,
                    onSurface = false,
                    onValueChange = {
                        viewModel.updateEmail(it)
                        emailError = if (!it.isValidEmail()) {
                            context.getString(R.string.email_valid_error)
                        } else {
                            null
                        }
                    },
                    keyboardType = KeyboardType.Email,
                    label = stringResource(id = R.string.email_address),
                    placeholder = stringResource(id = R.string.email_placeholder),
                    errorMessage = emailError
                )
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryButton(
                    text = stringResource(id = R.string.send_reset_link),
                    onClick = {
                        if (emailError == null) {
                            viewModel.onResetPasswordClick(onPasswordResetSuccess)
                        }
                    },
                    horizontalPadding = 50.dp,
                    enabled = emailError == null
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = onBackSignIn) {
                        Text(
                            text = stringResource(id = R.string.back_sign_in),
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}
