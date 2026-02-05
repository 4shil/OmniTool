package com.omnitool.features.text.whitespace

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
 * Whitespace Cleaner Screen
 */
@Composable
fun WhitespaceCleanerScreen(
    onBack: () -> Unit,
    viewModel: WhitespaceCleanerViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Whitespace Cleaner",
            toolIcon = OmniToolIcons.Whitespace,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = "Paste text with messy whitespace...",
                    minLines = 4
                )
            },
            outputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    viewModel.cleaningStats?.let { stats ->
                        if (stats.charactersRemoved > 0) {
                            InfoCard(
                                message = "Cleaned: ${stats.charactersRemoved} chars removed, " +
                                    "${stats.tabsConverted} tabs converted"
                            )
                        }
                    }
                    
                    WorkspaceOutputField(
                        value = viewModel.outputText,
                        placeholder = "Cleaned text will appear here...",
                        onCopy = {
                            toastState.show("Copied to clipboard", ToastType.Success)
                        }
                    )
                }
            },
            primaryActionLabel = "Clean Whitespace",
            onPrimaryAction = { viewModel.clean() },
            processingControls = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
                    ) {
                        CleanOption(
                            label = "Trailing spaces",
                            checked = viewModel.options.removeTrailingSpaces,
                            onCheckedChange = { 
                                viewModel.updateOptions(viewModel.options.copy(removeTrailingSpaces = it))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CleanOption(
                            label = "Extra spaces",
                            checked = viewModel.options.removeExtraSpaces,
                            onCheckedChange = { 
                                viewModel.updateOptions(viewModel.options.copy(removeExtraSpaces = it))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
                    ) {
                        CleanOption(
                            label = "Tabs → Spaces",
                            checked = viewModel.options.convertTabsToSpaces,
                            onCheckedChange = { 
                                viewModel.updateOptions(viewModel.options.copy(convertTabsToSpaces = it))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CleanOption(
                            label = "Empty lines",
                            checked = viewModel.options.removeEmptyLines,
                            onCheckedChange = { 
                                viewModel.updateOptions(viewModel.options.copy(removeEmptyLines = it))
                            },
                            modifier = Modifier.weight(1f)
                        )
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
private fun CleanOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (checked) OmniToolTheme.colors.mint.copy(alpha = 0.1f)
                else OmniToolTheme.colors.elevatedSurface
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = OmniToolTheme.colors.mint,
                uncheckedColor = OmniToolTheme.colors.textMuted
            ),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = if (checked) OmniToolTheme.colors.mint else OmniToolTheme.colors.textSecondary,
            maxLines = 1
        )
    }
}
