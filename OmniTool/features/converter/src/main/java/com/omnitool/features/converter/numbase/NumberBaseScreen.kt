package com.omnitool.features.converter.numbase

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Number Base Converter Screen
 */
@Composable
fun NumberBaseScreen(
    onBack: () -> Unit,
    viewModel: NumberBaseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Base Converter",
        toolIcon = OmniToolIcons.NumberBase,
        accentColor = OmniToolTheme.colors.lavender,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Base selector
                BaseSelector(
                    selected = viewModel.fromBase,
                    onSelect = { viewModel.setFromBase(it) }
                )
                
                // Input
                OutlinedTextField(
                    value = viewModel.inputValue,
                    onValueChange = { viewModel.setInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter ${viewModel.fromBase.displayName} number") },
                    placeholder = { Text(getPlaceholder(viewModel.fromBase)) },
                    singleLine = true,
                    isError = viewModel.errorMessage != null,
                    supportingText = viewModel.errorMessage?.let { { Text(it) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.lavender,
                        cursorColor = OmniToolTheme.colors.lavender
                    )
                )
            }
        },
        outputContent = {
            ConversionResults(
                binary = viewModel.binaryResult,
                octal = viewModel.octalResult,
                decimal = viewModel.decimalResult,
                hex = viewModel.hexResult,
                fromBase = viewModel.fromBase,
                onCopy = { copyToClipboard(context, it) }
            )
        },
        primaryActionLabel = if (viewModel.inputValue.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

private fun getPlaceholder(base: NumberBase): String {
    return when (base) {
        NumberBase.BINARY -> "e.g., 1010"
        NumberBase.OCTAL -> "e.g., 17"
        NumberBase.DECIMAL -> "e.g., 255"
        NumberBase.HEXADECIMAL -> "e.g., FF"
    }
}

@Composable
private fun BaseSelector(
    selected: NumberBase,
    onSelect: (NumberBase) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NumberBase.values().forEach { base ->
            val isSelected = base == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.lavender.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(base) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = base.displayName,
                    style = OmniToolTheme.typography.caption,
                    color = if (isSelected) OmniToolTheme.colors.lavender 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ConversionResults(
    binary: String,
    octal: String,
    decimal: String,
    hex: String,
    fromBase: NumberBase,
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
        if (binary.isEmpty() && octal.isEmpty() && decimal.isEmpty() && hex.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter a number to convert",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            ResultRow("Binary", binary, fromBase == NumberBase.BINARY, "0b", onCopy)
            ResultRow("Octal", octal, fromBase == NumberBase.OCTAL, "0o", onCopy)
            ResultRow("Decimal", decimal, fromBase == NumberBase.DECIMAL, "", onCopy)
            ResultRow("Hex", hex, fromBase == NumberBase.HEXADECIMAL, "0x", onCopy)
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    isSource: Boolean,
    prefix: String,
    onCopy: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (isSource) OmniToolTheme.colors.lavender.copy(alpha = 0.1f)
                else OmniToolTheme.colors.surface
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = if (value.isNotEmpty()) "$prefix$value" else "-",
                style = OmniToolTheme.typography.body.copy(fontFamily = FontFamily.Monospace),
                color = if (isSource) OmniToolTheme.colors.lavender else OmniToolTheme.colors.textPrimary
            )
        }
        
        if (value.isNotEmpty()) {
            IconButton(onClick = { onCopy("$prefix$value") }) {
                Icon(
                    imageVector = OmniToolIcons.Copy,
                    contentDescription = "Copy",
                    tint = OmniToolTheme.colors.lavender,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Number", text))
}
