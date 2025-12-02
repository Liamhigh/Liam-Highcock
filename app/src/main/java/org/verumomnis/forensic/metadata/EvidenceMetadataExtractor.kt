package org.verumomnis.forensic.metadata

import android.content.Context
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.verumomnis.forensic.core.ForensicLocation
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Evidence Metadata Extractor
 * 
 * Extracts EXIF and other metadata from evidence files.
 * All operations run on background thread (Dispatchers.IO).
 * 
 * @author Liam Highcock
 * @version 1.0.0
 */
class EvidenceMetadataExtractor(private val context: Context) {

    /**
     * Extracted metadata from an image
     */
    data class ImageMetadata(
        val width: Int?,
        val height: Int?,
        val orientation: Int?,
        val dateTaken: Long?,
        val make: String?,
        val model: String?,
        val software: String?,
        val location: ForensicLocation?,
        val flash: Boolean?,
        val focalLength: Double?,
        val aperture: Double?,
        val iso: Int?,
        val exposureTime: String?,
        val whiteBalance: String?,
        val exifVersion: String?
    ) {
        companion object {
            /**
             * Create an empty ImageMetadata instance when extraction fails
             */
            fun empty() = ImageMetadata(
                width = null, height = null, orientation = null, dateTaken = null,
                make = null, model = null, software = null, location = null,
                flash = null, focalLength = null, aperture = null, iso = null,
                exposureTime = null, whiteBalance = null, exifVersion = null
            )
        }
    }

    /**
     * Extract metadata from an image file (runs on IO thread)
     */
    suspend fun extractImageMetadata(imageFile: File): ImageMetadata = withContext(Dispatchers.IO) {
        try {
            val exif = ExifInterface(imageFile.absolutePath)
            extractFromExif(exif, imageFile)
        } catch (e: Exception) {
            ImageMetadata.empty()
        }
    }

    /**
     * Extract metadata from image bytes (runs on IO thread)
     */
    suspend fun extractImageMetadata(imageBytes: ByteArray): ImageMetadata = withContext(Dispatchers.IO) {
        try {
            // Write bytes to temporary file for EXIF extraction
            val tempFile = File.createTempFile("exif_extract", ".jpg", context.cacheDir)
            try {
                tempFile.writeBytes(imageBytes)
                val exif = ExifInterface(tempFile.absolutePath)
                extractFromExif(exif, tempFile)
            } finally {
                tempFile.delete()
            }
        } catch (e: Exception) {
            ImageMetadata.empty()
        }
    }

    /**
     * Extract metadata from input stream (runs on IO thread)
     */
    suspend fun extractImageMetadata(uri: Uri): ImageMetadata = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                extractImageMetadata(bytes)
            } ?: ImageMetadata.empty()
        } catch (e: Exception) {
            ImageMetadata.empty()
        }
    }

    private suspend fun extractFromExif(exif: ExifInterface, imageFile: File): ImageMetadata {
        // Get image dimensions
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(imageFile.absolutePath, options)
        
        // Extract GPS location
        val location = extractLocation(exif)
        
        // Extract date taken
        val dateTaken = extractDateTaken(exif)
        
        // Extract camera info
        val make = exif.getAttribute(ExifInterface.TAG_MAKE)
        val model = exif.getAttribute(ExifInterface.TAG_MODEL)
        val software = exif.getAttribute(ExifInterface.TAG_SOFTWARE)
        
        // Extract shooting parameters
        val flash = exif.getAttributeInt(ExifInterface.TAG_FLASH, -1).let { 
            if (it >= 0) (it and 1) == 1 else null 
        }
        
        val focalLength = exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, -1.0).let {
            if (it > 0) it else null
        }
        
        val aperture = exif.getAttributeDouble(ExifInterface.TAG_F_NUMBER, -1.0).let {
            if (it > 0) it else null
        }
        
        val iso = exif.getAttributeInt(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, -1).let {
            if (it > 0) it else null
        }
        
        val exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
        
        val whiteBalance = exif.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, -1).let { wb ->
            when (wb) {
                ExifInterface.WHITEBALANCE_AUTO -> "Auto"
                ExifInterface.WHITEBALANCE_MANUAL -> "Manual"
                else -> null
            }
        }
        
        val exifVersion = exif.getAttribute(ExifInterface.TAG_EXIF_VERSION)
        
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        
        return ImageMetadata(
            width = if (options.outWidth > 0) options.outWidth else null,
            height = if (options.outHeight > 0) options.outHeight else null,
            orientation = if (orientation != ExifInterface.ORIENTATION_UNDEFINED) orientation else null,
            dateTaken = dateTaken,
            make = make,
            model = model,
            software = software,
            location = location,
            flash = flash,
            focalLength = focalLength,
            aperture = aperture,
            iso = iso,
            exposureTime = exposureTime,
            whiteBalance = whiteBalance,
            exifVersion = exifVersion
        )
    }

    private fun extractLocation(exif: ExifInterface): ForensicLocation? {
        val latLong = exif.latLong ?: return null
        
        return ForensicLocation(
            latitude = latLong[0],
            longitude = latLong[1],
            accuracy = 0f, // EXIF doesn't contain accuracy
            altitude = exif.getAltitude(Double.NaN).let { if (it.isNaN()) null else it },
            timestamp = System.currentTimeMillis(),
            provider = "EXIF"
        )
    }

    private fun extractDateTaken(exif: ExifInterface): Long? {
        val dateString = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
            ?: return null
        
        return try {
            val format = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)
            format.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format image metadata for display
     */
    fun formatMetadataForDisplay(metadata: ImageMetadata): String {
        return buildString {
            appendLine("IMAGE METADATA")
            appendLine("══════════════")
            
            metadata.width?.let { appendLine("Dimensions: ${it}x${metadata.height}") }
            metadata.make?.let { appendLine("Camera Make: $it") }
            metadata.model?.let { appendLine("Camera Model: $it") }
            metadata.software?.let { appendLine("Software: $it") }
            metadata.dateTaken?.let { 
                appendLine("Date Taken: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(it)}")
            }
            metadata.location?.let { loc ->
                appendLine("GPS Location: ${loc.latitude}, ${loc.longitude}")
                loc.altitude?.let { appendLine("Altitude: ${it}m") }
            }
            metadata.flash?.let { appendLine("Flash: ${if (it) "Yes" else "No"}") }
            metadata.focalLength?.let { appendLine("Focal Length: ${it}mm") }
            metadata.aperture?.let { appendLine("Aperture: f/$it") }
            metadata.iso?.let { appendLine("ISO: $it") }
            metadata.exposureTime?.let { appendLine("Exposure: ${it}s") }
            metadata.whiteBalance?.let { appendLine("White Balance: $it") }
        }
    }

    /**
     * Format metadata for forensic report inclusion
     */
    fun formatMetadataForReport(metadata: ImageMetadata): Map<String, String> {
        val result = mutableMapOf<String, String>()
        
        metadata.width?.let { result["Dimensions"] = "${it}x${metadata.height}" }
        metadata.make?.let { result["Camera Make"] = it }
        metadata.model?.let { result["Camera Model"] = it }
        metadata.software?.let { result["Software"] = it }
        metadata.dateTaken?.let { 
            result["Date Taken"] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US).format(it)
        }
        metadata.location?.let { loc ->
            result["EXIF GPS"] = "${loc.latitude}, ${loc.longitude}"
        }
        metadata.flash?.let { result["Flash Used"] = if (it) "Yes" else "No" }
        metadata.focalLength?.let { result["Focal Length"] = "${it}mm" }
        metadata.aperture?.let { result["Aperture"] = "f/$it" }
        metadata.iso?.let { result["ISO"] = it.toString() }
        metadata.exposureTime?.let { result["Exposure Time"] = "${it}s" }
        metadata.whiteBalance?.let { result["White Balance"] = it }
        
        return result
    }
}
