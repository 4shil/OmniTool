package com.omnitool.features.converter.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Text Case Converter Screen
 * 
 * Features:
 * - Text input
 * - Case selector grid
 * - Result with copy
 * - Text statistics
 */
@Composable
fun TextCaseScreen(
    onBack: () -> Unit,
    viewModel: TextCaseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Text Case",
        toolIcon = OmniToolIcons.TextCase,
        accentColor = OmniToolTheme.colors.softCoral,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Input text area
                OutlinedTextField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.setInput(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Enter text to convert...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.softCoral,
                        cursorColor = OmniToolTheme.colors.softCoral
                    )
                )
                
                // Text stats
                TextStats(
                    chars = viewModel.charCount,
                    words = viewModel.wordCount,
                    lines = viewModel.lineCount
                )
                
                // Case selector grid
                CaseGrid(
                    selected = viewModel.selectedCase,
                    onSelect = { viewModel.setCase(it) }
                )
            }
        },
        outputContent = {
            ResultDisplay(
                result = viewModel.outputText,
                onCopy = { copyToClipboard(context, viewModel.outputText) }
            )
        },
        primaryActionLabel = if (viewModel.inputText.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun TextStats(
    chars: Int,
    words: Int,
    lines: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatChip("$chars chars")
        StatChip("$words words")
        StatChip("$lines lines")
    }
}

@Composable
private fun StatChip(text: String) {
    Text(
        text = text,
        style = OmniToolTheme.typography.caption,
        color = OmniToolTheme.colors.textMuted
    )
}

@Composable
private fun CaseGrid(
    selected: TextCase,
    onSelect: (TextCase) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.heightIn(max = 200.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(TextCase.values().toList()) { case ->
            val isSelected = case == selected
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.softCoral.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(case) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = case.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = if (isSelected) OmniToolTheme.colors.softCoral 
                            else OmniToolTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ResultDisplay(
    result: String,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Result",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            if (result.isNotEmpty()) {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = OmniToolIcons.Copy,
                        contentDescription = "Copy",
                        tint = OmniToolTheme.colors.softCoral
                    )
                }
            }
        }
        
        if (result.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter text above",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            Text(
                text = result,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textPrimary,
                modifier = Modifier.heightIn(max = 100.dp)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Converted text", text))
}
