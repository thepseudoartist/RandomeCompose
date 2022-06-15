package club.cred.randomecompose

import android.content.res.Resources
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

fun screenWidth(): Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()

fun screenHeight(): Float = Resources.getSystem().displayMetrics.heightPixels.toFloat()

fun Offset(value: Float) = Offset(value, value)

val Size.Companion.Max: Size
    get() = Size(screenWidth(), screenHeight())

val Offset.Companion.ScreenCenter: Offset
    get() = Offset(screenWidth() / 2, screenHeight() / 2)

fun Size.toOffset() = Offset(width, height)

fun Offset.toSize() = Size(x, y)