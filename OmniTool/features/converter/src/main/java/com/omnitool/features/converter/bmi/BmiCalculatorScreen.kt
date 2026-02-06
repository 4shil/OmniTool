package com.omnitool.features.converter.bmi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * BMI Calculator Screen
 */
@Composable
fun BmiCalculatorScreen(
    onBack: () -> Unit,
    viewModel: BmiCalculatorViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "BMI Calculator",
        toolIcon = OmniToolIcons.Bmi,
        accentColor = OmniToolTheme.colors.primaryLime,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Unit selector
                UnitSelector(
                    selected = viewModel.unit,
                    onSelect = { viewModel.setUnit(it) }
                )
                
                // Height input
                OutlinedTextField(
                    value = viewModel.height,
                    onValueChange = { viewModel.setHeight(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Height") },
                    suffix = { Text(viewModel.unit.heightLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.primaryLime
                    )
                )
                
                // Weight input
                OutlinedTextField(
                    value = viewModel.weight,
                    onValueChange = { viewModel.setWeight(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Weight") },
                    suffix = { Text(viewModel.unit.weightLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.primaryLime
                    )
                )
            }
        },
        outputContent = {
            if (viewModel.hasResult) {
                BmiResult(
                    bmi = viewModel.formatBmi(),
                    category = viewModel.bmiCategory
                )
            } else {
                EmptyResultState()
            }
        },
        primaryActionLabel = if (viewModel.hasResult) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun UnitSelector(
    selected: BmiUnit,
    onSelect: (BmiUnit) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        BmiUnit.values().forEach { unit ->
            val isSelected = unit == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.primaryLime.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(unit) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unit.displayName,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) OmniToolTheme.colors.primaryLime 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun BmiResult(
    bmi: String,
    category: BmiCategory
) {
    val categoryColor = when (category) {
        BmiCategory.UNDERWEIGHT -> OmniToolTheme.colors.skyBlue
        BmiCategory.NORMAL -> OmniToolTheme.colors.primaryLime
        BmiCategory.OVERWEIGHT -> OmniToolTheme.colors.warmYellow
        BmiCategory.OBESE -> OmniToolTheme.colors.softCoral
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // BMI value
        Text(
            text = bmi,
            style = OmniToolTheme.typography.titleXL.copy(
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            ),
            color = categoryColor
        )
        
        // Category badge
        Box(
            modifier = Modifier
                .clip(OmniToolTheme.shapes.small)
                .background(categoryColor.copy(alpha = 0.2f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = category.displayName,
                style = OmniToolTheme.typography.label,
                color = categoryColor
            )
        }
        
        Divider(color = OmniToolTheme.colors.surface)
        
        // Category legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BmiCategory.values().forEach { cat ->
                CategoryItem(
                    category = cat,
                    isActive = cat == category
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: BmiCategory,
    isActive: Boolean
) {
    val color = when (category) {
        BmiCategory.UNDERWEIGHT -> OmniToolTheme.colors.skyBlue
        BmiCategory.NORMAL -> OmniToolTheme.colors.primaryLime
        BmiCategory.OVERWEIGHT -> OmniToolTheme.colors.warmYellow
        BmiCategory.OBESE -> OmniToolTheme.colors.softCoral
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Text(
            text = category.range,
            style = OmniToolTheme.typography.caption,
            color = if (isActive) color else OmniToolTheme.colors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyResultState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Enter height and weight",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textMuted
        )
    }
}
