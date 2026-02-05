package com.omnitool.features.converter.color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.animation.pressScale
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Color Converter Screen
 */
@Composable
fun ColorConverterScreen(
    onBack: () -> Unit,
    viewModel: ColorConverterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Color Converter",
            toolIcon = OmniToolIcons.ColorPicker,
            accentColor = OmniToolTheme.colors.coral,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                    // Color preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(OmniToolTheme.shapes.medium)
                            .background(viewModel.previewColor)
                            .border(
                                width = 2.dp,
                                color = OmniToolTheme.colors.surface,
                                shape = OmniToolTheme.shapes.medium
                            )
                    )
                    
                    // HEX input
                    WorkspaceInputField(
                        value = viewModel.hexInput,
                        onValueChange = { viewModel.updateHex(it) },
                        placeholder = "#FF5733",
                        minLines = 1
                    )
                    
                    viewModel.errorMessage?.let { error ->
                        Text(
                            text = error,
                            style = OmniToolTheme.typography.caption,
                            color = OmniToolTheme.colors.error
                        )
                    }
                }
            },
            outputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    ColorFormatRow(
                        label = "HEX",
                        value = viewModel.hexInput,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.hexInput))
                            toastState.show("HEX copied", ToastType.Success)
                        }
                    )
                    ColorFormatRow(
                        label = "RGB",
                        value = viewModel.rgbResult,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.rgbResult))
                            toastState.show("RGB copied", ToastType.Success)
                        }
                    )
                    ColorFormatRow(
                        label = "HSL",
                        value = viewModel.hslResult,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.hslResult))
                            toastState.show("HSL copied", ToastType.Success)
                        }
                    )
                    ColorFormatRow(
                        label = "HSV",
                        value = viewModel.hsvResult,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.hsvResult))
                            toastState.show("HSV copied", ToastType.Success)
                        }
                    )
                    ColorFormatRow(
                        label = "CMYK",
                        value = viewModel.cmykResult,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.cmykResult))
                            toastState.show("CMYK copied", ToastType.Success)
                        }
                    )
                }
            },
            primaryActionLabel = "Clear",
            onPrimaryAction = { viewModel.clearAll() }
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
private fun ColorFormatRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.elevatedSurface)
            .pressScale()
            .clickable(onClick = onCopy)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textMuted,
            modifier = Modifier.width(50.dp)
        )
        Text(
            text = value.ifEmpty { "—" },
            style = OmniToolTheme.typography.body,
            color = if (value.isNotEmpty()) OmniToolTheme.colors.coral else OmniToolTheme.colors.textMuted
        )
    }
}
