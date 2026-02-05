package com.omnitool.features.text.duplicateremover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.InfoCard
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField
import com.omnitool.ui.screens.WorkspaceOutputField

/**
 * Duplicate Remover Screen
 */
@Composable
fun DuplicateRemoverScreen(
    onBack: () -> Unit,
    viewModel: DuplicateRemoverViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Remove Duplicates",
            toolIcon = OmniToolIcons.DuplicateRemover,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = "Paste text with duplicate lines...",
                    minLines = 4
                )
            },
            outputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    if (viewModel.removedCount > 0) {
                        InfoCard(
                            message = "Removed ${viewModel.removedCount} duplicate line${if (viewModel.removedCount > 1) "s" else ""}"
                        )
                    }
                    
                    WorkspaceOutputField(
                        value = viewModel.outputText,
                        placeholder = "Unique lines will appear here...",
                        onCopy = {
                            toastState.show("Copied to clipboard", ToastType.Success)
                        }
                    )
                }
            },
            primaryActionLabel = "Remove Duplicates",
            onPrimaryAction = { viewModel.removeDuplicates() },
            processingControls = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
                ) {
                    OptionCheckbox(
                        label = "Case sensitive",
                        checked = viewModel.caseSensitive,
                        onCheckedChange = { viewModel.setCaseSensitive(it) },
                        modifier = Modifier.weight(1f)
                    )
                    OptionCheckbox(
                        label = "Trim whitespace",
                        checked = viewModel.trimWhitespace,
                        onCheckedChange = { viewModel.setTrimWhitespace(it) },
                        modifier = Modifier.weight(1f)
                    )
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
private fun OptionCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = OmniToolTheme.spacing.xs, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = OmniToolTheme.colors.mint,
                uncheckedColor = OmniToolTheme.colors.textMuted
            ),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textSecondary
        )
    }
}
