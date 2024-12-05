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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.su.communityconnect.model.Event
import com.su.communityconnect.model.EventLocation
import com.su.communityconnect.model.PromoCode
import com.su.communityconnect.ui.components.*
import com.su.communityconnect.utils.convertUriListToStringList
import com.su.communityconnect.R

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

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(existingEvent) {
        existingEvent?.let { viewModel.initializeForm(it) }
    }

    LaunchedEffect(validationError.value) {
        validationError.value?.let { error ->
            val errorMessage = when (error) {
                EventValidationError.TITLE_REQUIRED -> context.getString(R.string.error_title_required)
                EventValidationError.DESCRIPTION_REQUIRED -> context.getString(R.string.error_description_required)
                EventValidationError.INVALID_EVENT_DATE -> context.getString(R.string.error_invalid_event_date)
                EventValidationError.INVALID_BOOKING_START_DATE -> context.getString(R.string.error_invalid_booking_start_date)
                EventValidationError.INVALID_BOOKING_END_DATE -> context.getString(R.string.error_invalid_booking_end_date)
                EventValidationError.INVALID_CAPACITY -> context.getString(R.string.error_invalid_capacity)
                EventValidationError.INVALID_PRICE -> context.getString(R.string.error_invalid_price)
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.submitting),
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
                        text = if (existingEvent == null) stringResource(R.string.create_event) else stringResource(R.string.edit_event),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        TextField(
                            value = formState.value.title,
                            onValueChange = { viewModel.updateForm { copy(title = it) } },
                            label = stringResource(R.string.label_event_title),
                            placeholder = stringResource(R.string.placeholder_event_title)
                        )
                    }

                    item {
                        TextField(
                            value = formState.value.description,
                            onValueChange = { viewModel.updateForm { copy(description = it) } },
                            label = stringResource(R.string.label_event_description),
                            placeholder = stringResource(R.string.placeholder_event_description),
                            isTextArea = true
                        )
                    }

                    item {
                        DropdownField(
                            label = stringResource(R.string.label_category),
                            options = categories.value.map { DropdownOption(it.id, it.name) },
                            selectedOptionId = formState.value.category,
                            onOptionSelected = { selectedId ->
                                viewModel.updateForm { copy(category = selectedId) }
                            }
                        )
                    }

                    item {
                        DateTimePickerField(
                            label = stringResource(R.string.label_event_date),
                            onDateTimeSelected = { viewModel.updateForm { copy(eventDate = it) } }
                        )
                    }

                    item {
                        DateTimePickerField(
                            label = stringResource(R.string.label_booking_start_date),
                            onDateTimeSelected = { viewModel.updateForm { copy(bookingStartDate = it) } }
                        )
                    }

                    item {
                        DateTimePickerField(
                            label = stringResource(R.string.label_booking_end_date),
                            onDateTimeSelected = { viewModel.updateForm { copy(bookingEndDate = it) } }
                        )
                    }

                    item {
                        LocationAutocompleteField(
                            context = context,
                            label = stringResource(R.string.label_event_location),
                            placeholder = stringResource(R.string.placeholder_event_location),
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
                            label = stringResource(R.string.label_guideline),
                            placeholder = stringResource(R.string.placeholder_guideline),
                            isTextArea = true
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            var rawCostPerTicketInput by remember { mutableStateOf(formState.value.costPerTicket.toString()) }

                            TextField(
                                value = rawCostPerTicketInput,
                                onValueChange = { input -> 
                                    if (input.isEmpty()) {
                                        rawCostPerTicketInput = ""
                                        viewModel.updateForm { copy(costPerTicket = 0.0) }
                                    } else {
                                        val isValid = input.toDoubleOrNull() != null || input.endsWith(".")
                                        if (isValid) {
                                            rawCostPerTicketInput = input
                                            val parsedValue = input.toDoubleOrNull() ?: 0.0
                                            viewModel.updateForm { copy(costPerTicket = parsedValue) }
                                        }
                                    }
                                },
                                label = stringResource(R.string.label_ticket_price),
                                placeholder = stringResource(R.string.placeholder_ticket_price),
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
                                label = stringResource(R.string.label_capacity),
                                placeholder = stringResource(R.string.placeholder_capacity),
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
                            label = stringResource(R.string.label_ticket_limit),
                            placeholder = stringResource(R.string.placeholder_ticket_limit),
                            keyboardType = KeyboardType.Number
                        )
                    }

                    item {
                        PromoCodeCreator(
                            label = stringResource(R.string.label_promo_code),
                            promoCodes = formState.value.promoCode,
                            onAddPromoCode = {
                                viewModel.updateForm {
                                    copy(promoCode = promoCode + PromoCode("", 0.0))
                                }
                            },
                            onRemovePromoCode = { index ->
                                viewModel.updateForm {
                                    copy(
                                        promoCode = promoCode.toMutableList().apply { removeAt(index) }
                                    )
                                }
                            },
                            onUpdatePromoCode = { index, updatedPromoCode ->
                                viewModel.updateForm {
                                    copy(
                                        promoCode = promoCode.toMutableList().apply { set(index, updatedPromoCode) }
                                    )
                                }
                            },
                            onValidationError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    item {
                        ImageUploaderField(
                            label = stringResource(R.string.label_event_images),
                            maxImages = 5,
                            onImagesSelected = { images ->
                                viewModel.updateForm {
                                    copy(images = convertUriListToStringList(images))
                                }
                            }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            PrimaryButton(
                                horizontalPadding = 20.dp,
                                text = if (existingEvent == null) stringResource(R.string.submit) else stringResource(R.string.save_changes),
                                onClick = {
                                    isLoading = true
                                    viewModel.validateAndSaveEvent(
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.event_saved),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            onEventSaved()
                                        },
                                        onError = { errorMessage ->
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "${context.getString(R.string.error)}: $errorMessage",
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
