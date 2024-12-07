package com.su.communityconnect.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.su.communityconnect.BuildConfig
import com.su.communityconnect.model.EventLocation
import com.su.communityconnect.model.state.EventLocationState
import com.su.communityconnect.ui.components.BackButton
import com.su.communityconnect.ui.components.PrimaryButton

@Composable
fun MapScreen(
    onBackClick: () -> Unit
) {
    val eventLocation = EventLocationState.eventLocation
    val context = LocalContext.current


    // API key directly used from BuildConfig
    val apiKey = BuildConfig.PLACES_API_KEY

    // If location is null, navigate back
    LaunchedEffect(eventLocation) {
        if (eventLocation == null) {
            onBackClick()
        }
    }

    if (eventLocation == null) return

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(eventLocation.latitude, eventLocation.longitude),
            15f
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            properties = MapProperties(mapStyleOptions = null),
        ) {
            Marker(
                state = MarkerState(position = LatLng(eventLocation.latitude, eventLocation.longitude)),
                title = eventLocation.displayName,
                snippet = eventLocation.fullAddress
            )
        }

        // Back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            BackButton(onBackClick = {
                EventLocationState.clearEventLocation()
                onBackClick()
            })
        }

        // Directions button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            PrimaryButton(
                text = "Get Direction",
                onClick = {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${eventLocation.latitude},${eventLocation.longitude}&key=$apiKey")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(mapIntent)
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}
