package com.omnitool.features.text.caseconverter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
 * Case Converter Screen - Live conversion as you type
 */
@Composable
fun CaseConverterScreen(
    onBack: () -> Unit,
    viewModel: CaseConverterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Case Converter",
            toolIcon = OmniToolIcons.CaseConverter,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = "Type or paste text to convert...",
                    minLines = 2
                )
            },
            outputContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)
                ) {
                    if (viewModel.outputs.isEmpty()) {
                        Text(
                            text = "Converted text will appear here as you type...",
                            style = OmniToolTheme.typography.body,
                            color = OmniToolTheme.colors.textMuted
                        )
                    } else {
                        viewModel.outputs.forEach { (caseType, result) ->
                            CaseResultRow(
                                caseName = caseType.displayName,
                                result = result,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(result))
                                    toastState.show("Copied ${caseType.displayName}", ToastType.Success)
                                }
                            )
                        }
                    }
                }
            },
            primaryActionLabel = "Clear",
            onPrimaryAction = { viewModel.clearAll() },
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
private fun CaseResultRow(
    caseName: String,
    result: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onCopy)
            .padding(horizontal = OmniToolTheme.spacing.xs, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = caseName,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted,
            modifier = Modifier.width(100.dp)
        )
        
        Text(
            text = result,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        
        Icon(
            imageVector = OmniToolIcons.Copy,
            contentDescription = "Copy",
            tint = OmniToolTheme.colors.textMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
