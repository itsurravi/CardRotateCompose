package com.ravikantsharma.cardrotatecompose

import android.os.SystemClock
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    back: @Composable () -> Unit = {},
    front: @Composable () -> Unit = {},
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 2400,
            easing = FastOutSlowInEasing,
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(cardFace) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation.value <= 90f) {
                Box(modifier = Modifier.fillMaxSize()) {
                    front()
                }
            } else {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    }) {
                    back()
                }
            }
        }
    }
}

@Composable
fun debounceCompose(debounceTime: Long = 500L, action: () -> Unit): () -> Unit {
    val debouncedAction = remember(action) {
        var lastClickTime: Long = 0
        {
            if (SystemClock.elapsedRealtime() - lastClickTime >= debounceTime) {
                lastClickTime = SystemClock.elapsedRealtime()
                action()
            }
        }
    }
    return debouncedAction
}

@Composable
fun <T> debounceCompose(debounceTime: Long = 500L, action: (T) -> Unit): (T) -> Unit {
    val debouncedAction = remember(action) {
        var lastClickTime: Long = 0
        val callback: (T) -> Unit = {
            if (SystemClock.elapsedRealtime() - lastClickTime >= debounceTime) {
                lastClickTime = SystemClock.elapsedRealtime()
                action(it)
            }
        }
        callback
    }
    return debouncedAction
}