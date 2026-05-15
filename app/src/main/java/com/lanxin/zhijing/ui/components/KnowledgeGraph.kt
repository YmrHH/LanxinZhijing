package com.lanxin.zhijing.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lanxin.zhijing.data.KnowledgeNode
import com.lanxin.zhijing.ui.theme.AppColors
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private fun colorForNodeType(type: String): Color = when (type) {
    "good" -> AppColors.green
    "medium" -> AppColors.yellow
    "focus" -> AppColors.primary
    "weak" -> AppColors.red
    else -> AppColors.textSecondary
}

@Composable
fun KnowledgeGraph(
    nodes: List<KnowledgeNode>,
    graphEdges: List<Pair<String, String>>,
    centerTitle: String,
    centerProgress: Int,
    onCenterClick: () -> Unit,
    onSatelliteClick: (KnowledgeNode) -> Unit,
    clickableSatelliteIds: Set<String>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val cxPx = constraints.maxWidth / 2f
        val cyPx = constraints.maxHeight / 2f
        val radiusPx = min(cxPx, cyPx) * 0.42f

        fun posForIndex(index: Int, total: Int): Offset {
            val angleDeg = -90f + index * 360f / total
            val rad = Math.toRadians(angleDeg.toDouble())
            return Offset(
                x = cxPx + (cos(rad) * radiusPx).toFloat(),
                y = cyPx + (sin(rad) * radiusPx).toFloat()
            )
        }

        val idToIndex = nodes.mapIndexed { index, node -> node.id to index }.toMap()
        val positions = nodes.mapIndexed { index, _ -> posForIndex(index, nodes.size) }

        fun positionForId(id: String): Offset? = when (id) {
            "derivative" -> Offset(cxPx, cyPx)
            else -> {
                val idx = idToIndex[id] ?: return null
                positions[idx]
            }
        }

        val centerSizePx = with(density) { 96.dp.toPx() }
        val satSizePx = with(density) { 68.dp.toPx() }

        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize()) {
                graphEdges.forEach { (from, to) ->
                    val a = positionForId(from) ?: return@forEach
                    val b = positionForId(to) ?: return@forEach
                    drawLine(
                        color = AppColors.border,
                        strokeWidth = 2.dp.toPx(),
                        start = a,
                        end = b
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(96.dp)
                    .clickable { onCenterClick() }
                    .background(AppColors.primary, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = centerTitle,
                        color = Color.White,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "$centerProgress%",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                }
            }

            nodes.forEachIndexed { index, node ->
                val p = positions[index]
                val clickable = node.id in clickableSatelliteIds
                val ox = (p.x - satSizePx / 2f).roundToInt()
                val oy = (p.y - satSizePx / 2f).roundToInt()
                Box(
                    modifier = Modifier
                        .offset { IntOffset(ox, oy) }
                        .size(68.dp)
                        .then(
                            if (clickable) Modifier.clickable { onSatelliteClick(node) }
                            else Modifier
                        )
                        .background(colorForNodeType(node.type), CircleShape)
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = node.label,
                            color = Color.White,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            lineHeight = 12.sp
                        )
                        Text(
                            text = "${node.progress}%",
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
