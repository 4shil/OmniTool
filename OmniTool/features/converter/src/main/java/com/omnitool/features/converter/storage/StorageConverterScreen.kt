package com.omnitool.features.converter.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
 * Storage/Data Size Converter Screen
 */
@Composable
fun StorageConverterScreen(
    onBack: () -> Unit,
    viewModel: StorageConverterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Data Size Converter",
            toolIcon = OmniToolIcons.StorageConverter,
            accentColor = OmniToolTheme.colors.coral,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    // Unit selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)
                    ) {
                        StorageUnit.values().forEach { unit ->
                            UnitChip(
                                unit = unit,
                                selected = unit == viewModel.fromUnit,
                                onClick = { viewModel.setFromUnit(unit) }
                            )
                        }
                    }
                    
                    WorkspaceInputField(
                        value = viewModel.inputValue,
                        onValueChange = { viewModel.updateInput(it) },
                        placeholder = "Enter ${viewModel.fromUnit.displayName}",
                        minLines = 1
                    )
                }
            },
            outputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)) {
                    StorageUnit.values().forEach { unit ->
                        val result = viewModel.results[unit] ?: ""
                        ResultRow(
                            unit = unit,
                            value = result,
                            isSource = unit == viewModel.fromUnit,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(result))
                                toastState.show("${unit.symbol} copied", ToastType.Success)
                            }
                        )
                    }
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
private fun UnitChip(
    unit: StorageUnit,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (selected) OmniToolTheme.colors.coral.copy(alpha = 0.15f)
                else OmniToolTheme.colors.surface
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = unit.symbol,
            style = OmniToolTheme.typography.label,
            color = if (selected) OmniToolTheme.colors.coral else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun ResultRow(
    unit: StorageUnit,
    value: String,
    isSource: Boolean,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (isSource) OmniToolTheme.colors.coral.copy(alpha = 0.08f)
                else OmniToolTheme.colors.elevatedSurface
            )
            .pressScale()
            .clickable(onClick = onCopy)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${unit.displayName} (${unit.symbol})",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textSecondary,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value.ifEmpty { "—" },
            style = OmniToolTheme.typography.body,
            color = if (value.isNotEmpty()) OmniToolTheme.colors.textPrimary else OmniToolTheme.colors.textMuted,
            modifier = Modifier.weight(0.55f)
        )
    }
}
