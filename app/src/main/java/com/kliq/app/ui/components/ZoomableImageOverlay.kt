package com.kliq.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import kotlin.math.roundToInt

/**
 * Minimum zoom scale factor.
 */
const val MIN_ZOOM_SCALE = 1.0f

/**
 * Maximum zoom scale factor.
 */
const val MAX_ZOOM_SCALE = 4.0f

/**
 * Default double-tap target scale factor.
 */
const val DOUBLE_TAP_ZOOM_SCALE = 2.5f

/**
 * Calculates clamped translation offsets based on current scale factor and container bounds.
 * Prevents zoomed images from panning outside container edges.
 */
fun calculateClampedOffset(
    scale: Float,
    rawOffsetX: Float,
    rawOffsetY: Float,
    containerWidth: Float,
    containerHeight: Float
): Pair<Float, Float> {
    if (scale <= MIN_ZOOM_SCALE) {
        return Pair(0f, 0f)
    }
    val maxOffsetX = ((containerWidth * (scale - 1f)) / 2f).coerceAtLeast(0f)
    val maxOffsetY = ((containerHeight * (scale - 1f)) / 2f).coerceAtLeast(0f)
    val clampedX = rawOffsetX.coerceIn(-maxOffsetX, maxOffsetX)
    val clampedY = rawOffsetY.coerceIn(-maxOffsetY, maxOffsetY)
    return Pair(clampedX, clampedY)
}

/**
 * Fullscreen high-contrast modal dialog supporting pinch-to-zoom, pan drag with boundary limits,
 * double-tap reset, and smooth spring physics animations.
 */
@Composable
fun ZoomableImageOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    imageUrl: String? = null,
    initials: String? = null,
    scaleState: Float = 1.0f,
    offsetXState: Float = 0.0f,
    offsetYState: Float = 0.0f,
    onZoomStateChanged: ((scale: Float, offsetX: Float, offsetY: Float) -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    if (!isVisible) return

    Dialog(
        onDismissRequest = {
            onZoomStateChanged?.invoke(1.0f, 0.0f, 0.0f)
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false
        )
    ) {
        var scale by remember { mutableFloatStateOf(scaleState) }
        var offsetX by remember { mutableFloatStateOf(offsetXState) }
        var offsetY by remember { mutableFloatStateOf(offsetYState) }

        LaunchedEffect(scaleState, offsetXState, offsetYState) {
            scale = scaleState
            offsetX = offsetXState
            offsetY = offsetYState
        }

        val springSpec = remember {
            spring<Float>(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }

        val animatedScale by animateFloatAsState(
            targetValue = scale,
            animationSpec = springSpec,
            label = "zoom_scale_anim"
        )
        val animatedOffsetX by animateFloatAsState(
            targetValue = offsetX,
            animationSpec = springSpec,
            label = "zoom_offset_x_anim"
        )
        val animatedOffsetY by animateFloatAsState(
            targetValue = offsetY,
            animationSpec = springSpec,
            label = "zoom_offset_y_anim"
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground.copy(alpha = 0.95f))
                .statusBarsPadding()
        ) {
            val density = LocalDensity.current
            val containerWidthPx = with(density) { maxWidth.toPx() }
            val containerHeightPx = with(density) { maxHeight.toPx() }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1.05f) {
                                    scale = 1.0f
                                    offsetX = 0.0f
                                    offsetY = 0.0f
                                } else {
                                    scale = DOUBLE_TAP_ZOOM_SCALE
                                    val (clampedX, clampedY) = calculateClampedOffset(
                                        scale = scale,
                                        rawOffsetX = 0.0f,
                                        rawOffsetY = 0.0f,
                                        containerWidth = containerWidthPx,
                                        containerHeight = containerHeightPx
                                    )
                                    offsetX = clampedX
                                    offsetY = clampedY
                                }
                                onZoomStateChanged?.invoke(scale, offsetX, offsetY)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(MIN_ZOOM_SCALE, MAX_ZOOM_SCALE)
                            val rawX = if (newScale > 1.0f) offsetX + pan.x else 0.0f
                            val rawY = if (newScale > 1.0f) offsetY + pan.y else 0.0f

                            val (clampedX, clampedY) = calculateClampedOffset(
                                scale = newScale,
                                rawOffsetX = rawX,
                                rawOffsetY = rawY,
                                containerWidth = containerWidthPx,
                                containerHeight = containerHeightPx
                            )

                            scale = newScale
                            offsetX = clampedX
                            offsetY = clampedY
                            onZoomStateChanged?.invoke(scale, offsetX, offsetY)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                            translationX = animatedOffsetX
                            translationY = animatedOffsetY
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (content != null) {
                        content()
                    } else if (!imageUrl.isNullOrBlank() && imageUrl != "null") {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Vollbild Profilbild Zoom",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentScale = ContentScale.Fit,
                            error = rememberVectorPainter(Icons.Default.Person)
                        )
                    } else if (!initials.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(CircleShape)
                                .background(DarkSurface)
                                .border(3.dp, PurplePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials.take(2).uppercase(),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 90.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurplePrimaryLight
                                )
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PurplePrimaryLight,
                            modifier = Modifier.size(160.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = scale > 1.05f,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            ) {
                val roundedScale = (scale * 10f).roundToInt() / 10f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface.copy(alpha = 0.85f))
                        .border(1.dp, PurplePrimary, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${roundedScale}x",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = PurplePrimaryLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            IconButton(
                onClick = {
                    onZoomStateChanged?.invoke(1.0f, 0.0f, 0.0f)
                    onDismiss()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkSurface.copy(alpha = 0.8f))
                    .border(1.dp, PurplePrimary.copy(alpha = 0.6f), CircleShape)
                    .semantics { contentDescription = "Schließen" }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
