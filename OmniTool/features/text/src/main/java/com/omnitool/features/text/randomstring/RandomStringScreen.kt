package com.omnitool.features.text.randomstring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceOutputField

/**
 * Random String Generator Screen
 */
@Composable
fun RandomStringScreen(
    onBack: () -> Unit,
    viewModel: RandomStringViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Random String",
            toolIcon = OmniToolIcons.RandomString,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                    // Length slider
                    SliderSetting(
                        label = "Length",
                        value = viewModel.length,
                        onValueChange = { viewModel.setLength(it) },
                        valueRange = 1f..128f
                    )
                    
                    // Count slider
                    SliderSetting(
                        label = "Count",
                        value = viewModel.count,
                        onValueChange = { viewModel.setCount(it) },
                        valueRange = 1f..20f
                    )
                    
                    // Character options
                    Text(
                        text = "Characters",
                        style = OmniToolTheme.typography.label,
                        color = OmniToolTheme.colors.textSecondary
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
                        ) {
                            OptionChip(
                                label = "A-Z",
                                checked = viewModel.options.uppercase,
                                onCheckedChange = {
                                    viewModel.updateOptions(viewModel.options.copy(uppercase = it))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            OptionChip(
                                label = "a-z",
                                checked = viewModel.options.lowercase,
                                onCheckedChange = {
                                    viewModel.updateOptions(viewModel.options.copy(lowercase = it))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            OptionChip(
                                label = "0-9",
                                checked = viewModel.options.numbers,
                                onCheckedChange = {
                                    viewModel.updateOptions(viewModel.options.copy(numbers = it))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
                        ) {
                            OptionChip(
                                label = "!@#$",
                                checked = viewModel.options.symbols,
                                onCheckedChange = {
                                    viewModel.updateOptions(viewModel.options.copy(symbols = it))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            OptionChip(
                                label = "No ambiguous",
                                checked = viewModel.options.excludeAmbiguous,
                                onCheckedChange = {
                                    viewModel.updateOptions(viewModel.options.copy(excludeAmbiguous = it))
                                },
                                modifier = Modifier.weight(2f)
                            )
                        }
                    }
                }
            },
            outputContent = {
                WorkspaceOutputField(
                    value = viewModel.outputText,
                    placeholder = "Generated strings will appear here...",
                    onCopy = {
                        toastState.show("Copied to clipboard", ToastType.Success)
                    }
                )
            },
            primaryActionLabel = "Generate",
            onPrimaryAction = { viewModel.generate() }
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
private fun SliderSetting(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Text(
                text = "$value",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.mint
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.mint,
                activeTrackColor = OmniToolTheme.colors.mint,
                inactiveTrackColor = OmniToolTheme.colors.elevatedSurface
            )
        )
    }
}

@Composable
private fun OptionChip(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (checked) OmniToolTheme.colors.mint.copy(alpha = 0.15f)
                else OmniToolTheme.colors.elevatedSurface
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.label,
            color = if (checked) OmniToolTheme.colors.mint else OmniToolTheme.colors.textSecondary
        )
    }
}
