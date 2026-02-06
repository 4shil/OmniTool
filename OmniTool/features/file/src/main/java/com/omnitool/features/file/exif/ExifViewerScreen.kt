package com.omnitool.features.file.exif

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * EXIF Viewer Screen
 * 
 * Features:
 * - Image picker
 * - Categorized EXIF data display
 * - GPS location link
 */
@Composable
fun ExifViewerScreen(
    onBack: () -> Unit,
    viewModel: ExifViewerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setImageUri(it) }
    }
    
    ToolWorkspaceScreen(
        toolName = "EXIF Viewer",
        toolIcon = OmniToolIcons.ExifViewer,
        accentColor = OmniToolTheme.colors.skyBlue,
        onBack = onBack,
        inputContent = {
            // Image preview
            ImagePreview(
                uri = viewModel.selectedImageUri,
                isLoading = viewModel.isLoading,
                onPickImage = { imagePicker.launch("image/*") }
            )
        },
        outputContent = {
            when {
                viewModel.errorMessage != null -> {
                    ErrorDisplay(viewModel.errorMessage!!)
                }
                viewModel.exifData.isEmpty() && !viewModel.isLoading -> {
                    EmptyState()
                }
                else -> {
                    ExifDataDisplay(
                        entries = viewModel.exifData,
                        hasGps = viewModel.hasGpsData,
                        latitude = viewModel.latitude,
                        longitude = viewModel.longitude
                    )
                }
            }
        },
        primaryActionLabel = if (viewModel.selectedImageUri == null) "Pick Image" else null,
        onPrimaryAction = { imagePicker.launch("image/*") },
        secondaryActionLabel = if (viewModel.selectedImageUri != null) "Clear" else null,
        onSecondaryAction = if (viewModel.selectedImageUri != null) {
            { viewModel.clear() }
        } else null
    )
}

@Composable
private fun ImagePreview(
    uri: android.net.Uri?,
    isLoading: Boolean,
    onPickImage: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<android.graphics.Bitmap?>(null) }
    
    LaunchedEffect(uri) {
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    // Load scaled down for preview
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 4
                    }
                    bitmap = BitmapFactory.decodeStream(stream, null, options)
                }
            } catch (e: Exception) {
                bitmap = null
            }
        } else {
            bitmap = null
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onPickImage),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = OmniToolTheme.colors.skyBlue)
            }
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
            else -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = OmniToolIcons.ExifViewer,
                        contentDescription = null,
                        tint = OmniToolTheme.colors.skyBlue,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to select an image",
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ExifDataDisplay(
    entries: List<ExifEntry>,
    hasGps: Boolean,
    latitude: Double?,
    longitude: Double?
) {
    val groupedEntries = entries.groupBy { it.category }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 350.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExifCategory.values().forEach { category ->
            val categoryEntries = groupedEntries[category]
            if (!categoryEntries.isNullOrEmpty()) {
                item {
                    Text(
                        text = category.displayName,
                        style = OmniToolTheme.typography.label,
                        color = OmniToolTheme.colors.skyBlue
                    )
                }
                
                items(categoryEntries) { entry ->
                    ExifRow(entry)
                }
                
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun ExifRow(entry: ExifEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = entry.label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = entry.value,
            style = OmniToolTheme.typography.body.copy(fontFamily = FontFamily.Monospace),
            color = OmniToolTheme.colors.textPrimary,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ErrorDisplay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.softCoral.copy(alpha = 0.1f))
            .padding(OmniToolTheme.spacing.md),
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
            .height(100.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Select an image to view EXIF data",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}
