package com.omnitool.features.text.wordcount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
 * Word/Character Counter Screen - Live stats as you type
 */
@Composable
fun WordCountScreen(
    onBack: () -> Unit,
    viewModel: WordCountViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Word Counter",
        toolIcon = OmniToolIcons.WordCount,
        accentColor = OmniToolTheme.colors.mint,
        onBack = onBack,
        inputContent = {
            WorkspaceInputField(
                value = viewModel.inputText,
                onValueChange = { viewModel.updateInput(it) },
                placeholder = "Type or paste text to count...",
                minLines = 6
            )
        },
        outputContent = {
            StatsGrid(stats = viewModel.stats)
        },
        primaryActionLabel = "Clear",
        onPrimaryAction = { viewModel.clearAll() },
        processingControls = null
    )
}

@Composable
private fun StatsGrid(stats: TextStats) {
    Column(
        verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
    ) {
        // Main counts row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
        ) {
            StatCard(
                label = "Words",
                value = stats.words.toString(),
                modifier = Modifier.weight(1f),
                isPrimary = true
            )
            StatCard(
                label = "Characters",
                value = stats.characters.toString(),
                modifier = Modifier.weight(1f),
                isPrimary = true
            )
        }
        
        // Secondary counts row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
        ) {
            StatCard(
                label = "No Spaces",
                value = stats.charactersNoSpaces.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Sentences",
                value = stats.sentences.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Lines",
                value = stats.lines.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Time estimates row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
        ) {
            StatCard(
                label = "Reading Time",
                value = stats.formatReadingTime(),
                modifier = Modifier.weight(1f),
                icon = "📖"
            )
            StatCard(
                label = "Speaking Time",
                value = stats.formatSpeakingTime(),
                modifier = Modifier.weight(1f),
                icon = "🎤"
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    icon: String? = null
) {
    Box(
        modifier = modifier
            .clip(OmniToolTheme.shapes.medium)
            .background(
                if (isPrimary) 
                    OmniToolTheme.colors.mint.copy(alpha = 0.1f) 
                else 
                    OmniToolTheme.colors.elevatedSurface
            )
            .padding(OmniToolTheme.spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (icon != null) {
                    Text(text = icon)
                }
                Text(
                    text = value,
                    style = if (isPrimary) 
                        OmniToolTheme.typography.titleL 
                    else 
                        OmniToolTheme.typography.header,
                    color = if (isPrimary) 
                        OmniToolTheme.colors.mint 
                    else 
                        OmniToolTheme.colors.textPrimary
                )
            }
            Text(
                text = label,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}
