package org.verumomnis.forensic.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import org.verumomnis.forensic.core.ForensicLocation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Forensic Location Service
 * 
 * Provides GPS location capture for evidence geolocation.
 * All location data is stored locally and never transmitted.
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class ForensicLocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if GPS is enabled
     */
    fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * Get current location
     */
    suspend fun getCurrentLocation(): ForensicLocation? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            suspendCancellableCoroutine { continuation ->
                try {
                    val locationRequest = LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        1000
                    ).apply {
                        setMinUpdateDistanceMeters(0f)
                        setMaxUpdates(1)
                        setWaitForAccurateLocation(true)
                    }.build()

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            fusedLocationClient.removeLocationUpdates(this)
                            result.lastLocation?.let { location ->
                                continuation.resume(location.toForensicLocation())
                            } ?: continuation.resume(null)
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )

                    continuation.invokeOnCancellation {
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                    }
                } catch (e: SecurityException) {
                    continuation.resume(null)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get last known location (faster but may be stale)
     */
    suspend fun getLastKnownLocation(): ForensicLocation? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            suspendCancellableCoroutine { continuation ->
                try {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            continuation.resume(location?.toForensicLocation())
                        }
                        .addOnFailureListener {
                            continuation.resume(null)
                        }
                } catch (e: SecurityException) {
                    continuation.resume(null)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extension function to convert Android Location to ForensicLocation
     */
    private fun Location.toForensicLocation(): ForensicLocation {
        return ForensicLocation(
            latitude = latitude,
            longitude = longitude,
            accuracy = accuracy,
            altitude = if (hasAltitude()) altitude else null,
            timestamp = time,
            provider = provider ?: "unknown"
        )
    }

    /**
     * Format location for display
     */
    fun formatLocation(location: ForensicLocation): String {
        return buildString {
            append("Lat: ${String.format("%.6f", location.latitude)}")
            append(", ")
            append("Lon: ${String.format("%.6f", location.longitude)}")
            append(" (±${String.format("%.1f", location.accuracy)}m)")
        }
    }

    /**
     * Format location for forensic report
     */
    fun formatLocationForReport(location: ForensicLocation): String {
        return buildString {
            appendLine("GEOLOCATION DATA")
            appendLine("================")
            appendLine("Latitude: ${location.latitude}")
            appendLine("Longitude: ${location.longitude}")
            appendLine("Accuracy: ±${location.accuracy} meters")
            location.altitude?.let {
                appendLine("Altitude: $it meters")
            }
            appendLine("Provider: ${location.provider}")
            appendLine("Timestamp: ${java.util.Date(location.timestamp)}")
        }
    }
}
