    package com.su.communityconnect.ui.components

    import android.content.Context
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material3.DropdownMenu
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.OutlinedTextFieldDefaults
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextField
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.input.TextFieldValue
    import androidx.compose.ui.unit.dp
    import com.google.android.libraries.places.api.Places
    import com.google.android.libraries.places.api.model.AutocompletePrediction
    import com.google.android.libraries.places.api.model.Place
    import com.google.android.libraries.places.api.net.FetchPlaceRequest
    import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
    import com.google.android.libraries.places.api.net.PlacesClient
    import com.su.communityconnect.BuildConfig
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.Job
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch

    @Composable
    fun LocationAutocompleteField(
        modifier: Modifier = Modifier,
        context: Context,
        label: String,
        placeholder: String,
        onLocationSelected: (String, String, String, String, Double, Double, String) -> Unit, // Updated callback
        onSurface: Boolean = true,
    ) {
        var query by remember { mutableStateOf(TextFieldValue("")) }
        var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
        var isDropdownExpanded by remember { mutableStateOf(false) }
        var debounceJob by remember { mutableStateOf<Job?>(null) } // To handle debouncing

        // Initialize PlacesClient once and remember it
        val placesClient: PlacesClient = remember {
            if (!Places.isInitialized()) {
                Places.initialize(context, BuildConfig.PLACES_API_KEY)
            }
            Places.createClient(context)
        }

        Column(modifier = modifier) {
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            // Input Field
            OutlinedTextField(
                value = query.text,
                placeholder = { Text(placeholder) },
                onValueChange = { newValue ->
                    query = TextFieldValue(newValue)
                    isDropdownExpanded = false

                    // Debounce to reduce API calls
                    debounceJob?.cancel()
                    debounceJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(300) // Wait for 300ms before making API call
                        if (newValue.isNotBlank()) {
                            val request = FindAutocompletePredictionsRequest.builder()
                                .setQuery(newValue)
                                .build()
                            placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response ->
                                    predictions = response.autocompletePredictions
                                    isDropdownExpanded = predictions.isNotEmpty()
                                }
                                .addOnFailureListener {
                                    predictions = emptyList()
                                    isDropdownExpanded = false
                                }
                        } else {
                            predictions = emptyList()
                            isDropdownExpanded = false
                        }
                    }
                },
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                    errorTextColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                predictions.forEach { prediction ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = {
                            Text(text = prediction.getFullText(null).toString())
                        },
                        onClick = {
                            val placeId = prediction.placeId
                            val placeRequest = FetchPlaceRequest.newInstance(
                                placeId, listOf(
                                    Place.Field.ID,
                                    Place.Field.FORMATTED_ADDRESS,
                                    Place.Field.SHORT_FORMATTED_ADDRESS,
                                    Place.Field.DISPLAY_NAME,
                                    Place.Field.LOCATION,
                                    Place.Field.ADDRESS_COMPONENTS
                                )
                            )
                            placesClient.fetchPlace(placeRequest)
                                .addOnSuccessListener { placeResponse ->
                                    val place = placeResponse.place
                                    val id = place.id ?: ""
                                    val fullAddress = place.formattedAddress ?: ""
                                    val shortAddress = place.shortFormattedAddress ?: ""
                                    val displayName = place.displayName ?: ""
                                    val lat = place.location?.latitude ?: 0.0
                                    val lng = place.location?.longitude ?: 0.0

                                    // Extract city from address components
                                    val city = place.addressComponents?.asList()
                                    ?.firstOrNull { it.types.contains("locality") }?.name
                                    ?: displayName // Use displayName as a fallback
                                    ?: shortAddress // Use shortAddress as a fallback
                                    ?: "Unknown City" // Default fallback

                                    // Pass details to callback
                                    onLocationSelected(
                                        id,
                                        city,
                                        shortAddress,
                                        fullAddress,
                                        lat,
                                        lng,
                                        displayName
                                    )
                                }
                                .addOnFailureListener {
                                    // Handle failure case
                                }
                            query = TextFieldValue(prediction.getFullText(null).toString())
                            predictions = emptyList()
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }


