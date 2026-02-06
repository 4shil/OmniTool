package com.omnitool.features.security.random

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Random Number Generator Screen
 * 
 * Features:
 * - Min/Max range input
 * - Generate count slider
 * - Unique/duplicate toggle
 * - Copy all results
 */
@Composable
fun RandomNumberScreen(
    onBack: () -> Unit,
    viewModel: RandomNumberViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    ToolWorkspaceScreen(
        toolName = "Random Number",
        toolIcon = OmniToolIcons.RandomNumber,
        accentColor = OmniToolTheme.colors.softCoral,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Range inputs
                RangeInputs(
                    minValue = viewModel.minValue,
                    maxValue = viewModel.maxValue,
                    onMinChange = { viewModel.updateMinValue(it) },
                    onMaxChange = { viewModel.updateMaxValue(it) }
                )
                
                // Count slider
                CountSlider(
                    count = viewModel.count,
                    onCountChange = { viewModel.updateCount(it) }
                )
                
                // Duplicates toggle
                DuplicatesToggle(
                    allowDuplicates = viewModel.allowDuplicates,
                    onToggle = { viewModel.toggleDuplicates() }
                )
            }
        },
        outputContent = {
            Column {
                if (viewModel.errorMessage != null) {
                    ErrorCard(
                        title = "Error",
                        explanation = viewModel.errorMessage!!,
                        suggestion = "Adjust the range or count"
                    )
                    Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                }
                
                ResultsDisplay(
                    numbers = viewModel.generatedNumbers,
                    onCopyAll = {
                        if (viewModel.generatedNumbers.isNotEmpty()) {
                            val text = viewModel.generatedNumbers.joinToString("\n")
                            clipboardManager.setText(AnnotatedString(text))
                            toastState.show("Copied to clipboard", ToastType.Success)
                        }
                    }
                )
            }
        },
        primaryActionLabel = "Generate",
        onPrimaryAction = { viewModel.generate() },
        secondaryActionLabel = if (viewModel.generatedNumbers.isNotEmpty()) "Clear" else null,
        onSecondaryAction = if (viewModel.generatedNumbers.isNotEmpty()) {
            { viewModel.clearResults() }
        } else null
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        OmniToolToast(
            message = toastState.message,
            type = toastState.type,
            visible = toastState.isVisible,
            onDismiss = { toastState.dismiss() }
        )
    }
}

@Composable
private fun RangeInputs(
    minValue: String,
    maxValue: String,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = "Range",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = minValue,
                onValueChange = onMinChange,
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OmniToolTheme.colors.softCoral,
                    unfocusedBorderColor = OmniToolTheme.colors.surface,
                    focusedLabelColor = OmniToolTheme.colors.softCoral,
                    unfocusedLabelColor = OmniToolTheme.colors.textMuted,
                    cursorColor = OmniToolTheme.colors.softCoral,
                    focusedTextColor = OmniToolTheme.colors.textPrimary,
                    unfocusedTextColor = OmniToolTheme.colors.textPrimary
                ),
                singleLine = true
            )
            
            Text(
                text = "to",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted
            )
            
            OutlinedTextField(
                value = maxValue,
                onValueChange = onMaxChange,
                label = { Text("Max") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OmniToolTheme.colors.softCoral,
                    unfocusedBorderColor = OmniToolTheme.colors.surface,
                    focusedLabelColor = OmniToolTheme.colors.softCoral,
                    unfocusedLabelColor = OmniToolTheme.colors.textMuted,
                    cursorColor = OmniToolTheme.colors.softCoral,
                    focusedTextColor = OmniToolTheme.colors.textPrimary,
                    unfocusedTextColor = OmniToolTheme.colors.textPrimary
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun CountSlider(
    count: Int,
    onCountChange: (Int) -> Unit
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
                text = "How many numbers?",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = "$count",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.softCoral
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = count.toFloat(),
            onValueChange = { onCountChange(it.toInt()) },
            valueRange = 1f..100f,
            steps = 98,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.softCoral,
                activeTrackColor = OmniToolTheme.colors.softCoral,
                inactiveTrackColor = OmniToolTheme.colors.surface
            )
        )
    }
}

@Composable
private fun DuplicatesToggle(
    allowDuplicates: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onToggle)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Allow duplicates",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = if (allowDuplicates) "Same number can appear multiple times" 
                       else "All numbers will be unique",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
        Switch(
            checked = allowDuplicates,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = OmniToolTheme.colors.softCoral,
                checkedTrackColor = OmniToolTheme.colors.softCoral.copy(alpha = 0.3f),
                uncheckedThumbColor = OmniToolTheme.colors.textMuted,
                uncheckedTrackColor = OmniToolTheme.colors.surface
            )
        )
    }
}

@Composable
private fun ResultsDisplay(
    numbers: List<Long>,
    onCopyAll: () -> Unit
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
                text = "Results",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            
            if (numbers.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clip(OmniToolTheme.shapes.small)
                        .clickable(onClick = onCopyAll)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = OmniToolIcons.Copy,
                        contentDescription = "Copy",
                        tint = OmniToolTheme.colors.softCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Copy All",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.softCoral
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 200.dp)
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.surface)
                .padding(OmniToolTheme.spacing.sm)
        ) {
            if (numbers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tap Generate to create random numbers",
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(numbers) { number ->
                        Text(
                            text = number.toString(),
                            style = OmniToolTheme.typography.body.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = OmniToolTheme.colors.textPrimary
                        )
                    }
                }
            }
        }
        
        if (numbers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${numbers.size} number${if (numbers.size > 1) "s" else ""} generated",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}
