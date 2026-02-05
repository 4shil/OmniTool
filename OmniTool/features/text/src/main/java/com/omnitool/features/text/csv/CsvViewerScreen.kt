package com.omnitool.features.text.csv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * CSV Viewer Screen
 */
@Composable
fun CsvViewerScreen(
    onBack: () -> Unit,
    viewModel: CsvViewerViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "CSV Viewer",
        toolIcon = OmniToolIcons.Csv,
        accentColor = OmniToolTheme.colors.mint,
        onBack = onBack,
        inputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                WorkspaceInputField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    placeholder = "Name,Age,City\nJohn,25,NYC\nJane,30,LA",
                    minLines = 4
                )
                
                // Options row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delimiter selector
                    Row(horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)) {
                        DelimiterChip(",", viewModel.delimiter == ',') { viewModel.setDelimiter(',') }
                        DelimiterChip(";", viewModel.delimiter == ';') { viewModel.setDelimiter(';') }
                        DelimiterChip("Tab", viewModel.delimiter == '\t') { viewModel.setDelimiter('\t') }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Has headers toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.setHasHeaders(!viewModel.hasHeaders) }
                    ) {
                        Checkbox(
                            checked = viewModel.hasHeaders,
                            onCheckedChange = { viewModel.setHasHeaders(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OmniToolTheme.colors.mint,
                                uncheckedColor = OmniToolTheme.colors.textMuted
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Headers",
                            style = OmniToolTheme.typography.caption,
                            color = OmniToolTheme.colors.textSecondary
                        )
                    }
                }
            }
        },
        outputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                // Stats
                if (viewModel.rowCount > 0) {
                    Text(
                        text = "${viewModel.rowCount} rows × ${viewModel.columnCount} columns",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.textMuted
                    )
                }
                
                // Error
                viewModel.errorMessage?.let { error ->
                    ErrorCard(
                        title = "Parse Error",
                        explanation = error,
                        suggestion = "Check your CSV format"
                    )
                }
                
                // Table
                if (viewModel.headers.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                            .clip(OmniToolTheme.shapes.medium)
                            .background(OmniToolTheme.colors.elevatedSurface)
                            .horizontalScroll(rememberScrollState())
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            // Header row
                            Row(
                                modifier = Modifier
                                    .background(OmniToolTheme.colors.mint.copy(alpha = 0.15f))
                            ) {
                                viewModel.headers.forEach { header ->
                                    TableCell(
                                        text = header,
                                        isHeader = true
                                    )
                                }
                            }
                            
                            // Data rows
                            viewModel.rows.forEach { row ->
                                Row {
                                    row.forEach { cell ->
                                        TableCell(text = cell)
                                    }
                                    // Pad if row has fewer columns
                                    repeat(viewModel.columnCount - row.size) {
                                        TableCell(text = "")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Table preview will appear here...",
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textMuted
                    )
                }
            }
        },
        primaryActionLabel = "Clear",
        onPrimaryAction = { viewModel.clearAll() }
    )
}

@Composable
private fun DelimiterChip(
    label: String,
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
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = if (selected) OmniToolTheme.colors.mint else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun TableCell(
    text: String,
    isHeader: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .border(0.5.dp, OmniToolTheme.colors.surface)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = if (isHeader) 
                OmniToolTheme.typography.label 
            else 
                OmniToolTheme.typography.caption,
            color = if (isHeader) 
                OmniToolTheme.colors.mint 
            else 
                OmniToolTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
