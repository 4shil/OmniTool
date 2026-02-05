package com.omnitool.features.converter.age

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Age Calculator Screen
 */
@Composable
fun AgeCalculatorScreen(
    onBack: () -> Unit,
    viewModel: AgeCalculatorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Age Calculator",
        toolIcon = OmniToolIcons.AgeCalc,
        accentColor = OmniToolTheme.colors.coral,
        onBack = onBack,
        inputContent = {
            Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                DatePickerButton(
                    label = "Birth Date",
                    date = viewModel.getFormattedBirthDate(),
                    onClick = {
                        val cal = viewModel.birthDate ?: java.util.Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> viewModel.setBirthDate(y, m, d) },
                            cal.get(java.util.Calendar.YEAR),
                            cal.get(java.util.Calendar.MONTH),
                            cal.get(java.util.Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                )
                
                DatePickerButton(
                    label = "Calculate Age On",
                    date = viewModel.getFormattedTargetDate(),
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> viewModel.setTargetDate(y, m, d) },
                            viewModel.targetDate.get(java.util.Calendar.YEAR),
                            viewModel.targetDate.get(java.util.Calendar.MONTH),
                            viewModel.targetDate.get(java.util.Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                )
            }
        },
        outputContent = {
            if (viewModel.birthDate != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
                ) {
                    // Main age display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AgeUnit(value = viewModel.ageYears, label = "Years")
                        AgeUnit(value = viewModel.ageMonths, label = "Months")
                        AgeUnit(value = viewModel.ageDays, label = "Days")
                    }
                    
                    // Detailed stats
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(OmniToolTheme.shapes.medium)
                            .background(OmniToolTheme.colors.elevatedSurface)
                            .padding(OmniToolTheme.spacing.sm)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            StatRow("Total Days", "${viewModel.totalDays}")
                            StatRow("Total Weeks", "${viewModel.totalWeeks}")
                            StatRow("Total Months", "${viewModel.totalMonths}")
                            StatRow("Next Birthday", viewModel.nextBirthday)
                            StatRow("Days Until", "${viewModel.daysUntilBirthday}")
                        }
                    }
                }
            } else {
                Text(
                    text = "Select your birth date to calculate age",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        },
        primaryActionLabel = "Today",
        onPrimaryAction = { viewModel.useToday() }
    )
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

@Composable
private fun AgeUnit(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            style = OmniToolTheme.typography.headingLarge.copy(fontSize = 36.sp),
            color = OmniToolTheme.colors.coral
        )
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textPrimary
        )
    }
}
