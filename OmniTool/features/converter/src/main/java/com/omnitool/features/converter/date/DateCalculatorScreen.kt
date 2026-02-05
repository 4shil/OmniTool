package com.omnitool.features.converter.date

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Date Calculator Screen
 */
@Composable
fun DateCalculatorScreen(
    onBack: () -> Unit,
    viewModel: DateCalculatorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Date Calculator",
        toolIcon = OmniToolIcons.DateCalc,
        accentColor = OmniToolTheme.colors.coral,
        onBack = onBack,
        inputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                // Mode selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)
                ) {
                    DateCalcMode.values().forEach { mode ->
                        ModeChip(
                            mode = mode,
                            selected = mode == viewModel.calculationMode,
                            onClick = { viewModel.setMode(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Date pickers
                when (viewModel.calculationMode) {
                    DateCalcMode.DIFFERENCE -> {
                        DatePickerButton(
                            label = "Start Date",
                            date = viewModel.getFormattedStartDate(),
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d -> viewModel.setStartDate(y, m, d) },
                                    viewModel.startDate.get(java.util.Calendar.YEAR),
                                    viewModel.startDate.get(java.util.Calendar.MONTH),
                                    viewModel.startDate.get(java.util.Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                        DatePickerButton(
                            label = "End Date",
                            date = viewModel.getFormattedEndDate(),
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d -> viewModel.setEndDate(y, m, d) },
                                    viewModel.endDate.get(java.util.Calendar.YEAR),
                                    viewModel.endDate.get(java.util.Calendar.MONTH),
                                    viewModel.endDate.get(java.util.Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                    }
                    else -> {
                        DatePickerButton(
                            label = "Start Date",
                            date = viewModel.getFormattedStartDate(),
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d -> viewModel.setStartDate(y, m, d) },
                                    viewModel.startDate.get(java.util.Calendar.YEAR),
                                    viewModel.startDate.get(java.util.Calendar.MONTH),
                                    viewModel.startDate.get(java.util.Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                        WorkspaceInputField(
                            value = viewModel.daysToAdd,
                            onValueChange = { viewModel.setDaysToAdd(it) },
                            placeholder = "Enter number of days",
                            minLines = 1
                        )
                    }
                }
            }
        },
        outputContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
            ) {
                Text(
                    text = viewModel.result,
                    style = OmniToolTheme.typography.headingLarge.copy(fontSize = 32.sp),
                    color = OmniToolTheme.colors.coral,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = viewModel.detailedResult,
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted,
                    textAlign = TextAlign.Center
                )
            }
        },
        primaryActionLabel = "Today",
        onPrimaryAction = { viewModel.useToday() }
    )
}

@Composable
private fun ModeChip(
    mode: DateCalcMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (selected) OmniToolTheme.colors.coral.copy(alpha = 0.15f)
                else OmniToolTheme.colors.surface
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mode.displayName,
            style = OmniToolTheme.typography.label,
            color = if (selected) OmniToolTheme.colors.coral else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun DatePickerButton(
    label: String,
    date: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onClick)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Text(
            text = date,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textPrimary
        )
    }
}
