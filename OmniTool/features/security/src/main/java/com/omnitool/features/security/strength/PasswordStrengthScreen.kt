package com.omnitool.features.security.strength

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Password Strength Checker Screen
 * 
 * Features:
 * - Real-time strength analysis
 * - Visual strength meter
 * - Detailed feedback with suggestions
 * - Entropy display
 */
@Composable
fun PasswordStrengthScreen(
    onBack: () -> Unit,
    viewModel: PasswordStrengthViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Password Strength",
        toolIcon = OmniToolIcons.PasswordStrength,
        accentColor = OmniToolTheme.colors.softCoral,
        onBack = onBack,
        inputContent = {
            WorkspaceInputField(
                value = viewModel.inputPassword,
                onValueChange = { viewModel.updatePassword(it) },
                placeholder = "Enter password to check...",
                minLines = 1
            )
        },
        outputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Strength meter
                StrengthMeter(
                    level = viewModel.strengthLevel,
                    score = viewModel.strengthScore
                )
                
                // Entropy display
                if (viewModel.inputPassword.isNotEmpty()) {
                    EntropyDisplay(entropyBits = viewModel.entropyBits)
                }
                
                // Feedback list
                if (viewModel.feedback.isNotEmpty()) {
                    FeedbackList(feedback = viewModel.feedback)
                }
            }
        },
        primaryActionLabel = "Clear",
        onPrimaryAction = { viewModel.clearPassword() }
    )
}

@Composable
private fun StrengthMeter(
    level: StrengthLevel,
    score: Int
) {
    val strengthColor by animateColorAsState(
        targetValue = when (level) {
            StrengthLevel.NONE -> OmniToolTheme.colors.textMuted
            StrengthLevel.VERY_WEAK -> Color(0xFFE53935) // Red
            StrengthLevel.WEAK -> Color(0xFFFF7043) // Orange
            StrengthLevel.FAIR -> Color(0xFFFFB300) // Amber
            StrengthLevel.STRONG -> Color(0xFF7CB342) // Light Green
            StrengthLevel.VERY_STRONG -> Color(0xFF43A047) // Green
        },
        animationSpec = tween(300),
        label = "strengthColor"
    )
    
    val progress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(300),
        label = "progress"
    )
    
    val strengthLabel = when (level) {
        StrengthLevel.NONE -> "Enter a password"
        StrengthLevel.VERY_WEAK -> "Very Weak"
        StrengthLevel.WEAK -> "Weak"
        StrengthLevel.FAIR -> "Fair"
        StrengthLevel.STRONG -> "Strong"
        StrengthLevel.VERY_STRONG -> "Very Strong"
    }
    
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
                text = "Strength",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Text(
                text = strengthLabel,
                style = OmniToolTheme.typography.label,
                color = strengthColor
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(OmniToolTheme.shapes.small)
                .background(OmniToolTheme.colors.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(OmniToolTheme.shapes.small)
                    .background(strengthColor)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "$score / 100",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
    }
}

@Composable
private fun EntropyDisplay(entropyBits: Double) {
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
                text = "Entropy",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            Text(
                text = "Measures password randomness",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
        Text(
            text = "%.1f bits".format(entropyBits),
            style = OmniToolTheme.typography.header,
            color = OmniToolTheme.colors.softCoral
        )
    }
}

@Composable
private fun FeedbackList(feedback: List<StrengthFeedback>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Analysis",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        // Positive feedback
        val positiveFeedback = feedback.filter { it.isPositive }
        val negativeFeedback = feedback.filter { !it.isPositive }
        
        positiveFeedback.forEach { item ->
            FeedbackItem(
                feedback = item,
                isPositive = true
            )
        }
        
        negativeFeedback.forEach { item ->
            FeedbackItem(
                feedback = item,
                isPositive = false
            )
        }
    }
}

@Composable
private fun FeedbackItem(
    feedback: StrengthFeedback,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(
                if (isPositive) OmniToolTheme.colors.primaryLime.copy(alpha = 0.1f)
                else OmniToolTheme.colors.softCoral.copy(alpha = 0.1f)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isPositive) OmniToolIcons.Check else OmniToolIcons.Close,
            contentDescription = null,
            tint = if (isPositive) OmniToolTheme.colors.primaryLime 
                   else OmniToolTheme.colors.softCoral,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = feedback.message,
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textPrimary
        )
    }
}
