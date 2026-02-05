package com.omnitool.features.text.json

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField
import com.omnitool.ui.screens.WorkspaceOutputField

/**
 * JSON Formatter Screen
 * 
 * Implements the Tool Workspace template for JSON formatting
 */
@Composable
fun JsonFormatterScreen(
    onBack: () -> Unit,
    viewModel: JsonFormatterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "JSON Formatter",
            toolIcon = OmniToolIcons.JsonFormatter,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = "Paste your JSON here..."
                )
            },
            outputContent = {
                Column {
                    // Error message if any
                    if (viewModel.errorMessage != null) {
                        ErrorCard(
                            title = "Invalid JSON",
                            explanation = viewModel.errorMessage!!,
                            suggestion = "Check for missing brackets, quotes, or commas"
                        )
                        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                    }
                    
                    WorkspaceOutputField(
                        value = viewModel.outputText,
                        placeholder = "Formatted JSON will appear here...",
                        onCopy = {
                            toastState.show("Copied to clipboard", ToastType.Success)
                        }
                    )
                }
            },
            primaryActionLabel = "Format",
            onPrimaryAction = { viewModel.formatJson() },
            secondaryActionLabel = "Minify",
            onSecondaryAction = { viewModel.minifyJson() },
            processingControls = {
                // Mode selector row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionChip(
                        label = "Validate",
                        onClick = { viewModel.validateJson() },
                        modifier = Modifier.weight(1f)
                    )
                    ActionChip(
                        label = "Clear",
                        onClick = { viewModel.clearAll() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )
        
        // Toast overlay
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
private fun ActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onClick)
            .padding(
                horizontal = OmniToolTheme.spacing.sm,
                vertical = OmniToolTheme.spacing.xs
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
    }
}
