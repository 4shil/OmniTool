package com.omnitool.features.converter.loan

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
 * Loan Calculator Screen
 */
@Composable
fun LoanCalculatorScreen(
    onBack: () -> Unit,
    viewModel: LoanCalculatorViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Loan Calculator",
        toolIcon = OmniToolIcons.Loan,
        accentColor = OmniToolTheme.colors.warmYellow,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Principal amount
                OutlinedTextField(
                    value = viewModel.principal,
                    onValueChange = { viewModel.setPrincipal(it) },
                    label = { Text("Loan Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.warmYellow
                    )
                )
                
                // Interest rate
                OutlinedTextField(
                    value = viewModel.interestRate,
                    onValueChange = { viewModel.setInterestRate(it) },
                    label = { Text("Interest Rate (% per year)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.warmYellow
                    )
                )
                
                // Tenure
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.tenure,
                        onValueChange = { viewModel.setTenure(it) },
                        label = { Text("Tenure") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniToolTheme.colors.warmYellow
                        )
                    )
                    
                    TenureToggle(
                        selected = viewModel.tenureType,
                        onSelect = { viewModel.setTenureType(it) }
                    )
                }
            }
        },
        outputContent = {
            if (viewModel.hasResult) {
                LoanResult(
                    emi = viewModel.formatCurrency(viewModel.emi),
                    totalInterest = viewModel.formatCurrency(viewModel.totalInterest),
                    totalPayment = viewModel.formatCurrency(viewModel.totalPayment),
                    interestPercent = viewModel.getInterestPercentage()
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
private fun TenureToggle(
    selected: TenureType,
    onSelect: (TenureType) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.elevatedSurface)
    ) {
        TenureType.values().forEach { type ->
            val isSelected = type == selected
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.warmYellow.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .clickable { onSelect(type) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = type.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = if (isSelected) OmniToolTheme.colors.warmYellow 
                            else OmniToolTheme.colors.textMuted
                )
            }
        }
    }
}

@Composable
private fun LoanResult(
    emi: String,
    totalInterest: String,
    totalPayment: String,
    interestPercent: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // EMI highlight
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Monthly EMI",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = emi,
                style = OmniToolTheme.typography.titleXL.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = OmniToolTheme.colors.warmYellow
            )
        }
        
        Divider(color = OmniToolTheme.colors.surface)
        
        // Breakdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ResultItem(label = "Total Interest", value = totalInterest, color = OmniToolTheme.colors.softCoral)
            ResultItem(label = "Total Payment", value = totalPayment, color = OmniToolTheme.colors.primaryLime)
        }
        
        // Interest ratio bar
        Column {
            Text(
                text = "Interest Ratio: ${String.format("%.1f", interestPercent)}%",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.primaryLime)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(interestPercent / 100f)
                        .fillMaxHeight()
                        .background(OmniToolTheme.colors.softCoral)
                )
            }
        }
    }
}

@Composable
private fun ResultItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
        Text(
            text = value,
            style = OmniToolTheme.typography.label,
            color = color
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
            text = "Enter loan details to calculate EMI",
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textMuted,
            textAlign = TextAlign.Center
        )
    }
}
