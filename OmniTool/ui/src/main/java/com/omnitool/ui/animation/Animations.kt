package com.omnitool.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

/**
 * OmniTool Interaction & Motion System
 * 
 * Allowed motions (from UI/UX spec):
 * - fade
 * - slide
 * - expand
 * - glow pulse
 * 
 * Forbidden motions:
 * - bounce
 * - spring physics
 * - overshoot
 * - cartoon motion
 * - playful easing
 * 
 * All animations < 200ms
 */

// Animation durations (all < 200ms as per spec)
object AnimationDuration {
    const val Instant = 50
    const val Fast = 100
    const val Standard = 150
    const val Slow = 200
}

// Easing curves (professional, no bounce/spring)
object AnimationEasing {
    val Standard = FastOutSlowInEasing
    val Enter = FastOutLinearInEasing
    val Exit = LinearOutSlowInEasing
}

/**
 * Press scale animation (0.97 scale on tap)
 */
@Composable
fun Modifier.pressScale(
    enabled: Boolean = true,
    targetScale: Float = 0.97f,
    onPress: () -> Unit = {}
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) targetScale else 1f,
        animationSpec = tween(
            durationMillis = AnimationDuration.Fast,
            easing = AnimationEasing.Standard
        ),
        label = "press_scale"
    )
    
    this
        .scale(scale)
        .pointerInput(enabled) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                    onPress()
                }
            )
        }
}

/**
 * Shake animation for errors
 */
@Composable
fun Modifier.shakeOnError(
    trigger: Boolean,
    onAnimationEnd: () -> Unit = {}
): Modifier = composed {
    val offsetX by animateFloatAsState(
        targetValue = if (trigger) 0f else 0f,
        animationSpec = if (trigger) {
            keyframes {
                durationMillis = AnimationDuration.Standard
                0f at 0
                -8f at 25
                8f at 50
                -6f at 75
                6f at 100
                -4f at 125
                0f at AnimationDuration.Standard
            }
        } else {
            snap()
        },
        finishedListener = { onAnimationEnd() },
        label = "shake"
    )
    
    this.graphicsLayer { translationX = offsetX }
}

/**
 * Glow pulse animation for success
 */
@Composable
fun rememberGlowPulseState(): GlowPulseState {
    return remember { GlowPulseState() }
}

class GlowPulseState {
    var isAnimating by mutableStateOf(false)
        private set
    
    fun trigger() {
        isAnimating = true
    }
    
    fun reset() {
        isAnimating = false
    }
}

@Composable
fun Modifier.glowPulse(
    state: GlowPulseState,
    glowColor: androidx.compose.ui.graphics.Color
): Modifier = composed {
    val alpha by animateFloatAsState(
        targetValue = if (state.isAnimating) 0f else 0f,
        animationSpec = if (state.isAnimating) {
            keyframes {
                durationMillis = AnimationDuration.Slow
                0f at 0
                0.5f at AnimationDuration.Fast
                0f at AnimationDuration.Slow
            }
        } else {
            snap()
        },
        finishedListener = { state.reset() },
        label = "glow_pulse"
    )
    
    // Simple alpha overlay effect
    this.graphicsLayer { this.alpha = 1f - (alpha * 0.3f) }
}

/**
 * Fade enter/exit transitions
 */
fun fadeEnterTransition() = fadeIn(
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Enter
    )
)

fun fadeExitTransition() = fadeOut(
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Exit
    )
)

/**
 * Slide enter/exit transitions
 */
fun slideInFromBottom() = slideInVertically(
    initialOffsetY = { it / 4 },
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Standard
    )
) + fadeEnterTransition()

fun slideOutToBottom() = slideOutVertically(
    targetOffsetY = { it / 4 },
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Standard
    )
) + fadeExitTransition()

fun slideInFromEnd() = slideInHorizontally(
    initialOffsetX = { it / 4 },
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Standard
    )
) + fadeEnterTransition()

fun slideOutToEnd() = slideOutHorizontally(
    targetOffsetX = { it / 4 },
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Standard
    )
) + fadeExitTransition()

/**
 * Expand animation for accordions/cards
 */
fun expandVertically() = androidx.compose.animation.expandVertically(
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Standard
    )
) + fadeEnterTransition()

fun shrinkVertically() = androidx.compose.animation.shrinkVertically(
    animationSpec = tween(
        durationMillis = AnimationDuration.Standard,
        easing = AnimationEasing.Standard
    )
) + fadeExitTransition()
