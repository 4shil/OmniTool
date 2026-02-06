package com.omnitool.features.file.resize

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Image Resize Screen
 * 
 * Features:
 * - Image picker
 * - Resize controls
 * - Preset buttons
 * - Size comparison
 */
@Composable
fun ImageResizeScreen(
    onBack: () -> Unit,
    viewModel: ImageResizeViewModel = hiltViewModel()
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.setImageUri(it) }
    }
    
    ToolWorkspaceScreen(
        toolName = "Image Resize",
        toolIcon = OmniToolIcons.ImageResize,
        accentColor = OmniToolTheme.colors.lavender,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Image preview
                ImagePreview(
                    bitmap = viewModel.originalBitmap,
                    isProcessing = viewModel.isProcessing,
                    onPickImage = { imagePicker.launch("image/*") }
                )
                
                if (viewModel.selectedImageUri != null) {
                    // Mode selector
                    ModeSelector(
                        selected = viewModel.resizeMode,
                        onSelect = { viewModel.setResizeMode(it) }
                    )
                    
                    // Resize controls based on mode
                    when (viewModel.resizeMode) {
                        ResizeMode.PERCENTAGE -> PercentageControl(
                            percent = viewModel.resizePercent,
                            onPercentChange = { viewModel.setResizePercent(it) }
                        )
                        ResizeMode.DIMENSIONS -> DimensionControls(
                            width = viewModel.targetWidth,
                            height = viewModel.targetHeight,
                            maintainRatio = viewModel.maintainAspectRatio,
                            onWidthChange = { viewModel.setTargetWidth(it) },
                            onHeightChange = { viewModel.setTargetHeight(it) },
                            onRatioToggle = { viewModel.setMaintainAspectRatio(it) }
                        )
                    }
                    
                    // Presets
                    PresetsRow(onPresetSelect = { viewModel.applyPreset(it) })
                }
            }
        },
        outputContent = {
            if (viewModel.selectedImageUri != null) {
                SizeComparison(
                    originalWidth = viewModel.originalWidth,
                    originalHeight = viewModel.originalHeight,
                    newWidth = viewModel.targetWidth,
                    newHeight = viewModel.targetHeight
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
private fun ImagePreview(
    bitmap: android.graphics.Bitmap?,
    isProcessing: Boolean,
    onPickImage: () -> Unit
) {
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
            isProcessing -> CircularProgressIndicator(color = OmniToolTheme.colors.lavender)
            bitmap != null -> Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = OmniToolIcons.ImageResize,
                    contentDescription = null,
                    tint = OmniToolTheme.colors.lavender,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to select image",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ModeSelector(
    selected: ResizeMode,
    onSelect: (ResizeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ResizeMode.values().forEach { mode ->
            val isSelected = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.lavender.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) OmniToolTheme.colors.lavender 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun PercentageControl(
    percent: Int,
    onPercentChange: (Int) -> Unit
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Scale", style = OmniToolTheme.typography.label, color = OmniToolTheme.colors.textPrimary)
            Text("$percent%", style = OmniToolTheme.typography.header, color = OmniToolTheme.colors.lavender)
        }
        
        Slider(
            value = percent.toFloat(),
            onValueChange = { onPercentChange(it.toInt()) },
            valueRange = 1f..200f,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.lavender,
                activeTrackColor = OmniToolTheme.colors.lavender
            )
        )
    }
}

@Composable
private fun DimensionControls(
    width: Int,
    height: Int,
    maintainRatio: Boolean,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    onRatioToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = width.toString(),
                onValueChange = { it.toIntOrNull()?.let(onWidthChange) },
                label = { Text("Width") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = height.toString(),
                onValueChange = { it.toIntOrNull()?.let(onHeightChange) },
                label = { Text("Height") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = maintainRatio,
                onCheckedChange = onRatioToggle,
                colors = CheckboxDefaults.colors(checkedColor = OmniToolTheme.colors.lavender)
            )
            Text(
                text = "Maintain aspect ratio",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun PresetsRow(onPresetSelect: (ResizePreset) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ResizePreset.values().toList()) { preset ->
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.surface)
                    .clickable { onPresetSelect(preset) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = preset.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun SizeComparison(
    originalWidth: Int,
    originalHeight: Int,
    newWidth: Int,
    newHeight: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Original", style = OmniToolTheme.typography.caption, color = OmniToolTheme.colors.textMuted)
            Text("${originalWidth}×${originalHeight}", style = OmniToolTheme.typography.label, color = OmniToolTheme.colors.textSecondary)
        }
        
        Text("→", style = OmniToolTheme.typography.header, color = OmniToolTheme.colors.lavender)
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("New", style = OmniToolTheme.typography.caption, color = OmniToolTheme.colors.textMuted)
            Text("${newWidth}×${newHeight}", style = OmniToolTheme.typography.header, color = OmniToolTheme.colors.primaryLime)
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Select an image to resize",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}
