package com.omnitool.features.converter.hash

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Hash Generator Screen
 */
@Composable
fun HashGeneratorScreen(
    onBack: () -> Unit,
    viewModel: HashGeneratorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    ToolWorkspaceScreen(
        toolName = "Hash Generator",
        toolIcon = OmniToolIcons.Hash,
        accentColor = OmniToolTheme.colors.primaryLime,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Input
                OutlinedTextField(
                    value = viewModel.inputText,
                    onValueChange = { viewModel.setInput(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("Enter text to hash...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OmniToolTheme.colors.primaryLime,
                        cursorColor = OmniToolTheme.colors.primaryLime
                    )
                )
                
                // Options row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Algorithms",
                        style = OmniToolTheme.typography.label,
                        color = OmniToolTheme.colors.textSecondary
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = viewModel.isUppercase,
                            onCheckedChange = { viewModel.toggleCase() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OmniToolTheme.colors.primaryLime
                            )
                        )
                        Text(
                            text = "UPPERCASE",
                            style = OmniToolTheme.typography.caption,
                            color = OmniToolTheme.colors.textMuted
                        )
                    }
                }
            }
        },
        outputContent = {
            HashResultList(
                hashes = viewModel.hashes,
                selected = viewModel.selectedAlgorithm,
                onSelect = { viewModel.setAlgorithm(it) },
                onCopy = { hash -> copyToClipboard(context, hash) }
            )
        },
        primaryActionLabel = if (viewModel.inputText.isNotEmpty()) "Clear" else null,
        onPrimaryAction = { viewModel.clear() }
    )
}

@Composable
private fun HashResultList(
    hashes: Map<HashAlgorithm, String>,
    selected: HashAlgorithm,
    onSelect: (HashAlgorithm) -> Unit,
    onCopy: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        if (hashes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter text to generate hashes",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 250.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HashAlgorithm.values().toList()) { algorithm ->
                    val hash = hashes[algorithm] ?: ""
                    val isSelected = algorithm == selected
                    
                    HashItem(
                        algorithm = algorithm,
                        hash = hash,
                        isSelected = isSelected,
                        onSelect = { onSelect(algorithm) },
                        onCopy = { onCopy(hash) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HashItem(
    algorithm: HashAlgorithm,
    hash: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (isSelected) OmniToolTheme.colors.primaryLime.copy(alpha = 0.1f)
                else OmniToolTheme.colors.surface
            )
            .clickable(onClick = onSelect)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = algorithm.displayName,
                style = OmniToolTheme.typography.label,
                color = if (isSelected) OmniToolTheme.colors.primaryLime 
                        else OmniToolTheme.colors.textPrimary
            )
            Text(
                text = hash,
                style = OmniToolTheme.typography.caption.copy(fontFamily = FontFamily.Monospace),
                color = OmniToolTheme.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        IconButton(onClick = onCopy) {
            Icon(
                imageVector = OmniToolIcons.Copy,
                contentDescription = "Copy",
                tint = OmniToolTheme.colors.primaryLime,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Hash", text))
}
