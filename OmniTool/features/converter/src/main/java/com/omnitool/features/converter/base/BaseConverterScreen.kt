package com.omnitool.features.converter.base

import androidx.compose.foundation.background
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
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Number Base Converter Screen
 */
@Composable
fun BaseConverterScreen(
    onBack: () -> Unit,
    viewModel: BaseConverterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Base Converter",
            toolIcon = OmniToolIcons.BaseConverter,
            accentColor = OmniToolTheme.colors.coral,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    // Base selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)
                    ) {
                        NumberBase.values().forEach { base ->
                            BaseChip(
                                base = base,
                                selected = base == viewModel.fromBase,
                                onClick = { viewModel.setFromBase(base) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    WorkspaceInputField(
                        value = viewModel.inputValue,
                        onValueChange = { viewModel.updateInput(it) },
                        placeholder = "Enter ${viewModel.fromBase.displayName} number",
                        minLines = 1
                    )
                    
                    viewModel.errorMessage?.let { error ->
                        ErrorCard(
                            title = "Invalid Input",
                            explanation = error,
                            suggestion = "Check your number format"
                        )
                    }
                }
            },
            outputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    ResultRow(
                        label = "Binary",
                        value = viewModel.binaryResult,
                        prefix = "0b",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.binaryResult.replace(" ", "")))
                            toastState.show("Binary copied", ToastType.Success)
                        }
                    )
                    ResultRow(
                        label = "Octal",
                        value = viewModel.octalResult,
                        prefix = "0o",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.octalResult))
                            toastState.show("Octal copied", ToastType.Success)
                        }
                    )
                    ResultRow(
                        label = "Decimal",
                        value = viewModel.decimalResult,
                        prefix = "",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.decimalResult.replace(",", "")))
                            toastState.show("Decimal copied", ToastType.Success)
                        }
                    )
                    ResultRow(
                        label = "Hex",
                        value = viewModel.hexResult,
                        prefix = "0x",
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.hexResult))
                            toastState.show("Hex copied", ToastType.Success)
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
private fun BaseChip(
    base: NumberBase,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (selected) OmniToolTheme.colors.coral.copy(alpha = 0.15f)
                else OmniToolTheme.colors.surface
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = base.displayName,
            style = OmniToolTheme.typography.label,
            color = if (selected) OmniToolTheme.colors.coral else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    prefix: String,
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
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = if (value.isNotEmpty()) "$prefix$value" else "—",
            style = OmniToolTheme.typography.body,
            color = if (value.isNotEmpty()) OmniToolTheme.colors.coral else OmniToolTheme.colors.textMuted,
            modifier = Modifier.weight(1f)
        )
    }
}
