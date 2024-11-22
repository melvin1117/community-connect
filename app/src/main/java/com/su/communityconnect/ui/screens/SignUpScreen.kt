package com.su.communityconnect.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.IconPosition
import com.su.communityconnect.ui.components.PasswordField
import com.su.communityconnect.ui.components.PrimaryButton
import com.su.communityconnect.ui.components.TermsAndConditionsBottomSheet
import com.su.communityconnect.ui.components.TextField

@Composable
fun SignUpScreen(onNavigateToSignIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var isTermsAccepted by remember { mutableStateOf(false) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }

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
                    value = email,
                    onValueChange = {
                        email = it
                        emailError =
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                                R.string.email_valid_error.toString()
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
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = if (it.length < 8) {
                            R.string.password_min_length_error.toString()
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
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = if (it != password) {
                            R.string.password_match_error.toString()
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
                            onNavigateToSignIn()
                        }
                    },
                    horizontalPadding = 50.dp,
                    enabled = isTermsAccepted
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
                PrimaryButton(
                    text = stringResource(id = R.string.sign_up_with_google),
                    onClick = { /* Handle Google Sign-In */ },
                    icon = painterResource(id = R.drawable.googlelogo),
                    iconPosition = IconPosition.Left,
                    horizontalPadding = 20.dp
                )
            }
        }
    }
}
