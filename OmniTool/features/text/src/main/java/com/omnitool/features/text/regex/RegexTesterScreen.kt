package com.omnitool.features.text.regex

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.components.SuccessCard
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Regex Tester Screen - Live regex testing
 */
@Composable
fun RegexTesterScreen(
    onBack: () -> Unit,
    viewModel: RegexTesterViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Regex Tester",
        toolIcon = OmniToolIcons.Regex,
        accentColor = OmniToolTheme.colors.mint,
        onBack = onBack,
        inputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                // Pattern input
                Text(
                    text = "Pattern",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textSecondary
                )
                WorkspaceInputField(
                    value = viewModel.pattern,
                    onValueChange = { viewModel.updatePattern(it) },
                    placeholder = "Enter regex pattern...",
                    minLines = 1
                )
                
                // Flags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
                ) {
                    FlagChip(
                        label = "Case insensitive",
                        shortLabel = "i",
                        checked = viewModel.flags.caseInsensitive,
                        onCheckedChange = {
                            viewModel.updateFlags(viewModel.flags.copy(caseInsensitive = it))
                        }
                    )
                    FlagChip(
                        label = "Multiline",
                        shortLabel = "m",
                        checked = viewModel.flags.multiline,
                        onCheckedChange = {
                            viewModel.updateFlags(viewModel.flags.copy(multiline = it))
                        }
                    )
                    FlagChip(
                        label = "Dot all",
                        shortLabel = "s",
                        checked = viewModel.flags.dotAll,
                        onCheckedChange = {
                            viewModel.updateFlags(viewModel.flags.copy(dotAll = it))
                        }
                    )
                }
                
                // Test string input
                Text(
                    text = "Test String",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textSecondary
                )
                WorkspaceInputField(
                    value = viewModel.testString,
                    onValueChange = { viewModel.updateTestString(it) },
                    placeholder = "Enter text to test against...",
                    minLines = 3
                )
            }
        },
        outputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                // Error message
                if (viewModel.errorMessage != null) {
                    ErrorCard(
                        title = "Pattern Error",
                        explanation = viewModel.errorMessage!!,
                        suggestion = "Check your regex syntax"
                    )
                }
                
                // Match status
                viewModel.isMatch?.let { matched ->
                    if (matched) {
                        SuccessCard(
                            title = "Match Found!",
                            message = "${viewModel.matches.size} match${if (viewModel.matches.size > 1) "es" else ""} found"
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(OmniToolTheme.shapes.medium)
                                .background(OmniToolTheme.colors.elevatedSurface)
                                .padding(OmniToolTheme.spacing.sm)
                        ) {
                            Text(
                                text = "No matches found",
                                style = OmniToolTheme.typography.body,
                                color = OmniToolTheme.colors.textMuted
                            )
                        }
                    }
                }
                
                // Matches list
                if (viewModel.matches.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xxs))
                    Text(
                        text = "Matches",
                        style = OmniToolTheme.typography.label,
                        color = OmniToolTheme.colors.textSecondary
                    )
                    viewModel.matches.forEachIndexed { index, match ->
                        MatchRow(index = index, match = match)
                    }
                }
            }
        },
        primaryActionLabel = "Clear",
        onPrimaryAction = { viewModel.clearAll() }
    )
}

@Composable
private fun FlagChip(
    label: String,
    shortLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (checked) OmniToolTheme.colors.mint.copy(alpha = 0.15f)
                else OmniToolTheme.colors.elevatedSurface
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "/$shortLabel",
            style = OmniToolTheme.typography.caption,
            color = if (checked) OmniToolTheme.colors.mint else OmniToolTheme.colors.textMuted
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = if (checked) OmniToolTheme.colors.textPrimary else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun MatchRow(
    index: Int,
    match: MatchResult
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(horizontal = OmniToolTheme.spacing.xs, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#${index + 1}",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.mint,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = "\"${match.value}\"",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "[${match.start}-${match.end}]",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
    }
}
