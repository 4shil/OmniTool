package com.omnitool.features.file.rename

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Batch Rename Screen
 * 
 * Features:
 * - File picker
 * - Rename mode selector
 * - Live preview
 */
@Composable
fun BatchRenameScreen(
    onBack: () -> Unit,
    viewModel: BatchRenameViewModel = hiltViewModel()
) {
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addFiles(uris)
    }
    
    ToolWorkspaceScreen(
        toolName = "Batch Rename",
        toolIcon = OmniToolIcons.Rename,
        accentColor = OmniToolTheme.colors.accentOrange,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Mode selector
                ModeSelector(
                    selected = viewModel.renameMode,
                    onSelect = { viewModel.setMode(it) }
                )
                
                // Mode-specific options
                when (viewModel.renameMode) {
                    RenameMode.FIND_REPLACE -> FindReplaceOptions(
                        findText = viewModel.findText,
                        replaceText = viewModel.replaceText,
                        onFindChange = { viewModel.setFindText(it) },
                        onReplaceChange = { viewModel.setReplaceText(it) }
                    )
                    RenameMode.PREFIX_SUFFIX -> PrefixSuffixOptions(
                        prefix = viewModel.prefix,
                        suffix = viewModel.suffix,
                        onPrefixChange = { viewModel.setPrefix(it) },
                        onSuffixChange = { viewModel.setSuffix(it) }
                    )
                    RenameMode.NUMBERING -> NumberingOptions(
                        startNumber = viewModel.startNumber,
                        padding = viewModel.numberPadding,
                        onStartChange = { viewModel.setStartNumber(it) },
                        onPaddingChange = { viewModel.setPadding(it) }
                    )
                }
                
                // Case options
                CaseSelector(
                    selected = viewModel.caseMode,
                    onSelect = { viewModel.setCaseMode(it) }
                )
            }
        },
        outputContent = {
            RenamePreviewList(
                previews = viewModel.previewFiles,
                onPickFiles = { filePicker.launch("*/*") },
                onClear = { viewModel.clearFiles() }
            )
        },
        primaryActionLabel = if (viewModel.selectedFiles.isEmpty()) "Add Files" else null,
        onPrimaryAction = { filePicker.launch("*/*") }
    )
}

@Composable
private fun ModeSelector(
    selected: RenameMode,
    onSelect: (RenameMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RenameMode.values().forEach { mode ->
            val isSelected = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.accentOrange.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(mode) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = if (isSelected) OmniToolTheme.colors.accentOrange 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun FindReplaceOptions(
    findText: String,
    replaceText: String,
    onFindChange: (String) -> Unit,
    onReplaceChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = findText,
            onValueChange = onFindChange,
            label = { Text("Find") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.accentOrange,
                cursorColor = OmniToolTheme.colors.accentOrange
            )
        )
        OutlinedTextField(
            value = replaceText,
            onValueChange = onReplaceChange,
            label = { Text("Replace with") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.accentOrange,
                cursorColor = OmniToolTheme.colors.accentOrange
            )
        )
    }
}

@Composable
private fun PrefixSuffixOptions(
    prefix: String,
    suffix: String,
    onPrefixChange: (String) -> Unit,
    onSuffixChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = prefix,
            onValueChange = onPrefixChange,
            label = { Text("Prefix") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.accentOrange
            )
        )
        OutlinedTextField(
            value = suffix,
            onValueChange = onSuffixChange,
            label = { Text("Suffix") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.accentOrange
            )
        )
    }
}

@Composable
private fun NumberingOptions(
    startNumber: Int,
    padding: Int,
    onStartChange: (Int) -> Unit,
    onPaddingChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = startNumber.toString(),
            onValueChange = { it.toIntOrNull()?.let(onStartChange) },
            label = { Text("Start #") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.accentOrange
            )
        )
        OutlinedTextField(
            value = padding.toString(),
            onValueChange = { it.toIntOrNull()?.let(onPaddingChange) },
            label = { Text("Digits") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.accentOrange
            )
        )
    }
}

@Composable
private fun CaseSelector(
    selected: CaseMode,
    onSelect: (CaseMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CaseMode.values().forEach { mode ->
            val isSelected = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.skyBlue.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(mode) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = if (isSelected) OmniToolTheme.colors.skyBlue 
                            else OmniToolTheme.colors.textMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RenamePreviewList(
    previews: List<RenamePreview>,
    onPickFiles: () -> Unit,
    onClear: () -> Unit
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
                text = "Preview (${previews.size} files)",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Row {
                TextButton(onClick = onPickFiles) {
                    Text("Add", color = OmniToolTheme.colors.accentOrange)
                }
                if (previews.isNotEmpty()) {
                    TextButton(onClick = onClear) {
                        Text("Clear", color = OmniToolTheme.colors.softCoral)
                    }
                }
            }
        }
        
        if (previews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add files to see preview",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(previews) { preview ->
                    PreviewItem(preview)
                }
            }
        }
    }
}

@Composable
private fun PreviewItem(preview: RenamePreview) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.surface)
            .padding(8.dp)
    ) {
        Text(
            text = preview.original,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted,
            textDecoration = if (preview.hasChange) TextDecoration.LineThrough else null,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (preview.hasChange) {
            Text(
                text = "→ ${preview.newName}",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.primaryLime,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
