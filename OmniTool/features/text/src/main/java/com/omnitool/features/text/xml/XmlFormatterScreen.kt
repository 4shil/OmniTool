package com.omnitool.features.text.xml

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * XML Formatter Screen
 */
@Composable
fun XmlFormatterScreen(
    onBack: () -> Unit,
    viewModel: XmlFormatterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "XML Formatter",
            toolIcon = OmniToolIcons.XmlFormatter,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = "Paste your XML here..."
                )
            },
            outputContent = {
                Column {
                    if (viewModel.errorMessage != null) {
                        ErrorCard(
                            title = "Invalid XML",
                            explanation = viewModel.errorMessage!!,
                            suggestion = "Check for missing or mismatched tags"
                        )
                        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                    }
                    
                    WorkspaceOutputField(
                        value = viewModel.outputText,
                        placeholder = "Formatted XML will appear here...",
                        onCopy = {
                            toastState.show("Copied to clipboard", ToastType.Success)
                        }
                    )
                }
            },
            primaryActionLabel = "Format",
            onPrimaryAction = { viewModel.formatXml() },
            secondaryActionLabel = "Minify",
            onSecondaryAction = { viewModel.minifyXml() }
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
