package com.omnitool.features.utilities.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Color Picker Screen
 * 
 * Features:
 * - Color preview
 * - RGB sliders
 * - Hex input
 * - Format outputs
 * - Saved colors
 */
@Composable
fun ColorPickerScreen(
    onBack: () -> Unit,
    viewModel: ColorPickerViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Color Picker",
            toolIcon = OmniToolIcons.ColorPicker,
            accentColor = OmniToolTheme.colors.warmYellow,
            onBack = onBack,
            inputContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
                ) {
                    // Color preview
                    ColorPreview(
                        color = viewModel.currentColor,
                        onSave = { viewModel.saveColor() }
                    )
                    
                    // RGB sliders
                    RgbSliders(
                        red = viewModel.red,
                        green = viewModel.green,
                        blue = viewModel.blue,
                        onRedChange = { viewModel.setRed(it) },
                        onGreenChange = { viewModel.setGreen(it) },
                        onBlueChange = { viewModel.setBlue(it) }
                    )
                    
                    // Hex input
                    HexInput(
                        value = viewModel.hexInput,
                        onValueChange = { viewModel.setHexInput(it) }
                    )
                }
            },
            outputContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
                ) {
                    // Output formats
                    FormatOutputs(
                        formats = listOf(
                            "HEX" to viewModel.hexString,
                            "RGB" to viewModel.rgbString,
                            "HSL" to viewModel.hslString,
                            "Android" to viewModel.androidColor
                        ),
                        onCopy = { format, value ->
                            clipboardManager.setText(AnnotatedString(value))
                            toastState.show("$format copied", ToastType.Success)
                        }
                    )
                    
                    // Saved colors
                    if (viewModel.savedColors.isNotEmpty()) {
                        SavedColors(
                            colors = viewModel.savedColors,
                            onSelect = { viewModel.selectSavedColor(it) },
                            onRemove = { viewModel.removeSavedColor(it) }
                        )
                    }
                }
            },
            primaryActionLabel = null,
            onPrimaryAction = {}
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            OmniToolToast(
                message = toastState.message,
                type = toastState.type,
                visible = toastState.isVisible,
                onDismiss = { toastState.dismiss() }
            )
        }
    }
}

@Composable
private fun ColorPreview(
    color: Color,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color swatch
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(color)
                .border(2.dp, OmniToolTheme.colors.surface, OmniToolTheme.shapes.medium)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Current Color",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "#${String.format("%02X%02X%02X", (color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt())}",
                style = OmniToolTheme.typography.header.copy(fontFamily = FontFamily.Monospace),
                color = OmniToolTheme.colors.textPrimary
            )
        }
        
        // Save button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.warmYellow.copy(alpha = 0.2f))
                .clickable(onClick = onSave),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = OmniToolIcons.Add,
                contentDescription = "Save color",
                tint = OmniToolTheme.colors.warmYellow
            )
        }
    }
}

@Composable
private fun RgbSliders(
    red: Int,
    green: Int,
    blue: Int,
    onRedChange: (Int) -> Unit,
    onGreenChange: (Int) -> Unit,
    onBlueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        ColorSlider("R", red, Color.Red, onRedChange)
        Spacer(modifier = Modifier.height(8.dp))
        ColorSlider("G", green, Color(0xFF4CAF50), onGreenChange)
        Spacer(modifier = Modifier.height(8.dp))
        ColorSlider("B", blue, Color.Blue, onBlueChange)
    }
}

@Composable
private fun ColorSlider(
    label: String,
    value: Int,
    color: Color,
    onValueChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.label,
            color = color,
            modifier = Modifier.width(20.dp)
        )
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..255f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.2f)
            )
        )
        
        Text(
            text = value.toString(),
            style = OmniToolTheme.typography.caption.copy(fontFamily = FontFamily.Monospace),
            color = OmniToolTheme.colors.textSecondary,
            modifier = Modifier.width(32.dp)
        )
    }
}

@Composable
private fun HexInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "#",
            style = OmniToolTheme.typography.header,
            color = OmniToolTheme.colors.textMuted
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.warmYellow,
                unfocusedBorderColor = OmniToolTheme.colors.surface,
                cursorColor = OmniToolTheme.colors.warmYellow,
                focusedTextColor = OmniToolTheme.colors.textPrimary,
                unfocusedTextColor = OmniToolTheme.colors.textPrimary
            ),
            textStyle = OmniToolTheme.typography.body.copy(fontFamily = FontFamily.Monospace),
            singleLine = true,
            placeholder = { Text("RRGGBB") }
        )
    }
}

@Composable
private fun FormatOutputs(
    formats: List<Pair<String, String>>,
    onCopy: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Formats",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        formats.forEach { (name, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.surface)
                    .clickable { onCopy(name, value) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.textMuted
                    )
                    Text(
                        text = value,
                        style = OmniToolTheme.typography.body.copy(fontFamily = FontFamily.Monospace),
                        color = OmniToolTheme.colors.textPrimary
                    )
                }
                Icon(
                    imageVector = OmniToolIcons.Copy,
                    contentDescription = "Copy",
                    tint = OmniToolTheme.colors.warmYellow,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SavedColors(
    colors: List<Color>,
    onSelect: (Color) -> Unit,
    onRemove: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = "Saved Colors",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(colors) { color ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(OmniToolTheme.shapes.small)
                        .background(color)
                        .border(2.dp, OmniToolTheme.colors.surface, OmniToolTheme.shapes.small)
                        .clickable { onSelect(color) }
                )
            }
        }
    }
}
