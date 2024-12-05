package com.su.communityconnect.ui.screens.event

import androidx.compose.ui.text.input.KeyboardType
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.EventLocation
import com.su.communityconnect.model.PromoCode
import com.su.communityconnect.ui.components.*
import com.su.communityconnect.utils.convertUriListToStringList

@Composable
fun EventFormScreen(
    viewModel: EventViewModel = hiltViewModel(),
    existingEvent: Event? = null,
    onBackClick: () -> Unit,
    onEventSaved: () -> Unit
) {
    val formState = viewModel.eventFormState.collectAsState()
    val validationError = viewModel.validationError.collectAsState()
    val context = LocalContext.current
    val categories = viewModel.categories.collectAsState()

    // State to track loading status
    var isLoading by remember { mutableStateOf(false) }

    // Initialize form state if editing an existing event
    LaunchedEffect(existingEvent) {
        existingEvent?.let { viewModel.initializeForm(it) }
    }

    // Observe validation errors
    LaunchedEffect(validationError.value) {
        validationError.value?.let { error ->
            val errorMessage = when (error) {
                EventValidationError.TITLE_REQUIRED -> "Title is required"
                EventValidationError.DESCRIPTION_REQUIRED -> "Description is required"
                EventValidationError.INVALID_EVENT_DATE -> "Invalid Event Date"
                EventValidationError.INVALID_BOOKING_START_DATE -> "Invalid Booking Start Date"
                EventValidationError.INVALID_BOOKING_END_DATE -> "Invalid Booking End Date"
                EventValidationError.INVALID_CAPACITY -> "Invalid Capacity"
                EventValidationError.INVALID_PRICE -> "Invalid Price"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Show loading indicator
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Submitting...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BackButton(
                    onBackClick = onBackClick,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (existingEvent == null) "Create Event" else "Edit Event",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp)) // Add space below the title

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        TextField(
                            value = formState.value.title,
                            onValueChange = { viewModel.updateForm { copy(title = it) } },
                            label = "Event Title",
                            placeholder = "Enter event title"
                        )
                    }

                    item {
                        TextField(
                            value = formState.value.description,
                            onValueChange = { viewModel.updateForm { copy(description = it) } },
                            label = "Event Description",
                            placeholder = "Enter event description",
                            isTextArea = true
                        )
                    }

                    item {
                        DropdownField(
                            label = "Category",
                            options = categories.value.map { DropdownOption(it.id, it.name) },
                            selectedOptionId = formState.value.category,
                            onOptionSelected = { selectedId ->
                                viewModel.updateForm { copy(category = selectedId) }
                            }
                        )
                    }

                    item {
                        DateTimePickerField(
                            label = "Event Happening Timestamp",
                            onDateTimeSelected = { viewModel.updateForm { copy(eventDate = it) } }
                        )
                    }

                    item {
                        DateTimePickerField(
                            label = "Booking Start Timestamp",
                            onDateTimeSelected = { viewModel.updateForm { copy(bookingStartDate = it) } },
                        )
                    }

                    item {
                        DateTimePickerField(
                            label = "Booking End Timestamp",
                            onDateTimeSelected = { viewModel.updateForm { copy(bookingEndDate = it) } },
                        )
                    }

                    item {
                        LocationAutocompleteField(
                            context = context,
                            label = "Event Location",
                            placeholder = "Search for a location",
                            onLocationSelected = { id, city, shortAddress, fullAddress, lat, lng, displayName ->
                                viewModel.updateForm {
                                    copy(
                                        location = EventLocation(
                                            id = id,
                                            city = city,
                                            shortAddress = shortAddress,
                                            fullAddress = fullAddress,
                                            latitude = lat,
                                            longitude = lng,
                                            displayName = displayName
                                        )
                                    )
                                }
                            }
                        )
                    }

                    item {
                        TextField(
                            value = formState.value.guideline,
                            onValueChange = { viewModel.updateForm { copy(guideline = it) } },
                            label = "Guideline",
                            placeholder = "Enter event guidelines",
                            isTextArea = true
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Ensure rawInput reflects the prefilled value of costPerTicket
                            var rawCostPerTicketInput by remember { mutableStateOf(formState.value.costPerTicket.toString()) }

                            TextField(
                                value = rawCostPerTicketInput,
                                onValueChange = { input ->
                                    if (input.isEmpty()) {
                                        // Handle empty input (e.g., user cleared the field)
                                        rawCostPerTicketInput = ""
                                        viewModel.updateForm { copy(costPerTicket = 0.0) }
                                    } else {
                                        // Validate and allow partial decimals (like "25." or "25.6")
                                        val isValid =
                                            input.toDoubleOrNull() != null || input.endsWith(".")
                                        if (isValid) {
                                            rawCostPerTicketInput = input
                                            val parsedValue = input.toDoubleOrNull() ?: 0.0
                                            viewModel.updateForm { copy(costPerTicket = parsedValue) }
                                        }
                                    }
                                },
                                label = "Cost Per Ticket",
                                placeholder = "Enter ticket price",
                                keyboardType = KeyboardType.Decimal,
                                modifier = Modifier.weight(1f)
                            )

                            var rawInputTotalCapacity by remember { mutableStateOf(formState.value.totalCapacity.toString()) }

                            TextField(
                                value = rawInputTotalCapacity,
                                onValueChange = { input ->
                                    if (input.isEmpty()) {
                                        rawInputTotalCapacity = ""
                                        viewModel.updateForm { copy(totalCapacity = 0) }
                                    } else {
                                        val isValid = input.toIntOrNull() != null
                                        if (isValid) {
                                            rawInputTotalCapacity = input
                                            val parsedValue = input.toIntOrNull() ?: 0
                                            viewModel.updateForm { copy(totalCapacity = parsedValue) }
                                        }
                                    }
                                },
                                label = "Total Capacity",
                                placeholder = "Enter total capacity",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )

                        }
                    }

                    item {
                        var rawInputTicketLimit by remember {
                            mutableStateOf(
                                formState.value.ticketLimitPerPerson?.toString() ?: ""
                            )
                        }

                        TextField(
                            value = rawInputTicketLimit,
                            onValueChange = { input ->
                                if (input.isEmpty()) {
                                    rawInputTicketLimit = ""
                                    viewModel.updateForm { copy(ticketLimitPerPerson = null) }
                                } else {
                                    val isValid = input.toIntOrNull() != null
                                    if (isValid) {
                                        rawInputTicketLimit = input
                                        val parsedValue = input.toIntOrNull()
                                        viewModel.updateForm { copy(ticketLimitPerPerson = parsedValue) }
                                    }
                                }
                            },
                            label = "Ticket Limit per Account",
                            placeholder = "Enter ticket limit count",
                            keyboardType = KeyboardType.Number
                        )
                    }

                    item {
                        PromoCodeCreator(
                            label = "Promo Code",
                            promoCodes = formState.value.promoCode,
                            onAddPromoCode = {
                                viewModel.updateForm {
                                    copy(promoCode = promoCode + PromoCode("", 0.0))
                                }
                            },
                            onRemovePromoCode = { index ->
                                viewModel.updateForm {
                                    copy(
                                        promoCode = promoCode.toMutableList()
                                            .apply { removeAt(index) })
                                }
                            },
                            onUpdatePromoCode = { index, updatedPromoCode ->
                                viewModel.updateForm {
                                    copy(
                                        promoCode = promoCode.toMutableList()
                                            .apply { set(index, updatedPromoCode) })
                                }
                            },
                            onValidationError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    item {
                        ImageUploaderField(
                            label = "Event Images",
                            maxImages = 5,
                            onImagesSelected = { images ->
                                viewModel.updateForm {
                                    copy(
                                        images = convertUriListToStringList(
                                            images
                                        )
                                    )
                                }
                            }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End // Align the button to the right
                        ) {
                            PrimaryButton(
                                horizontalPadding = 20.dp,
                                text = if (existingEvent == null) "Create Event" else "Save Changes",
                                onClick = {
                                    isLoading = true
                                    viewModel.validateAndSaveEvent(
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Event saved successfully!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            onEventSaved()
                                        },
                                        onError = { errorMessage ->
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Error: $errorMessage",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

