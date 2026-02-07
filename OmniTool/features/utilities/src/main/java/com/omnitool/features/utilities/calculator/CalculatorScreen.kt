package com.omnitool.features.utilities.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Calculator Screen
 * 
 * Standard calculator with grid layout
 */
@Composable
fun CalculatorScreen(
    onBack: () -> Unit,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Calculator",
        toolIcon = OmniToolIcons.Calculator,
        accentColor = OmniToolTheme.colors.softCoral,
        onBack = onBack,
        inputContent = {
            // Display area
            CalculatorDisplay(
                expression = viewModel.expression,
                value = viewModel.displayValue,
                hasMemory = viewModel.hasMemory
            )
        },
        outputContent = {
            // Button grid
            CalculatorKeypad(
                onDigit = { viewModel.onDigit(it) },
                onOperation = { viewModel.onOperation(it) },
                onEquals = { viewModel.onEquals() },
                onClear = { viewModel.clear() },
                onAllClear = { viewModel.allClear() },
                onBackspace = { viewModel.backspace() },
                onPercent = { viewModel.onPercent() },
                onPlusMinus = { viewModel.onPlusMinus() }
            )
        },
        primaryActionLabel = null,
        onPrimaryAction = null
    )
}

@Composable
private fun CalculatorDisplay(
    expression: String,
    value: String,
    hasMemory: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (hasMemory) {
                Text(
                    text = "M",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.softCoral
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            
            Text(
                text = expression,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = OmniToolTheme.typography.titleXL.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp
            ),
            color = OmniToolTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CalculatorKeypad(
    onDigit: (String) -> Unit,
    onOperation: (Operation) -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onAllClear: () -> Unit,
    onBackspace: () -> Unit,
    onPercent: () -> Unit,
    onPlusMinus: () -> Unit
) {
    val buttonSpacing = 8.dp
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        // Row 1: AC, ±, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalcButton("AC", Modifier.weight(1f), OmniToolTheme.colors.textMuted) { onAllClear() }
            CalcButton("±", Modifier.weight(1f), OmniToolTheme.colors.textMuted) { onPlusMinus() }
            CalcButton("%", Modifier.weight(1f), OmniToolTheme.colors.textMuted) { onPercent() }
            CalcButton("÷", Modifier.weight(1f), OmniToolTheme.colors.warmYellow) { onOperation(Operation.DIVIDE) }
        }
        
        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalcButton("7", Modifier.weight(1f)) { onDigit("7") }
            CalcButton("8", Modifier.weight(1f)) { onDigit("8") }
            CalcButton("9", Modifier.weight(1f)) { onDigit("9") }
            CalcButton("×", Modifier.weight(1f), OmniToolTheme.colors.warmYellow) { onOperation(Operation.MULTIPLY) }
        }
        
        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalcButton("4", Modifier.weight(1f)) { onDigit("4") }
            CalcButton("5", Modifier.weight(1f)) { onDigit("5") }
            CalcButton("6", Modifier.weight(1f)) { onDigit("6") }
            CalcButton("−", Modifier.weight(1f), OmniToolTheme.colors.warmYellow) { onOperation(Operation.SUBTRACT) }
        }
        
        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalcButton("1", Modifier.weight(1f)) { onDigit("1") }
            CalcButton("2", Modifier.weight(1f)) { onDigit("2") }
            CalcButton("3", Modifier.weight(1f)) { onDigit("3") }
            CalcButton("+", Modifier.weight(1f), OmniToolTheme.colors.warmYellow) { onOperation(Operation.ADD) }
        }
        
        // Row 5: 0, ., ⌫, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalcButton("0", Modifier.weight(1f)) { onDigit("0") }
            CalcButton(".", Modifier.weight(1f)) { onDigit(".") }
            CalcButton("⌫", Modifier.weight(1f), OmniToolTheme.colors.textMuted) { onBackspace() }
            CalcButton("=", Modifier.weight(1f), OmniToolTheme.colors.primaryLime) { onEquals() }
        }
    }
}

@Composable
private fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = OmniToolTheme.colors.textPrimary,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1.4f)
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = OmniToolTheme.typography.header.copy(
                fontSize = 24.sp
            ),
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
