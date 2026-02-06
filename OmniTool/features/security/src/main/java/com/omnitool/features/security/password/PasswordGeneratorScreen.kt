package com.omnitool.features.security.password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceOutputField

/**
 * Password Generator Screen
 * 
 * Features:
 * - Length slider (4-128)
 * - Character type toggles
 * - One-tap generation
 * - Copy to clipboard
 */
@Composable
fun PasswordGeneratorScreen(
    onBack: () -> Unit,
    viewModel: PasswordGeneratorViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "Password Generator",
            toolIcon = OmniToolIcons.PasswordGen,
            accentColor = OmniToolTheme.colors.softCoral,
            onBack = onBack,
            inputContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
                ) {
                    // Length slider
                    LengthSlider(
                        length = viewModel.passwordLength,
                        onLengthChange = { viewModel.updateLength(it) }
                    )
                    
                    // Character options
                    CharacterOptions(
                        includeUppercase = viewModel.includeUppercase,
                        includeLowercase = viewModel.includeLowercase,
                        includeNumbers = viewModel.includeNumbers,
                        includeSymbols = viewModel.includeSymbols,
                        onToggleUppercase = { viewModel.toggleUppercase() },
                        onToggleLowercase = { viewModel.toggleLowercase() },
                        onToggleNumbers = { viewModel.toggleNumbers() },
                        onToggleSymbols = { viewModel.toggleSymbols() }
                    )
                }
            },
            outputContent = {
                Column {
                    if (viewModel.errorMessage != null) {
                        ErrorCard(
                            title = "Error",
                            explanation = viewModel.errorMessage!!,
                            suggestion = "Enable at least one character type"
                        )
                        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                    }
                    
                    // Generated password display
                    PasswordDisplay(
                        password = viewModel.generatedPassword,
                        onCopy = {
                            if (viewModel.generatedPassword.isNotEmpty()) {
                                clipboardManager.setText(AnnotatedString(viewModel.generatedPassword))
                                toastState.show("Password copied to clipboard", ToastType.Success)
                            }
                        }
                    )
                }
            },
            primaryActionLabel = "Generate",
            onPrimaryAction = { viewModel.generatePassword() },
            secondaryActionLabel = if (viewModel.generatedPassword.isNotEmpty()) "Clear" else null,
            onSecondaryAction = if (viewModel.generatedPassword.isNotEmpty()) {
                { viewModel.clearPassword() }
            } else null
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
private fun LengthSlider(
    length: Int,
    onLengthChange: (Int) -> Unit
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
                text = "Length",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = "$length characters",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.softCoral
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = length.toFloat(),
            onValueChange = { onLengthChange(it.toInt()) },
            valueRange = 4f..128f,
            steps = 123,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.softCoral,
                activeTrackColor = OmniToolTheme.colors.softCoral,
                inactiveTrackColor = OmniToolTheme.colors.surface
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "4",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
            Text(
                text = "128",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun CharacterOptions(
    includeUppercase: Boolean,
    includeLowercase: Boolean,
    includeNumbers: Boolean,
    includeSymbols: Boolean,
    onToggleUppercase: () -> Unit,
    onToggleLowercase: () -> Unit,
    onToggleNumbers: () -> Unit,
    onToggleSymbols: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Include Characters",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CharacterOptionChip(
                label = "A-Z",
                selected = includeUppercase,
                onClick = onToggleUppercase,
                modifier = Modifier.weight(1f)
            )
            CharacterOptionChip(
                label = "a-z",
                selected = includeLowercase,
                onClick = onToggleLowercase,
                modifier = Modifier.weight(1f)
            )
            CharacterOptionChip(
                label = "0-9",
                selected = includeNumbers,
                onClick = onToggleNumbers,
                modifier = Modifier.weight(1f)
            )
            CharacterOptionChip(
                label = "!@#",
                selected = includeSymbols,
                onClick = onToggleSymbols,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CharacterOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (selected) OmniToolTheme.colors.softCoral.copy(alpha = 0.2f)
                else OmniToolTheme.colors.surface
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = OmniToolTheme.typography.label,
            color = if (selected) OmniToolTheme.colors.softCoral 
                    else OmniToolTheme.colors.textSecondary
        )
    }
}

@Composable
private fun PasswordDisplay(
    password: String,
    onCopy: () -> Unit
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
                text = "Generated Password",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            
            if (password.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clip(OmniToolTheme.shapes.small)
                        .clickable(onClick = onCopy)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = OmniToolIcons.Copy,
                        contentDescription = "Copy",
                        tint = OmniToolTheme.colors.softCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Copy",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.softCoral
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.surface)
                .padding(OmniToolTheme.spacing.sm),
            contentAlignment = Alignment.CenterStart
        ) {
            if (password.isEmpty()) {
                Text(
                    text = "Tap Generate to create a password",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted
                )
            } else {
                Text(
                    text = password,
                    style = OmniToolTheme.typography.body.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = OmniToolTheme.colors.textPrimary
                )
            }
        }
        
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${password.length} characters",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}
