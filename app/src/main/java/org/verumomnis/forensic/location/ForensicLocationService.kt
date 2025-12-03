package org.verumomnis.forensic.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Forensic Location Service for GPS evidence tagging
 * 
 * Provides accurate GPS coordinates for evidence collection,
 * enabling court-admissible location verification.
 * 
 * @author Liam Highcock
 */
class ForensicLocationService(private val context: Context) {

    companion object {
        private const val TAG = "ForensicLocation"
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Gets the current GPS location
     * @return ForensicLocation with coordinates and accuracy, or null if unavailable
     */
    suspend fun getCurrentLocation(): ForensicLocation? {
        if (!hasLocationPermission()) {
            Log.w(TAG, "Location permission not granted")
            return null
        }

        return try {
            val location = getLastKnownLocation() ?: getCurrentLocationAsync()
            location?.let {
                ForensicLocation(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    accuracy = it.accuracy,
                    altitude = if (it.hasAltitude()) it.altitude else null,
                    timestamp = it.time,
                    provider = it.provider ?: "unknown"
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception getting location", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location", e)
            null
        }
    }

    /**
     * Checks if location permission is granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Gets the last known location (fast but may be stale)
     */
    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    /**
     * Gets current location with high accuracy
     */
    @Suppress("MissingPermission")
    private suspend fun getCurrentLocationAsync(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val cancellationTokenSource = CancellationTokenSource()
        
        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            continuation.resume(location)
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }
}

/**
 * Data class representing a forensic location with full metadata
 */
data class ForensicLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val altitude: Double? = null,
    val timestamp: Long,
    val provider: String
) {
    /**
     * Formats location for display
     */
    fun toDisplayString(): String {
        return "%.6f, %.6f (±%.0fm)".format(latitude, longitude, accuracy)
    }

    /**
     * Formats location for forensic report
     */
    fun toForensicString(): String {
        return buildString {
            append("Latitude: $latitude\n")
            append("Longitude: $longitude\n")
            append("Accuracy: ${accuracy}m\n")
            altitude?.let { append("Altitude: ${it}m\n") }
            append("Provider: $provider\n")
            append("Captured: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", java.util.Locale.US).format(java.util.Date(timestamp))}")
        }
    }
}
