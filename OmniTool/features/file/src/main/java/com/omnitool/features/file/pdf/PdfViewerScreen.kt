package com.omnitool.features.file.pdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * PDF Viewer Screen
 * 
 * Features:
 * - PDF file picker
 * - Page display
 * - Navigation controls
 * - Zoom controls
 */
@Composable
fun PdfViewerScreen(
    onBack: () -> Unit,
    viewModel: PdfViewerViewModel = hiltViewModel()
) {
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.openPdf(it) }
    }
    
    ToolWorkspaceScreen(
        toolName = "PDF Viewer",
        toolIcon = OmniToolIcons.PdfViewer,
        accentColor = OmniToolTheme.colors.softCoral,
        onBack = onBack,
        inputContent = {
            if (viewModel.selectedPdfUri != null) {
                // File info and controls
                PdfControls(
                    fileName = viewModel.pdfFileName,
                    currentPage = viewModel.currentPage,
                    totalPages = viewModel.totalPages,
                    zoomLevel = viewModel.zoomLevel,
                    onPrevious = { viewModel.previousPage() },
                    onNext = { viewModel.nextPage() },
                    onZoomIn = { viewModel.zoomIn() },
                    onZoomOut = { viewModel.zoomOut() }
                )
            } else {
                // File picker prompt
                FilePickerPrompt(onPickFile = { pdfPicker.launch("application/pdf") })
            }
        },
        outputContent = {
            when {
                viewModel.errorMessage != null -> {
                    ErrorDisplay(viewModel.errorMessage!!)
                }
                viewModel.selectedPdfUri != null -> {
                    PdfPageDisplay(
                        bitmap = viewModel.currentPageBitmap,
                        isLoading = viewModel.isLoading,
                        zoomLevel = viewModel.zoomLevel
                    )
                }
                else -> {
                    EmptyState()
                }
            }
        },
        primaryActionLabel = if (viewModel.selectedPdfUri == null) "Open PDF" else null,
        onPrimaryAction = { pdfPicker.launch("application/pdf") },
        secondaryActionLabel = if (viewModel.selectedPdfUri != null) "Close" else null,
        onSecondaryAction = if (viewModel.selectedPdfUri != null) {
            { viewModel.clear() }
        } else null
    )
}

@Composable
private fun FilePickerPrompt(onPickFile: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onPickFile),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = OmniToolIcons.PdfViewer,
                contentDescription = null,
                tint = OmniToolTheme.colors.softCoral,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to open a PDF file",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun PdfControls(
    fileName: String,
    currentPage: Int,
    totalPages: Int,
    zoomLevel: Float,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // File name
        Text(
            text = fileName,
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Navigation and zoom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page navigation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onPrevious,
                    enabled = currentPage > 0
                ) {
                    Icon(
                        imageVector = OmniToolIcons.ArrowBack,
                        contentDescription = "Previous",
                        tint = if (currentPage > 0) OmniToolTheme.colors.textPrimary 
                               else OmniToolTheme.colors.textMuted
                    )
                }
                
                Text(
                    text = "${currentPage + 1} / $totalPages",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.softCoral
                )
                
                IconButton(
                    onClick = onNext,
                    enabled = currentPage < totalPages - 1
                ) {
                    Icon(
                        imageVector = OmniToolIcons.ArrowForward,
                        contentDescription = "Next",
                        tint = if (currentPage < totalPages - 1) OmniToolTheme.colors.textPrimary 
                               else OmniToolTheme.colors.textMuted
                    )
                }
            }
            
            // Zoom controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onZoomOut, enabled = zoomLevel > 0.5f) {
                    Icon(
                        imageVector = OmniToolIcons.ZoomOut,
                        contentDescription = "Zoom out",
                        tint = OmniToolTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = "${(zoomLevel * 100).toInt()}%",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
                
                IconButton(onClick = onZoomIn, enabled = zoomLevel < 3.0f) {
                    Icon(
                        imageVector = OmniToolIcons.ZoomIn,
                        contentDescription = "Zoom in",
                        tint = OmniToolTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PdfPageDisplay(
    bitmap: androidx.compose.ui.graphics.ImageBitmap?,
    isLoading: Boolean,
    zoomLevel: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = OmniToolTheme.colors.softCoral)
            }
            bitmap != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "PDF page",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
            else -> {
                Text(
                    text = "No page to display",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun ErrorDisplay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.softCoral.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.softCoral,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = OmniToolIcons.PdfViewer,
                contentDescription = null,
                tint = OmniToolTheme.colors.textMuted.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Open a PDF to view",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}
