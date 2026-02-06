package com.omnitool.features.file.exif

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * EXIF Viewer ViewModel
 * 
 * Features:
 * - Read EXIF data from images
 * - Display camera settings
 * - Show GPS location if available
 * - Display date/time info
 */
@HiltViewModel
class ExifViewerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set
    
    var exifData by mutableStateOf<List<ExifEntry>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var hasGpsData by mutableStateOf(false)
        private set
    
    var latitude by mutableStateOf<Double?>(null)
        private set
    
    var longitude by mutableStateOf<Double?>(null)
        private set
    
    fun setImageUri(uri: Uri) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            selectedImageUri = uri
            exifData = emptyList()
            hasGpsData = false
            latitude = null
            longitude = null
            
            try {
                val data = withContext(Dispatchers.IO) {
                    extractExifData(uri)
                }
                exifData = data
            } catch (e: Exception) {
                errorMessage = "Failed to read EXIF data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    private fun extractExifData(uri: Uri): List<ExifEntry> {
        val entries = mutableListOf<ExifEntry>()
        
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val exif = ExifInterface(stream)
            
            // Camera Info
            addEntry(entries, "Camera", ExifCategory.CAMERA,
                exif.getAttribute(ExifInterface.TAG_MAKE),
                exif.getAttribute(ExifInterface.TAG_MODEL))
            
            addIfNotNull(entries, "Lens", ExifCategory.CAMERA,
                exif.getAttribute(ExifInterface.TAG_LENS_MAKE)?.let { make ->
                    val model = exif.getAttribute(ExifInterface.TAG_LENS_MODEL) ?: ""
                    "$make $model".trim()
                })
            
            // Exposure Settings
            addIfNotNull(entries, "Aperture", ExifCategory.EXPOSURE,
                exif.getAttribute(ExifInterface.TAG_F_NUMBER)?.let { "f/$it" })
            
            addIfNotNull(entries, "Shutter Speed", ExifCategory.EXPOSURE,
                exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)?.let { 
                    val time = it.toDoubleOrNull() ?: return@let it
                    if (time < 1) "1/${(1/time).toInt()}s" else "${time}s"
                })
            
            addIfNotNull(entries, "ISO", ExifCategory.EXPOSURE,
                exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY))
            
            addIfNotNull(entries, "Focal Length", ExifCategory.EXPOSURE,
                exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)?.let {
                    val parts = it.split("/")
                    if (parts.size == 2) {
                        "${parts[0].toDouble() / parts[1].toDouble()}mm"
                    } else "${it}mm"
                })
            
            addIfNotNull(entries, "Flash", ExifCategory.EXPOSURE,
                exif.getAttribute(ExifInterface.TAG_FLASH)?.let {
                    when (it.toIntOrNull()) {
                        0 -> "Off"
                        1 -> "Fired"
                        5 -> "Fired, Return not detected"
                        7 -> "Fired, Return detected"
                        else -> it
                    }
                })
            
            // Image Info
            addIfNotNull(entries, "Resolution", ExifCategory.IMAGE,
                exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)?.let { w ->
                    val h = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) ?: return@let null
                    "${w} × ${h}"
                })
            
            addIfNotNull(entries, "Color Space", ExifCategory.IMAGE,
                exif.getAttribute(ExifInterface.TAG_COLOR_SPACE)?.let {
                    when (it) {
                        "1" -> "sRGB"
                        "2" -> "Adobe RGB"
                        else -> it
                    }
                })
            
            addIfNotNull(entries, "Orientation", ExifCategory.IMAGE,
                exif.getAttribute(ExifInterface.TAG_ORIENTATION)?.let {
                    when (it.toIntOrNull()) {
                        1 -> "Normal"
                        3 -> "Rotated 180°"
                        6 -> "Rotated 90° CW"
                        8 -> "Rotated 90° CCW"
                        else -> "Unknown"
                    }
                })
            
            // Date/Time
            addIfNotNull(entries, "Date Taken", ExifCategory.DATETIME,
                exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL) ?: 
                exif.getAttribute(ExifInterface.TAG_DATETIME))
            
            // GPS
            val latLong = FloatArray(2)
            if (exif.getLatLong(latLong)) {
                hasGpsData = true
                latitude = latLong[0].toDouble()
                longitude = latLong[1].toDouble()
                entries.add(ExifEntry("Latitude", "${latLong[0]}", ExifCategory.GPS))
                entries.add(ExifEntry("Longitude", "${latLong[1]}", ExifCategory.GPS))
                
                addIfNotNull(entries, "Altitude", ExifCategory.GPS,
                    exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE)?.let {
                        val parts = it.split("/")
                        if (parts.size == 2) {
                            "${parts[0].toDouble() / parts[1].toDouble()}m"
                        } else "${it}m"
                    })
            }
            
            // Software
            addIfNotNull(entries, "Software", ExifCategory.OTHER,
                exif.getAttribute(ExifInterface.TAG_SOFTWARE))
            
            addIfNotNull(entries, "Copyright", ExifCategory.OTHER,
                exif.getAttribute(ExifInterface.TAG_COPYRIGHT))
            
            addIfNotNull(entries, "Artist", ExifCategory.OTHER,
                exif.getAttribute(ExifInterface.TAG_ARTIST))
        }
        
        return entries
    }
    
    private fun addEntry(entries: MutableList<ExifEntry>, label: String, category: ExifCategory, vararg values: String?) {
        val combined = values.filterNotNull().joinToString(" ").trim()
        if (combined.isNotEmpty()) {
            entries.add(ExifEntry(label, combined, category))
        }
    }
    
    private fun addIfNotNull(entries: MutableList<ExifEntry>, label: String, category: ExifCategory, value: String?) {
        if (!value.isNullOrBlank()) {
            entries.add(ExifEntry(label, value, category))
        }
    }
    
    fun clear() {
        selectedImageUri = null
        exifData = emptyList()
        errorMessage = null
        hasGpsData = false
        latitude = null
        longitude = null
    }
}

data class ExifEntry(
    val label: String,
    val value: String,
    val category: ExifCategory
)

enum class ExifCategory(val displayName: String) {
    CAMERA("Camera"),
    EXPOSURE("Exposure"),
    IMAGE("Image"),
    DATETIME("Date & Time"),
    GPS("Location"),
    OTHER("Other")
}
