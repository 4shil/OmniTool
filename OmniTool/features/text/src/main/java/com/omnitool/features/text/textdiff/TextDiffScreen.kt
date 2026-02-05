package com.omnitool.features.text.textdiff

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Text Diff Compare Screen
 */
@Composable
fun TextDiffScreen(
    onBack: () -> Unit,
    viewModel: TextDiffViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Text Diff",
        toolIcon = OmniToolIcons.TextDiff,
        accentColor = OmniToolTheme.colors.mint,
        onBack = onBack,
        inputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                // Text A
                Text(
                    text = "Original Text",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textSecondary
                )
                WorkspaceInputField(
                    value = viewModel.textA,
                    onValueChange = { viewModel.updateTextA(it) },
                    placeholder = "Paste original text...",
                    minLines = 3
                )
                
                // Swap button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(OmniToolTheme.shapes.small)
                            .background(OmniToolTheme.colors.elevatedSurface)
                            .clickable { viewModel.swapTexts() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
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
                                style = OmniToolTheme.typography.caption,
                                color = OmniToolTheme.colors.textSecondary
                            )
                        }
                    }
                }
                
                // Text B
                Text(
                    text = "Changed Text",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.textSecondary
                )
                WorkspaceInputField(
                    value = viewModel.textB,
                    onValueChange = { viewModel.updateTextB(it) },
                    placeholder = "Paste changed text...",
                    minLines = 3
                )
            }
        },
        outputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                // Stats
                viewModel.stats?.let { stats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
                    ) {
                        StatChip(
                            label = "+${stats.additions}",
                            color = OmniToolTheme.colors.primaryLime,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "-${stats.deletions}",
                            color = OmniToolTheme.colors.softCoral,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "${stats.similarityPercent}% similar",
                            color = OmniToolTheme.colors.skyBlue,
                            modifier = Modifier.weight(2f)
                        )
                    }
                }
                
                // Diff result
                if (viewModel.diffResult.isEmpty()) {
                    Text(
                        text = "Diff result will appear here...",
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textMuted
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .clip(OmniToolTheme.shapes.medium)
                            .background(OmniToolTheme.colors.elevatedSurface)
                            .verticalScroll(rememberScrollState())
                            .padding(OmniToolTheme.spacing.xs)
                    ) {
                        viewModel.diffResult.forEach { line ->
                            DiffLineRow(line = line)
                        }
                    }
                }
            }
        },
        primaryActionLabel = "Compare",
        onPrimaryAction = { viewModel.compare() },
        secondaryActionLabel = "Clear",
        onSecondaryAction = { viewModel.clearAll() }
    )
}

@Composable
private fun StatChip(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(OmniToolTheme.shapes.small)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.label,
            color = color
        )
    }
}

@Composable
private fun DiffLineRow(line: DiffLine) {
    val (bgColor, prefix, textColor) = when (line.type) {
        DiffType.ADDED -> Triple(
            OmniToolTheme.colors.primaryLime.copy(alpha = 0.1f),
            "+ ",
            OmniToolTheme.colors.primaryLime
        )
        DiffType.REMOVED -> Triple(
            OmniToolTheme.colors.softCoral.copy(alpha = 0.1f),
            "- ",
            OmniToolTheme.colors.softCoral
        )
        DiffType.UNCHANGED -> Triple(
            OmniToolTheme.colors.elevatedSurface,
            "  ",
            OmniToolTheme.colors.textMuted
        )
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = prefix,
            style = OmniToolTheme.typography.caption,
            color = textColor
        )
        Text(
            text = line.content,
            style = OmniToolTheme.typography.caption,
            color = if (line.type == DiffType.UNCHANGED) 
                OmniToolTheme.colors.textSecondary 
            else 
                OmniToolTheme.colors.textPrimary
        )
    }
}
