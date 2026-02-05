package com.omnitool.features.text.hash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Hash Generator Screen
 */
@Composable
fun HashGeneratorScreen(
    onBack: () -> Unit,
    viewModel: HashGeneratorViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Hash Generator",
            toolIcon = OmniToolIcons.Hash,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = "Enter text to hash...",
                    minLines = 3
                )
            },
            outputContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
                ) {
                    if (viewModel.hashResults.isEmpty()) {
                        Text(
                            text = "Hash outputs will appear here...",
                            style = OmniToolTheme.typography.body,
                            color = OmniToolTheme.colors.textMuted
                        )
                    } else {
                        viewModel.hashResults.forEach { (algo, hash) ->
                            HashResultRow(
                                algorithmName = algo.displayName,
                                hash = hash,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(hash))
                                    toastState.show("${algo.displayName} copied", ToastType.Success)
                                }
                            )
                        }
                    }
                }
            },
            primaryActionLabel = "Generate All Hashes",
            onPrimaryAction = { viewModel.generateAllHashes() },
            processingControls = null
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
private fun HashResultRow(
    algorithmName: String,
    hash: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Algorithm name
        Text(
            text = algorithmName,
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.mint,
            modifier = Modifier.width(70.dp)
        )
        
        // Hash value
        Text(
            text = hash,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 2
        )
        
        // Copy button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(OmniToolTheme.shapes.small)
                .clickable(onClick = onCopy),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = OmniToolIcons.Copy,
                contentDescription = "Copy",
                tint = OmniToolTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
