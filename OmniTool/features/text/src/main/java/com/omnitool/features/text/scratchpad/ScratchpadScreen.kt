package com.omnitool.features.text.scratchpad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Code Scratchpad Screen - Simple editor for code snippets
 */
@Composable
fun ScratchpadScreen(
    onBack: () -> Unit,
    viewModel: ScratchpadViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Scratchpad",
            toolIcon = OmniToolIcons.Scratchpad,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    // Language selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)
                    ) {
                        ScratchpadLanguage.values().take(6).forEach { lang ->
                            LanguageChip(
                                language = lang,
                                selected = lang == viewModel.language,
                                onClick = { viewModel.setLanguage(lang) }
                            )
                        }
                    }
                    
                    // Editor
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 250.dp)
                            .clip(OmniToolTheme.shapes.medium)
                            .background(OmniToolTheme.colors.elevatedSurface)
                            .padding(OmniToolTheme.spacing.sm)
                    ) {
                        if (viewModel.content.isEmpty()) {
                            Text(
                                text = "Start typing or paste code...",
                                style = OmniToolTheme.typography.body,
                                color = OmniToolTheme.colors.textMuted
                            )
                        }
                        
                        BasicTextField(
                            value = viewModel.content,
                            onValueChange = { viewModel.updateContent(it) },
                            textStyle = OmniToolTheme.typography.body.copy(
                                color = OmniToolTheme.colors.textPrimary
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            },
            outputContent = {
                // Stats bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(OmniToolTheme.shapes.small)
                        .background(OmniToolTheme.colors.elevatedSurface)
                        .padding(OmniToolTheme.spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${viewModel.lineCount} lines  •  ${viewModel.charCount} chars",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.textMuted
                    )
                    Text(
                        text = viewModel.language.displayName,
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.mint
                    )
                }
            },
            primaryActionLabel = "Save",
            onPrimaryAction = { 
                viewModel.save()
                toastState.show("Saved", ToastType.Success)
            },
            secondaryActionLabel = "Clear",
            onSecondaryAction = { viewModel.clearAll() }
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
private fun LanguageChip(
    language: ScratchpadLanguage,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (selected) OmniToolTheme.colors.mint.copy(alpha = 0.15f)
                else OmniToolTheme.colors.surface
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = language.displayName,
            style = OmniToolTheme.typography.caption,
            color = if (selected) OmniToolTheme.colors.mint else OmniToolTheme.colors.textSecondary
        )
    }
}
