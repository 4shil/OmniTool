package com.omnitool.features.security.otp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.components.OmniToolToast
import com.omnitool.ui.components.ToastType
import com.omnitool.ui.components.rememberToastState
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * OTP Generator Screen
 * 
 * Features:
 * - TOTP with countdown timer
 * - HOTP with counter
 * - Generate random secret
 * - Copy OTP
 */
@Composable
fun OtpGeneratorScreen(
    onBack: () -> Unit,
    viewModel: OtpGeneratorViewModel = hiltViewModel()
) {
    val toastState = rememberToastState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        ToolWorkspaceScreen(
            toolName = "OTP Generator",
            toolIcon = OmniToolIcons.OtpGenerator,
            accentColor = OmniToolTheme.colors.softCoral,
            onBack = onBack,
            inputContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
                ) {
                    // Mode toggle
                    ModeToggle(
                        currentMode = viewModel.mode,
                        onModeChange = { viewModel.setMode(it) }
                    )
                    
                    // Secret key input
                    SecretKeyInput(
                        value = viewModel.secretKey,
                        onValueChange = { viewModel.updateSecretKey(it) },
                        onGenerateRandom = { viewModel.setRandomSecret() }
                    )
                    
                    // Counter for HOTP mode
                    if (viewModel.mode == OtpMode.HOTP) {
                        CounterDisplay(
                            counter = viewModel.counter,
                            onIncrement = { viewModel.incrementCounter() }
                        )
                    }
                }
            },
            outputContent = {
                Column {
                    if (viewModel.errorMessage != null) {
                        ErrorCard(
                            title = "Error",
                            explanation = viewModel.errorMessage!!,
                            suggestion = "Enter a valid Base32 secret key (A-Z, 2-7)"
                        )
                        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                    }
                    
                    OtpDisplay(
                        otp = viewModel.currentOtp,
                        timeRemaining = viewModel.timeRemaining,
                        period = viewModel.period,
                        mode = viewModel.mode,
                        onCopy = {
                            if (viewModel.currentOtp.isNotEmpty()) {
                                clipboardManager.setText(AnnotatedString(viewModel.currentOtp))
                                toastState.show("OTP copied to clipboard", ToastType.Success)
                            }
                        }
                    )
                }
            },
            primaryActionLabel = "Generate",
            onPrimaryAction = { viewModel.generateOtp() },
            secondaryActionLabel = if (viewModel.isGenerating) "Clear" else null,
            onSecondaryAction = if (viewModel.isGenerating) {
                { viewModel.clearAll() }
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
private fun ModeToggle(
    currentMode: OtpMode,
    onModeChange: (OtpMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
    ) {
        OtpMode.values().forEach { mode ->
            val isSelected = mode == currentMode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) OmniToolTheme.colors.softCoral.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.elevatedSurface
                    )
                    .clickable { onModeChange(mode) }
                    .padding(OmniToolTheme.spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = mode.name,
                        style = OmniToolTheme.typography.label,
                        color = if (isSelected) OmniToolTheme.colors.softCoral 
                                else OmniToolTheme.colors.textSecondary
                    )
                    Text(
                        text = if (mode == OtpMode.TOTP) "Time-based" else "Counter-based",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SecretKeyInput(
    value: String,
    onValueChange: (String) -> Unit,
    onGenerateRandom: () -> Unit
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
                text = "Secret Key (Base32)",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            
            Row(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .clickable(onClick = onGenerateRandom)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = OmniToolIcons.Shuffle,
                    contentDescription = "Generate Random",
                    tint = OmniToolTheme.colors.softCoral,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Random",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.softCoral
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("JBSWY3DPEHPK3PXP...") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.softCoral,
                unfocusedBorderColor = OmniToolTheme.colors.surface,
                cursorColor = OmniToolTheme.colors.softCoral,
                focusedTextColor = OmniToolTheme.colors.textPrimary,
                unfocusedTextColor = OmniToolTheme.colors.textPrimary,
                focusedPlaceholderColor = OmniToolTheme.colors.textMuted,
                unfocusedPlaceholderColor = OmniToolTheme.colors.textMuted
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Use A-Z and 2-7 only",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
    }
}

@Composable
private fun CounterDisplay(
    counter: Long,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Counter",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = counter.toString(),
                style = OmniToolTheme.typography.header,
                color = OmniToolTheme.colors.softCoral
            )
        }
        
        Row(
            modifier = Modifier
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.softCoral.copy(alpha = 0.2f))
                .clickable(onClick = onIncrement)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = OmniToolIcons.Add,
                contentDescription = "Increment",
                tint = OmniToolTheme.colors.softCoral,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Increment",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.softCoral
            )
        }
    }
}

@Composable
private fun OtpDisplay(
    otp: String,
    timeRemaining: Int,
    period: Int,
    mode: OtpMode,
    onCopy: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = timeRemaining.toFloat() / period,
        animationSpec = tween(300),
        label = "timerProgress"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "One-Time Password",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            
            if (otp.isNotEmpty()) {
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
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        // OTP display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OmniToolTheme.shapes.medium)
                .background(OmniToolTheme.colors.surface)
                .padding(OmniToolTheme.spacing.md),
            contentAlignment = Alignment.Center
        ) {
            if (otp.isEmpty()) {
                Text(
                    text = "Tap Generate to create OTP",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textMuted,
                    textAlign = TextAlign.Center
                )
            } else {
                // Format OTP with spaces for readability
                val formattedOtp = otp.chunked(3).joinToString(" ")
                Text(
                    text = formattedOtp,
                    style = OmniToolTheme.typography.titleXL.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        letterSpacing = 4.sp
                    ),
                    color = OmniToolTheme.colors.textPrimary
                )
            }
        }
        
        // Timer for TOTP
        if (mode == OtpMode.TOTP && otp.isNotEmpty()) {
            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(OmniToolTheme.shapes.small),
                    color = if (timeRemaining <= 5) OmniToolTheme.colors.softCoral 
                            else OmniToolTheme.colors.primaryLime,
                    trackColor = OmniToolTheme.colors.surface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Refreshes in ${timeRemaining}s",
                    style = OmniToolTheme.typography.caption,
                    color = if (timeRemaining <= 5) OmniToolTheme.colors.softCoral 
                            else OmniToolTheme.colors.textMuted
                )
            }
        }
    }
}
