package com.su.communityconnect.model.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

class LocationProvider(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        processLocation(location, continuation)
                    } else {
                        // Request a fresh location if no last location is available
                        val locationRequest = LocationRequest.Builder(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            5000 // 5 seconds
                        ).build()

                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    val freshLocation = locationResult.lastLocation
                                    if (freshLocation != null) {
                                        processLocation(freshLocation, continuation)
                                    } else {
                                        continuation.resume(null)
                                    }
                                    fusedLocationClient.removeLocationUpdates(this)
                                }
                            },
                            null
                        )
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null) // Handle failure gracefully
                }
        }
    }

    private fun processLocation(location: Location, continuation: kotlin.coroutines.Continuation<LocationData?>) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val city = addresses[0].locality ?: "Unknown City"
            continuation.resume(LocationData(city = city))
        } else {
            continuation.resume(null)
        }
    }

    fun isLocationPermissionGranted(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}

data class LocationData(val city: String)
