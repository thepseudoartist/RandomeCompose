package club.cred.randomecompose.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import club.cred.randomecompose.ScreenCenter
import club.cred.randomecompose.SensorData
import club.cred.randomecompose.screenHeight
import club.cred.randomecompose.screenWidth
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

val list = mutableListOf<Pair<Offset, Offset>>()

@Composable
fun FancyGrid(modifier: Modifier = Modifier, data: SensorData? = null) {
    val correction by derivedStateOf { (data ?: SensorData(0f, 0f, 0f)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = screenWidth() / 1.5f
            val height = screenHeight() / 1.4f
            val boxCount = 15

            val lineSpacing = min(width, height) / boxCount

            translate(left = correction.roll / 10f, top = correction.pitch / 10f) {
                // Center Grid
                drawGrid(
                    minBoxCount = boxCount,
                    offset = Offset.ScreenCenter - Offset(width / 2, height / 2),
                    size = Size(width, height),
                    lightCorrection = Offset(-correction.roll, correction.pitch),
                )

                // Top Grid
                rotate(180f) {
                    drawTrapeziumGrid(
                        minBoxCount = boxCount,
                        topOffset = Offset.ScreenCenter + Offset(-width / 2, height / 2),
                        bottomOffset = Offset(0f, screenHeight()),
                        widthTop = width,
                        widthBottom = screenWidth(),
                        lightCorrection = -Offset(-correction.roll, correction.pitch),
                    )
                }

                // Bottom Grid
                drawTrapeziumGrid(
                    minBoxCount = boxCount,
                    topOffset = Offset.ScreenCenter + Offset(-width / 2, height / 2),
                    bottomOffset = Offset(0f, screenHeight()),
                    widthTop = width,
                    widthBottom = screenWidth(),
                    lightCorrection = Offset(-correction.roll, correction.pitch),
                )

                // Left Grid
                drawTrapeziumGrid2(
                    minBoxCount = (height / lineSpacing).roundToInt(),
                    rightOffset = Offset.ScreenCenter - Offset(width / 2, height / 2),
                    leftOffset = Offset.Zero + Offset(-correction.roll / 10f, 0f),
                    heightLeft = height,
                    heightRight = screenHeight(),
                    lightCorrection = Offset(-correction.roll, correction.pitch),
                )

                // Right Grid
                rotate(180f) {
                    drawTrapeziumGrid2(
                        minBoxCount = (height / lineSpacing).roundToInt(),
                        rightOffset = Offset.ScreenCenter - Offset(width / 2, height / 2),
                        leftOffset = Offset.Zero + Offset(correction.roll / 10f, 0f),
                        heightLeft = height,
                        heightRight = screenHeight(),
                        lightCorrection = -Offset(-correction.roll, correction.pitch),
                    )
                }

            }

            val centerBoxSize = Size(screenWidth() / 1.2f - 20f.dp.toPx(), screenWidth() / 1.2f - 20f.dp.toPx())
            val topLeft = center - Offset(screenWidth() / 2.4f - 10f.dp.toPx(), screenWidth() / 2.4f - 10f.dp.toPx())

            translate(top = topLeft.y, left = topLeft.x) {
                drawRect(
                    color = Color.White,
                    topLeft = Offset.Zero,
                    size = centerBoxSize,
                    style = Stroke(width = 1.4f.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        list.clear()
    }
}

private fun DrawScope.drawTrapeziumGrid2(
    minBoxCount: Int,
    rightOffset: Offset,
    leftOffset: Offset,
    heightLeft: Float,
    heightRight: Float,
    lightCorrection: Offset,
) {
    val lineSpacingTop = heightLeft / minBoxCount
    val lineSpacingBottom = heightRight / minBoxCount

    val rtlCount = (heightLeft / lineSpacingTop).roundToInt()

    val brush = Brush.radialGradient(
        colors = listOf(Color(0xFFC7C5BF), Color(0xFF484942), Color(0xFF100C08)),
        radius = this.size.width,
        center = this.size.center + lightCorrection
    )

    for (line in 0..rtlCount) {
        drawLine(
            brush = brush,
            start = Offset(0f, line * lineSpacingTop) + rightOffset,
            end = Offset(0f, line * lineSpacingBottom) + leftOffset,
            strokeWidth = 1.dp.toPx(),
        )
    }

    list.forEachIndexed { index, line ->
        drawLine(
            brush = brush,
            start = line.first,
            end = line.second,
            strokeWidth = 1.dp.toPx(),
        )
    }
}

private fun DrawScope.drawTrapeziumGrid(
    minBoxCount: Int,
    topOffset: Offset,
    bottomOffset: Offset,
    widthTop: Float,
    widthBottom: Float,
    lightCorrection: Offset,
) {
    val lineSpacingTop = widthTop / minBoxCount
    val lineSpacingBottom = widthBottom / minBoxCount

    val verticalBoxCount = (widthTop / lineSpacingTop).roundToInt()
    val horizontalBoxCount = (abs(topOffset.y - bottomOffset.y) / minBoxCount).roundToInt()

    val brush = Brush.radialGradient(
        colors = listOf(Color(0xFFC7C5BF), Color(0xFF484942), Color(0xFF100C08)),
        radius = size.width,
        center = this.size.center + lightCorrection
    )

    for (line in 0..verticalBoxCount) {
        drawLine(
            brush = brush,
            start = Offset(line * lineSpacingTop, 0f) + topOffset,
            end = Offset(line * lineSpacingBottom, 0f) + bottomOffset,
            strokeWidth = 1.dp.toPx(),
        )
    }

    val y1 = abs(bottomOffset.y - topOffset.y)
    val x1 = abs(bottomOffset.x - topOffset.x)

    for (line in 0..horizontalBoxCount) {
        val size = line * lineSpacingBottom

        val startOffset = Offset(x1 * (y1 - size) / y1, size + topOffset.y)
        val endOffset = Offset(widthBottom - (x1 * (y1 - size) / y1), size + topOffset.y)

        // for horizontal lines in LR grids
        list += Pair(startOffset, Offset(startOffset.x, screenHeight() - startOffset.y))

        drawLine(
            brush = brush,
            start = startOffset,
            end = endOffset,
            strokeWidth = 1.dp.toPx(),
        )
    }
}

private fun DrawScope.drawGrid(
    minBoxCount: Int,
    offset: Offset,
    size: Size,
    lightCorrection: Offset = Offset.Zero,
) {
    // Making sure that boxes are always square shaped
    val lineSpacing = min(size.height, size.width) / minBoxCount

    val verticalBoxCount = (size.width / lineSpacing).roundToInt()
    val horizontalBoxCount = (size.height / lineSpacing).roundToInt()

    val brush = Brush.radialGradient(
        colors = listOf(Color(0xFF484942), Color(0xFF100C08), Color(0xFF100C08)),
        radius = size.height,
        center = this.size.center + lightCorrection
    )

    for (line in 0..verticalBoxCount) {
        drawLine(
            brush = brush,
            start = Offset(line * lineSpacing, 0f) + offset,
            end = Offset(line * lineSpacing, size.height) + offset,
            strokeWidth = 1.dp.toPx(),
        )
    }

    for (line in 0..horizontalBoxCount) {
        drawLine(
            brush = brush,
            start = Offset(0f, line * lineSpacing) + offset,
            end = Offset(size.width, line * lineSpacing) + offset,
            strokeWidth = 1.dp.toPx(),
        )
    }
}
