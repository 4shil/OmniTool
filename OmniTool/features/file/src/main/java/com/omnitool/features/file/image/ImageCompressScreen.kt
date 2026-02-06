package com.omnitool.features.file.image

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Image Compression Screen
 * 
 * Features:
 * - Image picker
 * - Quality slider
 * - Format selector
 * - Size comparison
 */
@Composable
fun ImageCompressScreen(
    onBack: () -> Unit,
    viewModel: ImageCompressViewModel = hiltViewModel()
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setImageUri(it) }
    }
    
    ToolWorkspaceScreen(
        toolName = "Image Compress",
        toolIcon = OmniToolIcons.ImageCompress,
        accentColor = OmniToolTheme.colors.skyBlue,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Image preview or picker
                ImagePickerArea(
                    bitmap = viewModel.originalBitmap,
                    isProcessing = viewModel.isProcessing,
                    onPickImage = { imagePicker.launch("image/*") }
                )
                
                // Quality slider
                if (viewModel.selectedImageUri != null) {
                    QualitySlider(
                        quality = viewModel.quality,
                        onQualityChange = { viewModel.setQuality(it) },
                        enabled = viewModel.outputFormat != ImageFormat.PNG
                    )
                    
                    // Format selector
                    FormatSelector(
                        selected = viewModel.outputFormat,
                        onSelect = { viewModel.setFormat(it) }
                    )
                }
            }
        },
        outputContent = {
            if (viewModel.selectedImageUri != null) {
                CompressionResults(
                    originalSize = viewModel.originalSize,
                    compressedSize = viewModel.compressedSize,
                    ratio = viewModel.compressionRatio,
                    formatSize = { viewModel.formatSize(it) },
                    isProcessing = viewModel.isProcessing
                )
            } else {
                EmptyState()
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
private fun ImagePickerArea(
    bitmap: android.graphics.Bitmap?,
    isProcessing: Boolean,
    onPickImage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onPickImage),
        contentAlignment = Alignment.Center
    ) {
        when {
            isProcessing -> {
                CircularProgressIndicator(
                    color = OmniToolTheme.colors.skyBlue
                )
            }
            bitmap != null -> {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = OmniToolIcons.ImageCompress,
                        contentDescription = null,
                        tint = OmniToolTheme.colors.skyBlue,
                        modifier = Modifier.size(48.dp)
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
private fun QualitySlider(
    quality: Int,
    onQualityChange: (Int) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quality",
                style = OmniToolTheme.typography.label,
                color = if (enabled) OmniToolTheme.colors.textPrimary 
                        else OmniToolTheme.colors.textMuted
            )
            Text(
                text = "$quality%",
                style = OmniToolTheme.typography.header,
                color = if (enabled) OmniToolTheme.colors.skyBlue 
                        else OmniToolTheme.colors.textMuted
            )
        }
        
        if (!enabled) {
            Text(
                text = "PNG uses lossless compression",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
        
        Slider(
            value = quality.toFloat(),
            onValueChange = { onQualityChange(it.toInt()) },
            valueRange = 1f..100f,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.skyBlue,
                activeTrackColor = OmniToolTheme.colors.skyBlue,
                inactiveTrackColor = OmniToolTheme.colors.surface,
                disabledThumbColor = OmniToolTheme.colors.textMuted,
                disabledActiveTrackColor = OmniToolTheme.colors.textMuted
            )
        )
    }
}

@Composable
private fun FormatSelector(
    selected: ImageFormat,
    onSelect: (ImageFormat) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ImageFormat.values().forEach { format ->
            val isSelected = format == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.skyBlue.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(format) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = format.displayName,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) OmniToolTheme.colors.skyBlue 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun CompressionResults(
    originalSize: Long,
    compressedSize: Long,
    ratio: Float,
    formatSize: (Long) -> String,
    isProcessing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Compression Results",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = OmniToolTheme.colors.skyBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SizeColumn(
                    label = "Original",
                    size = formatSize(originalSize),
                    color = OmniToolTheme.colors.textSecondary
                )
                
                Text(
                    text = "→",
                    style = OmniToolTheme.typography.header,
                    color = OmniToolTheme.colors.skyBlue
                )
                
                SizeColumn(
                    label = "Compressed",
                    size = formatSize(compressedSize),
                    color = OmniToolTheme.colors.primaryLime
                )
            }
            
            Divider(color = OmniToolTheme.colors.surface)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved: ",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textSecondary
                )
                Text(
                    text = "${String.format("%.1f", ratio)}%",
                    style = OmniToolTheme.typography.header.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (ratio > 0) OmniToolTheme.colors.primaryLime 
                            else OmniToolTheme.colors.softCoral
                )
            }
        }
    }
}

@Composable
private fun SizeColumn(
    label: String,
    size: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Text(
            text = size,
            style = OmniToolTheme.typography.header.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = color
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Select an image to compress",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}
