package com.su.communityconnect.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.su.communityconnect.ui.components.LocationAutocompleteField
import com.su.communityconnect.ui.components.PrimaryButton


@Composable
fun LocationSelectionScreen(
    onLocationSelected: (String) -> Unit,
    onRequestCurrentLocation: () -> Unit
) {
    val context = LocalContext.current
    val selectedLocation = remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Select Location",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Location Autocomplete
                LocationAutocompleteField(
                    context = context,
                    label = "",
                    placeholder = "Enter a location",
                    onLocationSelected = { _, city, _, _, _, _, _ ->
                        selectedLocation.value = city
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current Location Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRequestCurrentLocation() }
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = "Use Current Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Use Current Location",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Submit Button
                PrimaryButton(
                    text = "Submit",
                    onClick = { onLocationSelected(selectedLocation.value) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
