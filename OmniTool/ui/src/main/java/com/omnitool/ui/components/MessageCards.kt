package com.omnitool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons

/**
 * Error message card component
 * 
 * From UI/UX spec:
 * - Floating message cards for errors
 * - Title
 * - Clear explanation
 * - Fix suggestion
 * - Never use generic errors
 * - Teach the user
 */
@Composable
fun ErrorCard(
    title: String,
    explanation: String,
    suggestion: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.softCoral.copy(alpha = 0.1f))
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
            ) {
                Icon(
                    imageVector = OmniToolIcons.Error,
                    contentDescription = "Error",
                    tint = OmniToolTheme.colors.softCoral,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.softCoral
                )
            }
            
            Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xxs))
            
            Text(
                text = explanation,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textPrimary
            )
            
            if (suggestion != null) {
                Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xxs))
                Text(
                    text = "💡 $suggestion",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

/**
 * Success message card
 */
@Composable
fun SuccessCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.primaryLime.copy(alpha = 0.1f))
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
        ) {
            Icon(
                imageVector = OmniToolIcons.Success,
                contentDescription = "Success",
                tint = OmniToolTheme.colors.primaryLime,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = title,
                    style = OmniToolTheme.typography.label,
                    color = OmniToolTheme.colors.primaryLime
                )
                Text(
                    text = message,
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textPrimary
                )
            }
        }
    }
}

/**
 * Info/tip card
 */
@Composable
fun InfoCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.skyBlue.copy(alpha = 0.1f))
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
        ) {
            Icon(
                imageVector = OmniToolIcons.Info,
                contentDescription = "Info",
                tint = OmniToolTheme.colors.skyBlue,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textPrimary
            )
        }
    }
}
