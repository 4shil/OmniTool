package com.omnitool.features.converter.temperature

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.animation.pressScale
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Temperature Converter Screen
 */
@Composable
fun TemperatureConverterScreen(
    onBack: () -> Unit,
    viewModel: TemperatureConverterViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Temperature",
            toolIcon = OmniToolIcons.UnitConverter,
            accentColor = OmniToolTheme.colors.coral,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                    // Unit selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)
                    ) {
                        TempUnit.values().forEach { unit ->
                            TempChip(
                                unit = unit,
                                selected = unit == viewModel.fromUnit,
                                onClick = { viewModel.setFromUnit(unit) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    WorkspaceInputField(
                        value = viewModel.inputValue,
                        onValueChange = { viewModel.updateInput(it) },
                        placeholder = "Enter temperature in ${viewModel.fromUnit.symbol}",
                        minLines = 1
                    )
                }
            },
            outputContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TempResultCard(
                        unit = TempUnit.CELSIUS,
                        value = viewModel.celsiusResult,
                        isSource = viewModel.fromUnit == TempUnit.CELSIUS,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.celsiusResult))
                            toastState.show("Celsius copied", ToastType.Success)
                        }
                    )
                    TempResultCard(
                        unit = TempUnit.FAHRENHEIT,
                        value = viewModel.fahrenheitResult,
                        isSource = viewModel.fromUnit == TempUnit.FAHRENHEIT,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.fahrenheitResult))
                            toastState.show("Fahrenheit copied", ToastType.Success)
                        }
                    )
                    TempResultCard(
                        unit = TempUnit.KELVIN,
                        value = viewModel.kelvinResult,
                        isSource = viewModel.fromUnit == TempUnit.KELVIN,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(viewModel.kelvinResult))
                            toastState.show("Kelvin copied", ToastType.Success)
                        }
                    )
                }
            },
            primaryActionLabel = "Clear",
            onPrimaryAction = { viewModel.clearAll() }
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            OmniToolToast(
                message = toastState.message,
                type = toastState.type,
                visible = toastState.isVisible,
                onDismiss = { toastState.dismiss() }
            )
        }
    }
}

@Composable
private fun TempChip(
    unit: TempUnit,
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
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = unit.symbol,
            style = OmniToolTheme.typography.label,
            color = if (selected) OmniToolTheme.colors.coral else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun TempResultCard(
    unit: TempUnit,
    value: String,
    isSource: Boolean,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(
                if (isSource) OmniToolTheme.colors.coral.copy(alpha = 0.1f)
                else OmniToolTheme.colors.elevatedSurface
            )
            .pressScale()
            .clickable(onClick = onCopy)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = unit.displayName,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textSecondary
        )
        Text(
            text = if (value.isNotEmpty()) "$value ${unit.symbol}" else "—",
            style = OmniToolTheme.typography.headingMedium.copy(fontSize = 24.sp),
            color = if (value.isNotEmpty()) OmniToolTheme.colors.coral else OmniToolTheme.colors.textMuted
        )
    }
}
