package com.omnitool.features.converter.temperature

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Temperature Converter Screen
 */
@Composable
fun TemperatureScreen(
    onBack: () -> Unit,
    viewModel: TemperatureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Temperature",
        toolIcon = OmniToolIcons.Temperature,
        accentColor = OmniToolTheme.colors.softCoral,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Unit selector
                UnitSelector(
                    selected = viewModel.fromUnit,
                    onSelect = { viewModel.setFromUnit(it) }
                )
                
                // Input
                OutlinedTextField(
                    value = viewModel.inputValue,
                    onValueChange = { viewModel.setInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter ${viewModel.fromUnit.displayName}") },
                    suffix = { Text(viewModel.fromUnit.symbol) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.softCoral,
                        cursorColor = OmniToolTheme.colors.softCoral
                    )
                )
            }
        },
        outputContent = {
            TemperatureResults(
                celsius = viewModel.celsiusResult,
                fahrenheit = viewModel.fahrenheitResult,
                kelvin = viewModel.kelvinResult,
                fromUnit = viewModel.fromUnit,
                onCopy = { copyToClipboard(context, it) }
            )
        },
        primaryActionLabel = if (viewModel.inputValue.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun UnitSelector(
    selected: TempUnit,
    onSelect: (TempUnit) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TempUnit.values().forEach { unit ->
            val isSelected = unit == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.softCoral.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(unit) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unit.symbol,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) OmniToolTheme.colors.softCoral 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun TemperatureResults(
    celsius: String,
    fahrenheit: String,
    kelvin: String,
    fromUnit: TempUnit,
    onCopy: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (celsius.isEmpty() && fahrenheit.isEmpty() && kelvin.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter temperature to convert",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            TempResultRow("Celsius", celsius, "°C", fromUnit == TempUnit.CELSIUS, onCopy)
            TempResultRow("Fahrenheit", fahrenheit, "°F", fromUnit == TempUnit.FAHRENHEIT, onCopy)
            TempResultRow("Kelvin", kelvin, "K", fromUnit == TempUnit.KELVIN, onCopy)
        }
    }
}

@Composable
private fun TempResultRow(
    label: String,
    value: String,
    symbol: String,
    isSource: Boolean,
    onCopy: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (isSource) OmniToolTheme.colors.softCoral.copy(alpha = 0.1f)
                else OmniToolTheme.colors.surface
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = if (value.isNotEmpty()) "$value $symbol" else "-",
                style = OmniToolTheme.typography.label,
                color = if (isSource) OmniToolTheme.colors.softCoral 
                        else OmniToolTheme.colors.textPrimary
            )
        }
        
        if (value.isNotEmpty()) {
            IconButton(onClick = { onCopy("$value $symbol") }) {
                Icon(
                    imageVector = OmniToolIcons.Copy,
                    contentDescription = "Copy",
                    tint = OmniToolTheme.colors.softCoral,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Temperature", text))
}
