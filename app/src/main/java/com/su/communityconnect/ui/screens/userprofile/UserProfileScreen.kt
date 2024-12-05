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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.ui.components.*
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now

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
                            onImageSelected = { uri ->
                                profileImageUri = uri
                            },
                            size = 120
                        )
                    }

                    TextField(
                        value = user.displayName,
                        onValueChange = {
                            if (it.any { char -> char.isDigit() }) {
                                nameError = "Name cannot contain numbers."
                            } else {
                                nameError = null
                                viewModel.updateUser(user.copy(displayName = it))
                            }
                        },
                        label = "Full Name",
                        placeholder = "Enter your name",
                        errorMessage = nameError
                    )

                    TextField(
                        value = user.email,
                        onValueChange = {},
                        label = "Email",
                        placeholder = "Your email address",
                        readOnly = true
                    )

                    DatePickerField(
                        label = "Date of Birth",
                        placeholder = "Select your date of birth",
                        startDate = user.dateOfBirth?.let { LocalDate.parse(it) } ?: LocalDate.now(),
                        prevSelectedDate = user.dateOfBirth?.let { LocalDate.parse(it) } ?: null,
                        onDateSelected = { date ->
                            if (date >= LocalDate.now()) {
                                dobErrorMessage = "Date of Birth must be in the past."
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
                                // Allow backspace to clear the input
                                mobileNumberError = null
                                viewModel.updateUser(user.copy(phone = ""))
                            } else {
                                val regex = "^[+]?[0-9]{0,15}\$".toRegex() // Allow partial inputs during typing
                                if (!regex.matches(input)) {
                                    mobileNumberError = "Invalid phone number format."
                                } else {
                                    mobileNumberError = null
                                    viewModel.updateUser(user.copy(phone = input))
                                }
                            }
                        },
                        label = "Phone Number",
                        placeholder = "Enter your phone number",
                        keyboardType = KeyboardType.Phone,
                        errorMessage = mobileNumberError
                    )


                    Text("Gender", style = MaterialTheme.typography.titleSmall)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RadioButton(
                            selected = user.gender == "Male",
                            onClick = { viewModel.updateUser(user.copy(gender = "Male")) }
                        )
                        Text("Male")

                        RadioButton(
                            selected = user.gender == "Female",
                            onClick = { viewModel.updateUser(user.copy(gender = "Female")) }
                        )
                        Text("Female")

                        RadioButton(
                            selected = user.gender == "Other",
                            onClick = { viewModel.updateUser(user.copy(gender = "Other")) }
                        )
                        Text("Other")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = { onBackClick() }) {
                            Text("Cancel")
                        }

                        PrimaryButton(
                            text = "Save Changes",
                            onClick = {
                                val validationErrors = listOfNotNull(
                                    if (user.displayName.isEmpty()) "Name cannot be empty." else null,
                                    if (profileImageUri == null && user.profilePictureUrl.isEmpty()) "Profile picture is required." else null,
                                    dobErrorMessage,
                                    mobileNumberError
                                )

                                if (validationErrors.isEmpty()) {
                                    viewModel.saveUserProfile(
                                        imageUri = profileImageUri,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Profile saved successfully!",
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




