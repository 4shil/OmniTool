package com.omnitool.features.text.lorem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
 * Lorem Ipsum Generator Screen
 */
@Composable
fun LoremIpsumScreen(
    onBack: () -> Unit,
    viewModel: LoremIpsumViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Lorem Ipsum",
            toolIcon = OmniToolIcons.LoremIpsum,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)) {
                    // Generation type selector
                    Text(
                        text = "Generate",
                        style = OmniToolTheme.typography.label,
                        color = OmniToolTheme.colors.textSecondary
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(OmniToolTheme.shapes.medium)
                            .background(OmniToolTheme.colors.elevatedSurface)
                    ) {
                        GenerationType.values().forEach { type ->
                            val isSelected = type == viewModel.generationType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) OmniToolTheme.colors.mint.copy(alpha = 0.2f)
                                        else OmniToolTheme.colors.elevatedSurface
                                    )
                                    .clickable { viewModel.setType(type) }
                                    .padding(OmniToolTheme.spacing.xs),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type.displayName,
                                    style = OmniToolTheme.typography.label,
                                    color = if (isSelected) 
                                        OmniToolTheme.colors.mint 
                                    else 
                                        OmniToolTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                    
                    // Count slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Count",
                                style = OmniToolTheme.typography.label,
                                color = OmniToolTheme.colors.textSecondary
                            )
                            Text(
                                text = "${viewModel.paragraphCount}",
                                style = OmniToolTheme.typography.label,
                                color = OmniToolTheme.colors.mint
                            )
                        }
                        
                        Slider(
                            value = viewModel.paragraphCount.toFloat(),
                            onValueChange = { viewModel.setCount(it.toInt()) },
                            valueRange = 1f..20f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = OmniToolTheme.colors.mint,
                                activeTrackColor = OmniToolTheme.colors.mint,
                                inactiveTrackColor = OmniToolTheme.colors.elevatedSurface
                            )
                        )
                    }
                }
            },
            outputContent = {
                WorkspaceOutputField(
                    value = viewModel.outputText,
                    placeholder = "Generated lorem ipsum will appear here...",
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
