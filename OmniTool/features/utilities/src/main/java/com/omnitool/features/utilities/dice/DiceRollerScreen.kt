package com.omnitool.features.utilities.dice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Dice Roller Screen
 * 
 * Features:
 * - Dice type selector
 * - Count slider
 * - Animated results
 * - Roll history
 */
@Composable
fun DiceRollerScreen(
    onBack: () -> Unit,
    viewModel: DiceRollerViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Dice Roller",
        toolIcon = OmniToolIcons.Dice,
        accentColor = OmniToolTheme.colors.softCoral,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Dice type selector
                DiceTypeSelector(
                    selected = viewModel.selectedDice,
                    onSelect = { viewModel.selectDice(it) }
                )
                
                // Count slider
                DiceCountSlider(
                    count = viewModel.diceCount,
                    onCountChange = { viewModel.setDiceCount(it) }
                )
            }
        },
        outputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Results display
                ResultsDisplay(
                    results = viewModel.results,
                    total = viewModel.total,
                    isRolling = viewModel.isRolling
                )
                
                // History
                if (viewModel.history.isNotEmpty()) {
                    HistorySection(
                        history = viewModel.history,
                        onClear = { viewModel.clearHistory() }
                    )
                }
            }
        },
        primaryActionLabel = "Roll",
        onPrimaryAction = { viewModel.roll() },
        secondaryActionLabel = if (viewModel.results.isNotEmpty()) "Clear" else null,
        onSecondaryAction = if (viewModel.results.isNotEmpty()) {
            { viewModel.clearResults() }
        } else null
    )
}

@Composable
private fun DiceTypeSelector(
    selected: DiceType,
    onSelect: (DiceType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Text(
            text = "Select Dice",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DiceType.values().toList()) { dice ->
                val isSelected = dice == selected
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(OmniToolTheme.shapes.small)
                        .background(
                            if (isSelected) OmniToolTheme.colors.softCoral.copy(alpha = 0.2f)
                            else OmniToolTheme.colors.surface
                        )
                        .clickable { onSelect(dice) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dice.displayName,
                        style = OmniToolTheme.typography.label,
                        color = if (isSelected) OmniToolTheme.colors.softCoral 
                                else OmniToolTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DiceCountSlider(
    count: Int,
    onCountChange: (Int) -> Unit
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
                text = "Number of dice",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = "${count}d",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.softCoral
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = count.toFloat(),
            onValueChange = { onCountChange(it.toInt()) },
            valueRange = 1f..20f,
            steps = 18,
            colors = SliderDefaults.colors(
                thumbColor = OmniToolTheme.colors.softCoral,
                activeTrackColor = OmniToolTheme.colors.softCoral,
                inactiveTrackColor = OmniToolTheme.colors.surface
            )
        )
    }
}

@Composable
private fun ResultsDisplay(
    results: List<Int>,
    total: Int,
    isRolling: Boolean
) {
    val rotation by rememberInfiniteTransition(label = "roll").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (results.isEmpty()) {
            Text(
                text = "🎲",
                fontSize = 48.sp,
                modifier = if (isRolling) Modifier.rotate(rotation) else Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap Roll to throw the dice",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted,
                textAlign = TextAlign.Center
            )
        } else {
            // Show individual results
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                results.take(10).forEach { result ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(44.dp)
                            .clip(OmniToolTheme.shapes.small)
                            .background(OmniToolTheme.colors.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = result.toString(),
                            style = OmniToolTheme.typography.header.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = OmniToolTheme.colors.softCoral
                        )
                    }
                }
            }
            
            if (results.size > 10) {
                Text(
                    text = "+${results.size - 10} more",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
            
            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
            
            // Total
            Text(
                text = "Total: $total",
                style = OmniToolTheme.typography.titleXL.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = OmniToolTheme.colors.primaryLime
            )
        }
    }
}

@Composable
private fun HistorySection(
    history: List<RollResult>,
    onClear: () -> Unit
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
                text = "History",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            
            Row(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .clickable(onClick = onClear)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = OmniToolIcons.Clear,
                    contentDescription = "Clear",
                    tint = OmniToolTheme.colors.textMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Clear",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        history.take(5).forEach { roll ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${roll.count}${roll.diceType.displayName}",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textSecondary
                )
                Text(
                    text = roll.results.joinToString(", "),
                    style = OmniToolTheme.typography.caption.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = OmniToolTheme.colors.textMuted,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    maxLines = 1
                )
                Text(
                    text = "= ${roll.total}",
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.softCoral
                )
            }
        }
    }
}
