package us.cboyd.shared

/**
 * Basic Geometry functions
 *
 * @author Christopher Boyd
 */

object Geometry {
    fun rad2deg(angle: Double): Double {
        return angle * 180.0 / Math.PI
    }

    fun deg2rad(angle: Double): Double {
        return angle * Math.PI / 180.0
    }

    fun dist2(spanX: Float, spanY: Float): Double {
        return Math.sqrt((spanX * spanX + spanY * spanY).toDouble())
    }
}