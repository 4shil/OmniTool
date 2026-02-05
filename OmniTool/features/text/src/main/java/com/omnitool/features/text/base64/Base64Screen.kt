package com.omnitool.features.text.base64

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
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
 * Base64 Encode/Decode Screen
 */
@Composable
fun Base64Screen(
    onBack: () -> Unit,
    viewModel: Base64ViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Base64 ${if (viewModel.mode == Base64Mode.ENCODE) "Encode" else "Decode"}",
            toolIcon = OmniToolIcons.Base64,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = if (viewModel.mode == Base64Mode.ENCODE) 
                        "Enter text to encode..." 
                    else 
                        "Enter Base64 to decode..."
                )
            },
            outputContent = {
                Column {
                    if (viewModel.errorMessage != null) {
                        ErrorCard(
                            title = "Error",
                            explanation = viewModel.errorMessage!!,
                            suggestion = if (viewModel.mode == Base64Mode.DECODE) 
                                "Check that the input is valid Base64" 
                            else null
                        )
                        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                    }
                    
                    WorkspaceOutputField(
                        value = viewModel.outputText,
                        placeholder = if (viewModel.mode == Base64Mode.ENCODE) 
                            "Encoded text will appear here..." 
                        else 
                            "Decoded text will appear here...",
                        onCopy = {
                            toastState.show("Copied to clipboard", ToastType.Success)
                        }
                    )
                }
            },
            primaryActionLabel = if (viewModel.mode == Base64Mode.ENCODE) "Encode" else "Decode",
            onPrimaryAction = { viewModel.process() },
            processingControls = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Mode toggle
                    ModeToggle(
                        currentMode = viewModel.mode,
                        onModeChange = { viewModel.setMode(it) },
                        modifier = Modifier.weight(2f)
                    )
                    
                    // Swap button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(OmniToolTheme.shapes.medium)
                            .background(OmniToolTheme.colors.elevatedSurface)
                            .clickable(enabled = viewModel.outputText.isNotEmpty()) { 
                                viewModel.swapInputOutput() 
                            }
                            .padding(OmniToolTheme.spacing.xs),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = OmniToolIcons.Refresh,
                                contentDescription = "Swap",
                                tint = OmniToolTheme.colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Swap",
                                style = OmniToolTheme.typography.label,
                                color = OmniToolTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
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
private fun ModeToggle(
    currentMode: Base64Mode,
    onModeChange: (Base64Mode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
    ) {
        Base64Mode.values().forEach { mode ->
            val isSelected = mode == currentMode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) OmniToolTheme.colors.mint.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.elevatedSurface
                    )
                    .clickable { onModeChange(mode) }
                    .padding(OmniToolTheme.spacing.xs),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.name,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) 
                        OmniToolTheme.colors.mint 
                    else 
                        OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}
