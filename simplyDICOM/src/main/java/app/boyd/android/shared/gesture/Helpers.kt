package app.boyd.android.shared.gesture

import android.view.MotionEvent
import app.boyd.shared.Geometry
import kotlin.math.abs

/** Scroll Mode Values  */
enum class ScrollMode {
    NO_SCROLL, MOVE, SCALE, MOVE2
}

/**
 * Calculates the average X & Y positions, returns the [MotionEvent]'s pointer count.
 */
fun MotionEvent.calcAvgPosition(): Pair<Double, Double> {
    val numPointers = this.pointerCount
    // Store initial positions
    var x = 0.0
    var y = 0.0
    for (i in 0 until numPointers) {
        x += this.getX(i)
        y += this.getY(i)
    }
    val dPointers = numPointers.toDouble()
    x /= dPointers
    y /= dPointers
    return Pair(x, y)
}


/** Determine if angle is small enough  */
fun Double.isAngleSmall(): Boolean {
    val angleDeg = Geometry.rad2deg(abs(this))
    // The sweet spot seems to be around 20 degrees:
    return 0.0 <= angleDeg && angleDeg < 20.0 || 160.0 < angleDeg && angleDeg <= 180.0
}