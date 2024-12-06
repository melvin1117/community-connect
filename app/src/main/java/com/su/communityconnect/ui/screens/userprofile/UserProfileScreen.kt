package com.su.communityconnect.ui.screens.userprofile

import android.net.Uri
import androidx.compose.ui.text.input.KeyboardType
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.ui.components.*
import com.su.communityconnect.R
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.format.DateTimeParseException

@Composable
fun UserProfileScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val context = LocalContext.current
    var showValidationError by remember { mutableStateOf(false) }

    var profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var dobErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var mobileNumberError by rememberSaveable { mutableStateOf<String?>(null) }
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            userState?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BackButton(onBackClick = onBackClick)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfilePicture(
                            imageUrl = profileImageUri?.toString() ?: user.profilePictureUrl,
                            displayName = user.displayName,
                            onImageSelected = { uri -> profileImageUri = uri },
                            size = 120
                        )
                    }

                    TextField(
                        value = user.displayName,
                        onValueChange = {
                            if (it.any { char -> char.isDigit() }) {
                                nameError = context.getString(R.string.error_name_contains_numbers)
                            } else {
                                nameError = null
                                viewModel.updateUser(user.copy(displayName = it))
                            }
                        },
                        label = stringResource(R.string.label_full_name),
                        placeholder = stringResource(R.string.placeholder_full_name),
                        errorMessage = nameError
                    )

                    TextField(
                        value = user.email,
                        onValueChange = {},
                        label = stringResource(R.string.label_email),
                        placeholder = stringResource(R.string.placeholder_email),
                        readOnly = true
                    )

                    DatePickerField(
                        label = stringResource(R.string.label_date_of_birth),
                        placeholder = stringResource(R.string.placeholder_date_of_birth),
                        startDate = if (!user.dateOfBirth.isNullOrBlank()) {
                            try {
                                LocalDate.parse(user.dateOfBirth)
                            } catch (e: DateTimeParseException) {
                                LocalDate.now() // Default to current date if parsing fails
                            }
                        } else {
                            LocalDate.now() // Default to current date if dateOfBirth is null or blank
                        },
                        prevSelectedDate = user.dateOfBirth?.takeIf { it.isNotBlank() }?.let {
                            try {
                                LocalDate.parse(it)
                            } catch (e: DateTimeParseException) {
                                null // Return null if parsing fails
                            }
                        },
                        onDateSelected = { date ->
                            if (date >= LocalDate.now()) {
                                dobErrorMessage = context.getString(R.string.error_dob_future)
                            } else {
                                dobErrorMessage = null
                                viewModel.updateUser(user.copy(dateOfBirth = date.toString()))
                            }
                        }
                    )
                    if (dobErrorMessage != null) {
                        Text(
                            text = dobErrorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    TextField(
                        value = user.phone,
                        onValueChange = { input ->
                            if (input.isEmpty()) {
                                mobileNumberError = null
                                viewModel.updateUser(user.copy(phone = ""))
                            } else {
                                val regex = "^[+]?[0-9]{0,15}\$".toRegex()
                                if (!regex.matches(input)) {
                                    mobileNumberError = context.getString(R.string.error_invalid_phone)
                                } else {
                                    mobileNumberError = null
                                    viewModel.updateUser(user.copy(phone = input))
                                }
                            }
                        },
                        label = stringResource(R.string.label_phone_number),
                        placeholder = stringResource(R.string.placeholder_phone_number),
                        keyboardType = KeyboardType.Phone,
                        errorMessage = mobileNumberError
                    )

                    Text(stringResource(R.string.label_gender), style = MaterialTheme.typography.titleSmall)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RadioButton(
                            selected = user.gender == "Male",
                            onClick = { viewModel.updateUser(user.copy(gender = "Male")) }
                        )
                        Text(stringResource(R.string.gender_male))

                        RadioButton(
                            selected = user.gender == "Female",
                            onClick = { viewModel.updateUser(user.copy(gender = "Female")) }
                        )
                        Text(stringResource(R.string.gender_female))

                        RadioButton(
                            selected = user.gender == "Other",
                            onClick = { viewModel.updateUser(user.copy(gender = "Other")) }
                        )
                        Text(stringResource(R.string.gender_other))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = { onBackClick() }) {
                            Text(stringResource(R.string.button_cancel), color = MaterialTheme.colorScheme.onSurface)
                        }

                        PrimaryButton(
                            text = stringResource(R.string.button_save_changes),
                            onClick = {
                                val validationErrors = listOfNotNull(
                                    if (user.displayName.isEmpty()) context.getString(R.string.error_name_empty) else null,
                                    if (profileImageUri == null && user.profilePictureUrl.isEmpty()) context.getString(R.string.error_profile_picture_required) else null,
                                    dobErrorMessage,
                                    mobileNumberError
                                )

                                if (validationErrors.isEmpty()) {
                                    viewModel.saveUserProfile(
                                        imageUri = profileImageUri,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.profile_saved_successfully),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            onBackClick()
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                } else {
                                    showValidationError = true
                                    Toast.makeText(
                                        context,
                                        validationErrors.joinToString("\n"),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
