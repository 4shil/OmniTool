package com.omnitool.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.animation.AnimationDuration
import com.omnitool.ui.animation.slideInFromBottom
import com.omnitool.ui.animation.slideOutToBottom
import com.omnitool.ui.icons.OmniToolIcons
import kotlinx.coroutines.delay

/**
 * Toast message component for feedback
 * Slides in from bottom, auto-dismisses
 */
@Composable
fun OmniToolToast(
    message: String,
    type: ToastType = ToastType.Info,
    visible: Boolean,
    onDismiss: () -> Unit,
    durationMs: Long = 2500L
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMs)
            onDismiss()
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInFromBottom(),
        exit = slideOutToBottom()
    ) {
        val (icon, color) = when (type) {
            ToastType.Success -> OmniToolIcons.Success to OmniToolTheme.colors.primaryLime
            ToastType.Error -> OmniToolIcons.Error to OmniToolTheme.colors.softCoral
            ToastType.Warning -> OmniToolIcons.Warning to OmniToolTheme.colors.warmYellow
            ToastType.Info -> OmniToolIcons.Info to OmniToolTheme.colors.skyBlue
        }
        
        Box(
            modifier = Modifier
                .padding(OmniToolTheme.spacing.sm)
                .clip(OmniToolTheme.shapes.large)
                .background(OmniToolTheme.colors.elevatedSurface)
                .padding(
                    horizontal = OmniToolTheme.spacing.sm,
                    vertical = OmniToolTheme.spacing.xs
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.xs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = type.name,
                    tint = color,
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
}

enum class ToastType {
    Success,
    Error,
    Warning,
    Info
}

/**
 * Toast state manager
 */
@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

class ToastState {
    var isVisible by mutableStateOf(false)
        private set
    
    var message by mutableStateOf("")
        private set
    
    var type by mutableStateOf(ToastType.Info)
        private set
    
    fun show(message: String, type: ToastType = ToastType.Info) {
        this.message = message
        this.type = type
        this.isVisible = true
    }
    
    fun dismiss() {
        isVisible = false
    }
}
