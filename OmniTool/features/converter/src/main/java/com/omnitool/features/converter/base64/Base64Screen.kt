package com.omnitool.features.converter.base64

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Base64 Encoder/Decoder Screen
 */
@Composable
fun Base64Screen(
    onBack: () -> Unit,
    viewModel: Base64ViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Base64",
        toolIcon = OmniToolIcons.Base64,
        accentColor = OmniToolTheme.colors.lavender,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Mode selector
                ModeSelector(
                    selected = viewModel.mode,
                    onSelect = { viewModel.setMode(it) }
                )
                
                // Input
                OutlinedTextField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.setInput(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { 
                        Text(
                            if (viewModel.mode == Base64Mode.ENCODE) "Enter text to encode..."
                            else "Enter Base64 to decode..."
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.lavender,
                        cursorColor = OmniToolTheme.colors.lavender
                    )
                )
                
                // URL-safe option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = viewModel.urlSafe,
                        onCheckedChange = { viewModel.setUrlSafe(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = OmniToolTheme.colors.lavender
                        )
                    )
                    Text(
                        text = "URL-safe encoding",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.textSecondary
                    )
                }
            }
        },
        outputContent = {
            ResultDisplay(
                result = viewModel.outputText,
                error = viewModel.errorMessage,
                onCopy = { copyToClipboard(context, viewModel.outputText) },
                onSwap = { viewModel.swap() }
            )
        },
        primaryActionLabel = if (viewModel.inputText.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun ModeSelector(
    selected: Base64Mode,
    onSelect: (Base64Mode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Base64Mode.values().forEach { mode ->
            val isSelected = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.lavender.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(mode) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) OmniToolTheme.colors.lavender 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ResultDisplay(
    result: String,
    error: String?,
    onCopy: () -> Unit,
    onSwap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Result",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Row {
                if (result.isNotEmpty()) {
                    IconButton(onClick = onSwap) {
                        Icon(
                            imageVector = OmniToolIcons.Swap,
                            contentDescription = "Swap",
                            tint = OmniToolTheme.colors.skyBlue
                        )
                    }
                    IconButton(onClick = onCopy) {
                        Icon(
                            imageVector = OmniToolIcons.Copy,
                            contentDescription = "Copy",
                            tint = OmniToolTheme.colors.lavender
                        )
                    }
                }
            }
        }
        
        when {
            error != null -> Text(
                text = error,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.softCoral
            )
            result.isEmpty() -> Text(
                text = "Enter text above",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted
            )
            else -> Text(
                text = result,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textPrimary,
                modifier = Modifier.heightIn(max = 100.dp)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Base64 result", text))
}
