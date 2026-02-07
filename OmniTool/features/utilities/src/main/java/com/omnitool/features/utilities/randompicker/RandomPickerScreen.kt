package com.omnitool.features.utilities.randompicker

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Random Picker Screen
 */
@Composable
fun RandomPickerScreen(
    onBack: () -> Unit,
    viewModel: RandomPickerViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Random Picker",
        toolIcon = OmniToolIcons.RandomPicker,
        accentColor = OmniToolTheme.colors.mintGreen,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.currentInput,
                        onValueChange = { viewModel.updateInput(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Add item") },
                        placeholder = { Text("Enter item or paste list") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { viewModel.addItem() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OmniToolTheme.colors.mintGreen,
                            cursorColor = OmniToolTheme.colors.mintGreen
                        )
                    )
                    IconButton(
                        onClick = { viewModel.addItem() },
                        enabled = viewModel.currentInput.isNotBlank()
                    ) {
                        Icon(
                            imageVector = OmniToolIcons.Add,
                            contentDescription = "Add",
                            tint = if (viewModel.currentInput.isNotBlank()) 
                                OmniToolTheme.colors.mintGreen else OmniToolTheme.colors.textMuted
                        )
                    }
                }
                
                // Items list
                ItemsList(
                    items = viewModel.items,
                    onRemove = { viewModel.removeItem(it) }
                )
            }
        },
        outputContent = {
            ResultDisplay(
                selectedItem = viewModel.selectedItem,
                isPicking = viewModel.isPicking,
                itemCount = viewModel.items.size,
                onPick = { viewModel.pickRandom() },
                onRemoveSelected = { viewModel.removeSelected() }
            )
        },
        primaryActionLabel = if (viewModel.items.isNotEmpty()) "Pick Random" else null,
        onPrimaryAction = { viewModel.pickRandom() },
        secondaryActionLabel = if (viewModel.items.isNotEmpty()) "Clear All" else null,
        onSecondaryAction = { viewModel.clearItems() }
    )
}

@Composable
private fun ItemsList(
    items: List<String>,
    onRemove: (String) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Add items to pick from",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.elevatedSurface)
                .padding(OmniToolTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items) { item ->
                ItemRow(item = item, onRemove = { onRemove(item) })
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = OmniToolIcons.Clear,
            contentDescription = "Remove",
            tint = OmniToolTheme.colors.textMuted,
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onRemove)
        )
    }
}

@Composable
private fun ResultDisplay(
    selectedItem: String?,
    isPicking: Boolean,
    itemCount: Int,
    onPick: () -> Unit,
    onRemoveSelected: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPicking) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedItem != null) {
            Text(
                text = "🎉 Selected:",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = selectedItem,
                style = OmniToolTheme.typography.titleXL.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = OmniToolTheme.colors.mintGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(scale)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onPick) {
                    Text("Pick Again", color = OmniToolTheme.colors.mintGreen)
                }
                TextButton(onClick = onRemoveSelected) {
                    Text("Remove & Pick", color = OmniToolTheme.colors.softCoral)
                }
            }
        } else {
            Icon(
                imageVector = OmniToolIcons.RandomPicker,
                contentDescription = null,
                tint = OmniToolTheme.colors.textMuted,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (itemCount > 0) 
                    "Tap 'Pick Random' to select" 
                else 
                    "Add items first",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted,
                textAlign = TextAlign.Center
            )
            
            if (itemCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$itemCount items in list",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.mintGreen
                )
            }
        }
    }
}
