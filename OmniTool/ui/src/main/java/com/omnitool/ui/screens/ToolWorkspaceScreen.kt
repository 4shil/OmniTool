package com.omnitool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.PrimaryButton
import com.omnitool.ui.components.SecondaryButton
import com.omnitool.ui.icons.OmniToolIcons

/**
 * Universal Tool Workspace Screen Template
 * 
 * From UI/UX spec:
 * - Header: ← back + tool name
 * - Main workspace card: Input panel, Processing controls, Output panel
 * - Primary action: Large pastel floating button
 * - No nested settings, No sub menus
 * - One tool = one screen
 */
@Composable
fun ToolWorkspaceScreen(
    toolName: String,
    toolIcon: ImageVector,
    accentColor: Color,
    onBack: () -> Unit,
    inputContent: @Composable () -> Unit,
    outputContent: @Composable () -> Unit,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    processingControls: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniToolTheme.colors.background)
    ) {
        // Header
        ToolWorkspaceHeader(
            toolName = toolName,
            toolIcon = toolIcon,
            accentColor = accentColor,
            onBack = onBack
        )
        
        // Main content area
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = OmniToolTheme.spacing.sm)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
            
            // Input Panel
            WorkspaceCard(title = "Input") {
                inputContent()
            }
            
            // Processing controls (optional)
            if (processingControls != null) {
                Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
                processingControls()
            }
            
            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
            
            // Output Panel
            WorkspaceCard(title = "Output") {
                outputContent()
            }
            
            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.lg))
        }
        
        // Primary Action Bar
        ActionBar(
            primaryActionLabel = primaryActionLabel,
            onPrimaryAction = onPrimaryAction,
            accentColor = accentColor,
            secondaryActionLabel = secondaryActionLabel,
            onSecondaryAction = onSecondaryAction
        )
    }
}

@Composable
private fun ToolWorkspaceHeader(
    toolName: String,
    toolIcon: ImageVector,
    accentColor: Color,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = OmniToolTheme.spacing.sm,
                vertical = OmniToolTheme.spacing.sm
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = OmniToolIcons.Back,
                contentDescription = "Back",
                tint = OmniToolTheme.colors.textPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(OmniToolTheme.spacing.sm))
        
        // Tool icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = toolIcon,
                contentDescription = toolName,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(OmniToolTheme.spacing.xs))
        
        // Tool name
        Text(
            text = toolName,
            style = OmniToolTheme.typography.header,
            color = OmniToolTheme.colors.textPrimary
        )
    }
}

@Composable
private fun WorkspaceCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = OmniToolTheme.elevation.medium,
                shape = OmniToolTheme.shapes.card,
                spotColor = OmniToolTheme.colors.shadowColor
            )
            .clip(OmniToolTheme.shapes.card)
            .background(OmniToolTheme.colors.surface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = title,
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textMuted
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        content()
    }
}

@Composable
private fun ActionBar(
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    accentColor: Color,
    secondaryActionLabel: String?,
    onSecondaryAction: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OmniToolTheme.colors.surface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
    ) {
        if (secondaryActionLabel != null && onSecondaryAction != null) {
            SecondaryButton(
                text = secondaryActionLabel,
                onClick = onSecondaryAction,
                modifier = Modifier.weight(1f)
            )
        }
        
        PrimaryButton(
            text = primaryActionLabel,
            onClick = onPrimaryAction,
            accentColor = accentColor,
            modifier = if (secondaryActionLabel != null) Modifier.weight(1f) else Modifier.fillMaxWidth()
        )
    }
}

/**
 * Reusable Input Field for tool workspace
 */
@Composable
fun WorkspaceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Enter text...",
    modifier: Modifier = Modifier,
    minLines: Int = 5
) {
    val focusRequester = remember { FocusRequester() }
    val clipboardManager = LocalClipboardManager.current
    
    Column(modifier = modifier) {
        // Input field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = (minLines * 24).dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface)
                .padding(OmniToolTheme.spacing.sm)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
            
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = OmniToolTheme.typography.body.copy(
                    color = OmniToolTheme.colors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        // Paste shortcut button
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.elevatedSurface)
                    .clickable {
                        clipboardManager.getText()?.let { 
                            onValueChange(it.text) 
                        }
                    }
                    .padding(horizontal = OmniToolTheme.spacing.xs, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = OmniToolIcons.Paste,
                        contentDescription = "Paste",
                        tint = OmniToolTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Paste",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.textSecondary
                    )
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

/**
 * Reusable Output Field for tool workspace
 */
@Composable
fun WorkspaceOutputField(
    value: String,
    placeholder: String = "Output will appear here...",
    modifier: Modifier = Modifier,
    onCopy: () -> Unit = {},
    onExport: (() -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current
    
    Column(modifier = modifier) {
        // Output display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface)
                .padding(OmniToolTheme.spacing.sm)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            } else {
                Text(
                    text = value,
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
        
        // Copy and Export buttons
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Copy button
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(OmniToolTheme.colors.elevatedSurface)
                    .clickable(enabled = value.isNotEmpty()) {
                        clipboardManager.setText(AnnotatedString(value))
                        onCopy()
                    }
                    .padding(horizontal = OmniToolTheme.spacing.xs, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = OmniToolIcons.Copy,
                        contentDescription = "Copy",
                        tint = if (value.isNotEmpty()) 
                            OmniToolTheme.colors.textSecondary 
                        else 
                            OmniToolTheme.colors.textDisabled,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Copy",
                        style = OmniToolTheme.typography.caption,
                        color = if (value.isNotEmpty()) 
                            OmniToolTheme.colors.textSecondary 
                        else 
                            OmniToolTheme.colors.textDisabled
                    )
                }
            }
            
            if (onExport != null) {
                Spacer(modifier = Modifier.width(OmniToolTheme.spacing.xs))
                
                // Export button
                Box(
                    modifier = Modifier
                        .clip(OmniToolTheme.shapes.small)
                        .background(OmniToolTheme.colors.elevatedSurface)
                        .clickable(enabled = value.isNotEmpty(), onClick = onExport)
                        .padding(horizontal = OmniToolTheme.spacing.xs, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = OmniToolIcons.Export,
                            contentDescription = "Export",
                            tint = if (value.isNotEmpty()) 
                                OmniToolTheme.colors.textSecondary 
                            else 
                                OmniToolTheme.colors.textDisabled,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Export",
                            style = OmniToolTheme.typography.caption,
                            color = if (value.isNotEmpty()) 
                                OmniToolTheme.colors.textSecondary 
                            else 
                                OmniToolTheme.colors.textDisabled
                        )
                    }
                }
            }
        }
    }
}
