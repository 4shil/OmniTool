package com.omnitool.features.text.clipboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
 * Clipboard History Screen
 */
@Composable
fun ClipboardHistoryScreen(
    onBack: () -> Unit,
    viewModel: ClipboardHistoryViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    // Get current clipboard content on launch
    LaunchedEffect(Unit) {
        clipboardManager.getText()?.text?.let { content ->
            viewModel.addToHistory(content)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Clipboard History",
            toolIcon = OmniToolIcons.Clipboard,
            accentColor = OmniToolTheme.colors.mint,
            onBack = onBack,
            inputContent = {
                Column(verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)) {
                    // Search
                    WorkspaceInputField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = "Search history...",
                        minLines = 1
                    )
                    
                    // Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${viewModel.filteredItems.size} items",
                            style = OmniToolTheme.typography.caption,
                            color = OmniToolTheme.colors.textMuted
                        )
                        Text(
                            text = "${viewModel.historyItems.count { it.isPinned }} pinned",
                            style = OmniToolTheme.typography.caption,
                            color = OmniToolTheme.colors.accentYellow
                        )
                    }
                }
            },
            outputContent = {
                if (viewModel.filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = OmniToolIcons.Clipboard,
                                contentDescription = null,
                                tint = OmniToolTheme.colors.textMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
                            Text(
                                text = if (viewModel.searchQuery.isEmpty()) 
                                    "No clipboard history yet" 
                                else 
                                    "No results found",
                                style = OmniToolTheme.typography.body,
                                color = OmniToolTheme.colors.textMuted
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xxs),
                        modifier = Modifier.heightIn(max = 350.dp)
                    ) {
                        items(
                            items = viewModel.filteredItems,
                            key = { it.id }
                        ) { entry ->
                            ClipboardEntryCard(
                                entry = entry,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(entry.content))
                                    toastState.show("Copied", ToastType.Success)
                                },
                                onPin = { viewModel.togglePin(entry.id) },
                                onDelete = { viewModel.deleteItem(entry.id) }
                            )
                        }
                    }
                }
            },
            primaryActionLabel = "Clear Unpinned",
            onPrimaryAction = { 
                viewModel.clearUnpinned()
                toastState.show("Unpinned items cleared", ToastType.Info)
            }
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
private fun ClipboardEntryCard(
    entry: ClipboardEntry,
    onCopy: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (entry.isPinned) 
                    OmniToolTheme.colors.accentYellow.copy(alpha = 0.08f)
                else 
                    OmniToolTheme.colors.elevatedSurface
            )
            .pressScale()
            .clickable(onClick = onCopy)
            .padding(OmniToolTheme.spacing.xs),
        verticalAlignment = Alignment.Top
    ) {
        // Pin indicator
        if (entry.isPinned) {
            Icon(
                imageVector = OmniToolIcons.Pin,
                contentDescription = "Pinned",
                tint = OmniToolTheme.colors.accentYellow,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        
        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.getPreview(80),
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.getFormattedTime(),
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
        
        // Actions
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            IconButton(
                icon = if (entry.isPinned) OmniToolIcons.Pin else OmniToolIcons.PinOutline,
                onClick = onPin,
                tint = if (entry.isPinned) 
                    OmniToolTheme.colors.accentYellow 
                else 
                    OmniToolTheme.colors.textMuted
            )
            IconButton(
                icon = OmniToolIcons.Delete,
                onClick = onDelete,
                tint = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun IconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(OmniToolTheme.shapes.small)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}
