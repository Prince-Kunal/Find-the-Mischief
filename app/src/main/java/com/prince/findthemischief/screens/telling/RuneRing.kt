package com.prince.findthemischief.screens.telling

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.prince.findthemischief.game.PlayerLabel
import com.prince.findthemischief.game.TimerUiState
import com.prince.findthemischief.ui.theme.DeepEmerald
import com.prince.findthemischief.ui.theme.MischiefGreen
import com.prince.findthemischief.ui.theme.NearBlack
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.Parchment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Up to this many players, names fit on the ring; beyond it the ring shows numbers. */
const val NAMES_ON_RING_MAX = 8

private val ChipMaxWidth = 84.dp
private val NodeSize = 36.dp

/**
 * The speaking order as a ring: first speaker at the top, then clockwise. Arrows between the
 * players show the direction. When the timer is on, a gold arc around the ring runs down.
 * [center] is drawn in the middle of the ring.
 */
@Composable
fun RuneRing(
    order: List<PlayerLabel>,
    timer: TimerUiState,
    modifier: Modifier = Modifier,
    center: @Composable () -> Unit,
) {
    val progress by animateFloatAsState(
        targetValue = timer.fraction,
        animationSpec = tween(durationMillis = 1_000, easing = LinearEasing),
        label = "timer arc",
    )
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val side = min(maxWidth, 340.dp)
        val namesOnRing = order.size <= NAMES_ON_RING_MAX
        val radius = side / 2 - if (namesOnRing) ChipMaxWidth / 2 else NodeSize / 2 + 4.dp
        Box(
            modifier = Modifier
                .size(side)
                .drawBehind { drawRing(radius.toPx(), order.size, timer.enabled, progress) },
            contentAlignment = Alignment.Center,
        ) {
            center()
            RingLayout(radius = radius, modifier = Modifier.size(side)) {
                order.forEachIndexed { position, player ->
                    if (namesOnRing) NameChip(player.name, isStarter = position == 0)
                    else NumberNode(position + 1, isStarter = position == 0)
                }
            }
        }
    }
}

/** Places its children evenly around a circle, the first at 12 o'clock, going clockwise. */
@Composable
private fun RingLayout(radius: Dp, modifier: Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(loose) }
        layout(constraints.maxWidth, constraints.maxHeight) {
            val cx = constraints.maxWidth / 2f
            val cy = constraints.maxHeight / 2f
            val r = radius.toPx()
            placeables.forEachIndexed { i, p ->
                val angle = -PI / 2 + 2 * PI * i / placeables.size
                p.place(
                    x = (cx + r * cos(angle) - p.width / 2f).toInt(),
                    y = (cy + r * sin(angle) - p.height / 2f).toInt(),
                )
            }
        }
    }
}

@Composable
private fun NameChip(name: String, isStarter: Boolean) {
    val shape = RoundedCornerShape(50)
    Text(
        text = name,
        style = MaterialTheme.typography.labelLarge,
        color = if (isStarter) NearBlack else Parchment,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .widthIn(max = ChipMaxWidth)
            .background(if (isStarter) OldGold else DeepEmerald, shape)
            .border(1.dp, if (isStarter) OldGold else OldGoldDim, shape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun NumberNode(number: Int, isStarter: Boolean) {
    Box(
        modifier = Modifier
            .size(NodeSize)
            .background(if (isStarter) OldGold else DeepEmerald, CircleShape)
            .border(1.dp, if (isStarter) OldGold else OldGoldDim, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$number",
            style = MaterialTheme.typography.titleSmall,
            color = if (isStarter) NearBlack else Parchment,
        )
    }
}

private fun DrawScope.drawRing(radius: Float, count: Int, timerOn: Boolean, progress: Float) {
    val topLeft = Offset(center.x - radius, center.y - radius)
    val ringSize = Size(radius * 2, radius * 2)
    drawCircle(OldGoldDim, radius, style = Stroke(width = 1.dp.toPx()))
    if (timerOn && progress > 0f) {
        drawArc(
            color = OldGold,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = topLeft,
            size = ringSize,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
        )
    }
    // A small arrow halfway between neighbours, pointing clockwise.
    if (count < 2) return
    val half = 4.dp.toPx()
    for (i in 0 until count) {
        val angle = -PI / 2 + 2 * PI * (i + 0.5) / count
        val at = Offset(center.x + radius * cos(angle).toFloat(), center.y + radius * sin(angle).toFloat())
        val forward = Offset(-sin(angle).toFloat(), cos(angle).toFloat())
        val side = Offset(cos(angle).toFloat(), sin(angle).toFloat())
        val arrow = Path().apply {
            val tip = at + forward * half
            val back = at - forward * half
            moveTo(tip.x, tip.y)
            lineTo((back + side * half).x, (back + side * half).y)
            lineTo((back - side * half).x, (back - side * half).y)
            close()
        }
        drawPath(arrow, MischiefGreen)
    }
}
