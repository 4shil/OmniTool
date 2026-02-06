package com.omnitool.features.file.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * PDF Viewer ViewModel
 * 
 * Features:
 * - Open PDF files
 * - Page navigation
 * - Zoom support
 * - Page count display
 */
@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    var selectedPdfUri by mutableStateOf<Uri?>(null)
        private set
    
    var currentPage by mutableIntStateOf(0)
        private set
    
    var totalPages by mutableIntStateOf(0)
        private set
    
    var currentPageBitmap by mutableStateOf<ImageBitmap?>(null)
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var zoomLevel by mutableFloatStateOf(1.0f)
        private set
    
    var pdfFileName by mutableStateOf("")
        private set
    
    private var pdfRenderer: PdfRenderer? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    
    fun openPdf(uri: Uri) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            try {
                closeCurrentPdf()
                selectedPdfUri = uri
                
                // Get file name
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    pdfFileName = if (nameIndex >= 0) cursor.getString(nameIndex) else "PDF Document"
                }
                
                withContext(Dispatchers.IO) {
                    // Copy to temp file since PdfRenderer needs a seekable file descriptor
                    val tempFile = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    parcelFileDescriptor = ParcelFileDescriptor.open(
                        tempFile,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                    
                    pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
                    totalPages = pdfRenderer!!.pageCount
                }
                
                currentPage = 0
                renderCurrentPage()
            } catch (e: Exception) {
                errorMessage = "Failed to open PDF: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun goToPage(page: Int) {
        if (page in 0 until totalPages && page != currentPage) {
            currentPage = page
            renderCurrentPage()
        }
    }
    
    fun nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++
            renderCurrentPage()
        }
    }
    
    fun previousPage() {
        if (currentPage > 0) {
            currentPage--
            renderCurrentPage()
        }
    }
    
    fun setZoom(zoom: Float) {
        zoomLevel = zoom.coerceIn(0.5f, 3.0f)
        renderCurrentPage()
    }
    
    fun zoomIn() {
        setZoom(zoomLevel + 0.25f)
    }
    
    fun zoomOut() {
        setZoom(zoomLevel - 0.25f)
    }
    
    private fun renderCurrentPage() {
        viewModelScope.launch {
            isLoading = true
            
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    pdfRenderer?.let { renderer ->
                        val page = renderer.openPage(currentPage)
                        
                        // Calculate dimensions with zoom
                        val width = (page.width * zoomLevel * 2).toInt()
                        val height = (page.height * zoomLevel * 2).toInt()
                        
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        bitmap.eraseColor(android.graphics.Color.WHITE)
                        
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                        
                        bitmap
                    }
                }
                
                currentPageBitmap = bitmap?.asImageBitmap()
            } catch (e: Exception) {
                errorMessage = "Failed to render page: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    private fun closeCurrentPdf() {
        try {
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        } catch (_: Exception) {}
        
        pdfRenderer = null
        parcelFileDescriptor = null
        currentPageBitmap = null
        totalPages = 0
        currentPage = 0
    }
    
    fun clear() {
        closeCurrentPdf()
        selectedPdfUri = null
        pdfFileName = ""
        errorMessage = null
        zoomLevel = 1.0f
    }
    
    override fun onCleared() {
        super.onCleared()
        closeCurrentPdf()
    }
}
